package com.tierable.stasis;


import com.google.auto.common.SuperficialValidation;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.tierable.stasis.StasisProcessor.CLASS_NAME_PRESERVATION_STRATEGY;


/**
 * @author Aniruddh Fichadia
 * @date 2017-08-08
 */
public class PreservationMappingConfiguration {
    private static final ClassName CLASS_NAME_PRESERVATION_STRATEGY_AUTO_RESOLVE = ClassName.get(
            PreservationStrategyAutoResolve.class
    );
    private static final ClassName CLASS_NAME_OBJECT                             = ClassName.get(
            Object.class
    );

    private final TypeName                fallbackPreservationStrategy;
    private final Map<TypeName, TypeName> defaultStrategies;


    public PreservationMappingConfiguration(TypeName fallbackPreservationStrategy,
                                            Map<TypeName, TypeName> defaultStrategies) {
        this.fallbackPreservationStrategy = fallbackPreservationStrategy;
        this.defaultStrategies = defaultStrategies;
    }


    public TypeName getPreservationStrategyForElement(Element element) {
        Preserve elementAnnotation = element.getAnnotation(Preserve.class);

        if (!elementAnnotation.enabled()) { // Skip disabled annotations
            return null;
        }

        TypeName preservationStrategy;
        try {
            elementAnnotation.value();

            // Exception expected to be thrown here
            throw new NullPointerException("This shouldn't happen");
        } catch (MirroredTypeException e) {
            TypeName annotatedPreservationStrategy = TypeName.get(e.getTypeMirror());

            if (CLASS_NAME_PRESERVATION_STRATEGY_AUTO_RESOLVE.equals(
                    annotatedPreservationStrategy)) {
                TypeName elementTypeTypeName = TypeName.get(element.asType());
                preservationStrategy = defaultStrategies.get(elementTypeTypeName);

                if (preservationStrategy == null) {
                    preservationStrategy = fallbackPreservationStrategy;
                }
            } else {
                preservationStrategy = annotatedPreservationStrategy;
            }
        }

        return preservationStrategy;
    }


    @Override
    public String toString() {
        return "PreservationMappingConfiguration" +
                "\nfallbackPreservationStrategy=" + fallbackPreservationStrategy +
                "\ndefaultStrategies=" + defaultStrategies;
    }


    public static class Extractor {
        private final Elements                         elementUtils;
        private final Types                            typeUtils;
        private final LinkedHashMap<Element, String>   errors;
        private       PreservationMappingConfiguration extractedConfiguration;


        public Extractor(Elements elementUtils, Types typeUtils) {
            this.elementUtils = elementUtils;
            this.typeUtils = typeUtils;
            this.errors = new LinkedHashMap<>();
        }


        public void extract(RoundEnvironment env)
                throws RuntimeException {
            Set<? extends Element> defaultPreservationConfigurations = env.getElementsAnnotatedWith(
                    PreservationMapping.class
            );

            TypeName fallbackPreservationStrategy;
            Map<TypeName, TypeName> defaultStrategies;
            switch (defaultPreservationConfigurations.size()) {
                case 1:
                    TypeElement preservationConfigurationElement = (TypeElement) defaultPreservationConfigurations
                            .iterator()
                            .next();

                    if (SuperficialValidation.validateElement(preservationConfigurationElement)) {
                        if (ElementKind.INTERFACE.equals(
                                preservationConfigurationElement.getKind())) {
                            fallbackPreservationStrategy = getFallbackPreservationStrategy(
                                    preservationConfigurationElement
                            );
                            defaultStrategies = extractDefaultStrategies(
                                    preservationConfigurationElement
                            );

                            extractedConfiguration = new PreservationMappingConfiguration(
                                    fallbackPreservationStrategy, defaultStrategies
                            );
                        } else {
                            errors.put(
                                    preservationConfigurationElement,
                                    String.format(
                                            Locale.ENGLISH, "%s must be an interface",
                                            PreservationMapping.class.getSimpleName()
                                    )
                            );
                        }
                    } else {
                        errors.put(
                                preservationConfigurationElement,
                                String.format(
                                        Locale.ENGLISH, "%s is not a valid class",
                                        preservationConfigurationElement
                                )
                        );
                    }
                    break;
                case 0:
                    throw new RuntimeException(
                            String.format(
                                    Locale.ENGLISH, "No %s found",
                                    PreservationMapping.class.getSimpleName()
                            )
                    );
                default: // Any other size
                    for (Element preservationConfigElement : defaultPreservationConfigurations) {
                        errors.put(
                                preservationConfigElement,
                                String.format(
                                        Locale.ENGLISH,
                                        "Multiple %s's found. You can only define a single %s",
                                        PreservationMapping.class.getSimpleName(),
                                        PreservationMapping.class.getSimpleName()
                                )
                        );
                    }

            }
        }


        /**
         * @return A map where the keys are the preserved types, and the values are the corresponding {@link
         * PreservationStrategy}
         */
        private Map<TypeName, TypeName> extractDefaultStrategies(TypeElement element) {
            final TypeMirror preservationStrategyType = elementUtils.getTypeElement(
                    CLASS_NAME_PRESERVATION_STRATEGY.toString()
            ).asType();


            Map<TypeName, TypeName> defaultStrategies = new HashMap<>();

            // Method ordering ... should be ... parent classes first
            List<ExecutableElement> methods = getMethodsFromInterface(element);

            for (ExecutableElement method : methods) {
                TypeMirror returnType = method.getReturnType();

                if (!isAssignableWithErasure(returnType, preservationStrategyType)) {
                    errors.put(
                            method,
                            String.format(
                                    Locale.ENGLISH, "Mapping method must return a %s",
                                    CLASS_NAME_PRESERVATION_STRATEGY.simpleName()
                            )
                    );
                    continue;
                }

                TypeName returnTypeName = TypeName.get(returnType);
                List<? extends VariableElement> parameters = method.getParameters();
                for (VariableElement parameter : parameters) {
                    TypeName paramTypeName = TypeName.get(parameter.asType());
                    defaultStrategies.put(paramTypeName, returnTypeName);
                }
            }

            return defaultStrategies;
        }

        private List<ExecutableElement> getMethodsFromInterface(TypeElement element) {
            List<? extends Element> membersFromObject = elementUtils.getAllMembers(
                    elementUtils.getTypeElement(CLASS_NAME_OBJECT.toString())
            );
            List<? extends Element> membersFromElement = new ArrayList<>(
                    elementUtils.getAllMembers(element)
            );

            membersFromElement.removeAll(membersFromObject);

            return ElementFilter.methodsIn(membersFromElement);
        }

        private boolean isAssignableWithErasure(TypeMirror tested, TypeMirror from) {
            return typeUtils.isAssignable(typeUtils.erasure(tested), typeUtils.erasure(from));
        }

        private TypeName getFallbackPreservationStrategy(Element element) {
            PreservationMapping annotation = element.getAnnotation(
                    PreservationMapping.class
            );

            try {
                annotation.value();
            } catch (MirroredTypeException e) {
                return TypeName.get(e.getTypeMirror());
            }
            throw new NullPointerException("No fallback strategy defined.");
        }


        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public LinkedHashMap<Element, String> getErrors() {
            return errors;
        }

        public PreservationMappingConfiguration getExtractedConfiguration() {
            return extractedConfiguration;
        }
    }
}
