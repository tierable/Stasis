package com.tierable.stasis;


import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
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
    private static final boolean IS_TEST = true;

    public static final String PACKAGE_STASIS = "com.tierable.stasis";


    public static final ClassName CLASS_NAME_OBJECT                                    = ClassName.get(
            "java.lang", "Object"
    );
    public static final ClassName CLASS_NAME_STASIS_PRESERVATION_STRATEGY              = ClassName.get(
            PACKAGE_STASIS, "StasisPreservationStrategy"
    );
    public static final ClassName CLASS_NAME_STASIS_PRESERVATION_STRATEGY_AUTO_RESOLVE = ClassName.get(
            PACKAGE_STASIS, "StasisPreservationStrategyAutoResolve"
    );

    private Filer    filer;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        this.filer = processingEnv.getFiler();

        elementUtils = processingEnv.getElementUtils();
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

        annotations.add(StasisPreservationMapping.class);
        annotations.add(StasisPreserve.class);

        return annotations;
    }


    private Map<TypeName, TypeName> getDefaultStrategies(TypeElement element) {
        Map<TypeName, TypeName> defaultStrategies = new HashMap<>();

        List<ExecutableElement> methods = getMethodsFromInterface(element);

        for (ExecutableElement method : methods) {
            TypeName returnTypeName = TypeName.get(method.getReturnType());

//elementUtils.getTypeElement(CLASS_NAME_STASIS_PRESERVATION_STRATEGY.toString()).asType()
            // TODO: must return a StasisPreservationStrategy
//            Type.isAssignableFrom(returnTypeName, PRESERVATION_STRATEGY);

            List<? extends VariableElement> parameters = method.getParameters();
            for (VariableElement parameter : parameters) {
                TypeName paramTypeName = TypeName.get(parameter.asType());
                defaultStrategies.put(paramTypeName, returnTypeName);
            }
        }

        return defaultStrategies;
    }

    private List<ExecutableElement> getMethodsFromInterface(TypeElement element) {
        List<? extends Element> objectMembers = elementUtils.getAllMembers(
                elementUtils.getTypeElement(CLASS_NAME_OBJECT.toString())
        );
        List<? extends Element> allMembers = new ArrayList<>(
                elementUtils.getAllMembers(element)
        );

        allMembers.removeAll(objectMembers);

        return ElementFilter.methodsIn(allMembers);
    }

    private TypeName getFallbackPreservationStrategy(Element element) {
        StasisPreservationMapping annotation = element.getAnnotation(
                StasisPreservationMapping.class
        );

        try {
            annotation.value();
        } catch (MirroredTypeException e) {
            return TypeName.get(e.getTypeMirror());
        }
        throw new NullPointerException("No fallback strategy defined.");
    }

    @Override
    public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
