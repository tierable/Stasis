package com.tierable.stasis;


import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import static com.google.auto.common.MoreElements.getPackage;


/**
 * @author Aniruddh Fichadia
 * @date 2017-07-27
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class StasisProcessor
        extends AbstractProcessor {
    private static final boolean IS_TEST = false;

    public static final ClassName CLASS_NAME_PRESERVATION_STRATEGY = ClassName.get(
            PreservationStrategy.class
    );


    private Filer    filer;
    private Elements elementUtils;
    private Types    typeUtils;


    //region Initialisation boilerplate
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();

        annotations.add(PreservationMapping.class);
        annotations.add(Preserve.class);

        return annotations;
    }
    //endregion


    @Override
    public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
        if (env.processingOver()) {
            return true;
        }

        Set<? extends Element> annotatedElements = env.getElementsAnnotatedWith(
                Preserve.class
        );
        if (annotatedElements.isEmpty()) {
            // Early exit if there are no elements, or if this is another annotation processing
            // pass without any annotated elements remaining
            return false;
        }


        // Extract mapping configuration
        PreservationMappingConfiguration.Extractor mappingConfigExtractor = new PreservationMappingConfiguration.Extractor(
                elementUtils, typeUtils
        );
        mappingConfigExtractor.extract(env);
        if (mappingConfigExtractor.hasErrors()) {
            for (Entry<Element, String> errorInfo : mappingConfigExtractor.getErrors().entrySet()) {
                error(errorInfo.getKey(), errorInfo.getValue());
            }

            return true;
        }
        PreservationMappingConfiguration stasisMappingConfiguration = mappingConfigExtractor
                .getExtractedConfiguration();


        // Extract processing configuration
        ProcessingConfiguration.Extractor processingConfigExtractor = new ProcessingConfiguration.Extractor();
        processingConfigExtractor.extract(stasisMappingConfiguration, annotatedElements);

        if (processingConfigExtractor.hasErrors()) {
            for (Entry<Element, String> errorInfo : processingConfigExtractor.getErrors()
                                                                             .entrySet()) {
                error(errorInfo.getKey(), errorInfo.getValue());
            }

            return true;
        }
        ProcessingConfiguration processingConfiguration = processingConfigExtractor
                .getExtractedConfiguration();


        if (IS_TEST) {
            System.out.println(stasisMappingConfiguration);
            System.out.println();
            System.out.println(processingConfiguration);
            System.out.println();
        }

        for (TypeElement classForPreservation : processingConfiguration.classesForPreservation) {
            TypeElement enclosingElement = findEnclosingTypeElement(classForPreservation);

            String packageName = getPackage(enclosingElement).getQualifiedName().toString();
            String className = enclosingElement.getQualifiedName()
                                               .toString()
                                               .substring(packageName.length() + 1);

            String sanitisedTargetClassName = className.replace('.', '$');

            ClassName generatedClassName = ClassName.get(
                    packageName,
                    CLASS_NAME_PRESERVATION_STRATEGY.simpleName() + sanitisedTargetClassName
            );
            Set<Element> preservedMembers = processingConfiguration.getPreservedMembersForClass(
                    classForPreservation
            );
            Map<Element, TypeName> preservationStrategiesForClass = processingConfiguration.getPreservationStrategiesForClass(
                    classForPreservation
            );

            if (IS_TEST) {
                System.out.println("classForPreservation: " + classForPreservation);
                System.out.println("generatedClassName: " + generatedClassName);
                System.out.println("preservedMembers: " + preservedMembers);
                System.out.println(
                        "preservationStrategiesForClass: " + preservationStrategiesForClass);
                System.out.println();
            }

            TypeSpec.Builder generatedClassBuilder = TypeSpec.classBuilder(generatedClassName)
                                                             .addModifiers(Modifier.PUBLIC);

            new PreservationStrategyClassBuilder(generatedClassBuilder, classForPreservation,
                                                 preservedMembers,
                                                 preservationStrategiesForClass)
                    .applyClassDefinitions()
                    .applyFields()
                    .applyMethods();

            try {
                JavaFile javaFile = JavaFile.builder(packageName, generatedClassBuilder.build())
                                            .addFileComment(
                                                    "This code was generated by a tool " +
                                                            "(StasisProcessor, not a human tool :D)"
                                            )
                                            .build();
                javaFile.writeTo(filer);

                if (IS_TEST) {
                    javaFile.writeTo(System.out);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }


    //region Util
    public static TypeElement findEnclosingTypeElement(Element e) {
        while (e != null && !(e instanceof TypeElement)) {
            e = e.getEnclosingElement();
        }
        return TypeElement.class.cast(e);
    }
    //endregion


    //region Logging
    private void error(Element element, String message, Object... args) {
        printMessage(Kind.ERROR, element, message, args);
    }

    private void note(Element element, String message, Object... args) {
        printMessage(Kind.NOTE, element, message, args);
    }

    private void warning(Element element, String message, Object... args) {
        printMessage(Kind.WARNING, element, message, args);
    }

    private void printMessage(Kind kind, Element element, String message, Object[] args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }

        processingEnv.getMessager().printMessage(kind, message, element);
    }
    //endregion
}