//        env.

        Set<? extends Element> defaultPreservationConfigurations = env.getElementsAnnotatedWith(
                StasisPreservationMapping.class
        );

        TypeName fallbackPreservationStrategy = null;
        Map<TypeName, TypeName> defaultStrategies = null;
        switch (defaultPreservationConfigurations.size()) {
            case 1:
                TypeElement preservationConfigurationElement =
                        (TypeElement) defaultPreservationConfigurations.iterator().next();

                if (!SuperficialValidation.validateElement(preservationConfigurationElement)) {
                    // TODO boom
                }

                if (ElementKind.INTERFACE.equals(preservationConfigurationElement.getKind())) {
                    fallbackPreservationStrategy = getFallbackPreservationStrategy(
                            preservationConfigurationElement);
                    defaultStrategies = getDefaultStrategies(preservationConfigurationElement);
                } else {
                    throw new RuntimeException(
                            "StasisPreservationMapping must be an interface"
                    );
                }
                break;
            case 0:
//                throw new RuntimeException("No StasisPreservationMapping");
                break;
            default:
                throw new RuntimeException(
                        "Multiple StasisPreservationMapping's found. You can only define a single StasisPreservationMapping"
                );
        }


        if (IS_TEST) {
            System.out.println("Default strategies: " + defaultStrategies);
            System.out.println("Fallback strategy: " + fallbackPreservationStrategy);
        }

        Set<Element> classesForPreservation = new HashSet<>();
        Map<Element, Set<Element>> preservedMembersForClasses = new HashMap<>();
        Map<Element, TypeName> memberPreservationStrategies = new HashMap<>();

        for (Element element : env.getElementsAnnotatedWith(StasisPreserve.class)) {
            if (!SuperficialValidation.validateElement(element)) {
                continue;
            }

            if (ElementKind.FIELD.equals(element.getKind())) {
                StasisPreserve elementAnnotation = element.getAnnotation(StasisPreserve.class);

                TypeElement classForPreservation = findEnclosingTypeElement(element);
                if (ElementKind.CLASS.equals(classForPreservation.getKind())) {
                    classesForPreservation.add(classForPreservation);

                    if (!elementAnnotation.enabled()) {
                        continue;
                    }

                    Set<Element> preservedMembers = preservedMembersForClasses.get(
                            classForPreservation
                    );
                    if (preservedMembers == null) {
                        preservedMembers = new HashSet<>();
                        preservedMembersForClasses.put(classForPreservation, preservedMembers);
                    }
                    preservedMembers.add(element);


                    TypeName preservationStrategy;
                    try {
                        elementAnnotation.value();
                        throw new NullPointerException(
                                // TODO message
                                "stuff went boom"
                        );
                    } catch (MirroredTypeException e) {
                        TypeName annotatedPreservationStrategy = TypeName.get(e.getTypeMirror());

                        if (CLASS_NAME_STASIS_PRESERVATION_STRATEGY_AUTO_RESOLVE.equals(
                                annotatedPreservationStrategy)) {
                            TypeName elementTypeTypeName = TypeName.get(element.asType());
                            preservationStrategy = defaultStrategies.get(elementTypeTypeName);

                            if (preservationStrategy != null) {
                                if (IS_TEST) {
//                                    note(element, "For %s, using default strategy %s", element,
//                                         preservationStrategy);
                                }
                            } else {
                                preservationStrategy = fallbackPreservationStrategy;
                                if (IS_TEST) {
//                                    note(element, "For %s, default strategy not defined. " +
//                                                 "Using fallback preservation strategy %s", element,
//                                         preservationStrategy);
                                }
                            }
                        } else {
                            preservationStrategy = annotatedPreservationStrategy;

//                            if (IS_TEST) {
//                                note(element, "For %s, using annotated preservation strategy %s",
//                                     preservationStrategy);
//                            }
                        }
                    }

                    memberPreservationStrategies.put(element, preservationStrategy);
                }
            }
        }

        if (IS_TEST) {
            System.out.println("classesForPreservation: " + classesForPreservation);
            System.out.println("preservedMembersForClasses: " + preservedMembersForClasses);
            System.out.println("memberPreservationStrategies: " + memberPreservationStrategies);
        }

        for (Element classForPreservation : classesForPreservation) {
            TypeElement enclosingElement = findEnclosingTypeElement(classForPreservation);

            String packageName = getPackage(enclosingElement).getQualifiedName().toString();
            String className = enclosingElement.getQualifiedName()
                                               .toString()
                                               .substring(packageName.length() + 1);

            ClassName classForPreservationClassName = ClassName.get(packageName, className);
            String sanitisedTargetClassName = className.replace('.', '$');

            ClassName generatedClassName = ClassName.get(
                    packageName,
                    CLASS_NAME_STASIS_PRESERVATION_STRATEGY.simpleName() + sanitisedTargetClassName
            );

            Set<Element> preservedMembers = preservedMembersForClasses.get(classForPreservation);
            if (preservedMembers == null) {
                preservedMembers = new HashSet<>();
            }

            if (IS_TEST) {
                System.out.println("classForPreservation: " + classForPreservation);
                System.out.println("generatedClassName: " + generatedClassName);
                System.out.println("preservedMembers: " + preservedMembers);
            }

            TypeSpec.Builder generatedClassBuilder =
                    TypeSpec.classBuilder(generatedClassName)
                            .addModifiers(Modifier.PUBLIC);

            new StatisPreservationStrategyClassBuilder(generatedClassBuilder,
                                                       classForPreservationClassName,
                                                       preservedMembers,
                                                       memberPreservationStrategies)
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

        return false;
    }

    public static TypeElement findEnclosingTypeElement(Element e) {
        while (e != null && !(e instanceof TypeElement)) {
            e = e.getEnclosingElement();
        }
        return TypeElement.class.cast(e);
    }


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