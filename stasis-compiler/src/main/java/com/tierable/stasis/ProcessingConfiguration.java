package com.tierable.stasis;


import com.google.auto.common.SuperficialValidation;
import com.squareup.javapoet.TypeName;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.tierable.stasis.StasisProcessor.findEnclosingTypeElement;


/**
 * @author Aniruddh Fichadia
 * @date 2017-08-08
 */
public class ProcessingConfiguration {
    public final  Set<TypeElement>           classesForPreservation;
    private final Map<Element, Set<Element>> preservedMembersForClasses;
    private final Map<Element, TypeName>     memberPreservationStrategies;


    public ProcessingConfiguration(Set<TypeElement> classesForPreservation, Map<Element, Set<Element>> preservedMembersForClasses, Map<Element, TypeName> memberPreservationStrategies) {
        this.classesForPreservation = classesForPreservation;
        this.preservedMembersForClasses = preservedMembersForClasses;
        this.memberPreservationStrategies = memberPreservationStrategies;
    }


    public Set<Element> getPreservedMembersForClass(Element classForPreservation) {
        Set<Element> preservedMembers = preservedMembersForClasses.get(classForPreservation);
        if (preservedMembers == null) {
            preservedMembers = new HashSet<>();
        }

        return preservedMembers;
    }

    public Map<Element, TypeName> getPreservationStrategiesForClass(TypeElement classElement)
            throws IllegalArgumentException {
        if (classesForPreservation.contains(classElement)) {
            Set<Element> membersInClass = preservedMembersForClasses.get(classElement);

            Map<Element, TypeName> classMemberPreservationStrategies = new HashMap<>();
            for (Element member : membersInClass) {
                classMemberPreservationStrategies.put(
                        member, memberPreservationStrategies.get(member)
                );
            }

            return classMemberPreservationStrategies;
        }

        throw new IllegalArgumentException("Class has not been preserved");
    }


    @Override
    public String toString() {
        return "ProcessingConfiguration" +
                "\nclassesForPreservation=" + classesForPreservation +
                "\npreservedMembersForClasses=" + preservedMembersForClasses +
                "\nmemberPreservationStrategies=" + memberPreservationStrategies;
    }


    public static class Extractor {
        private final LinkedHashMap<Element, String> errors = new LinkedHashMap<>();
        private ProcessingConfiguration extractedConfiguration;


        public void extract(PreservationMappingConfiguration stasisMappingConfiguration, Set<? extends Element> annotatedElements) {
            Set<TypeElement> classesForPreservation = new HashSet<>();
            Map<Element, Set<Element>> preservedMembersForClasses = new HashMap<>();
            Map<Element, TypeName> memberPreservationStrategies = new HashMap<>();

            for (Element element : annotatedElements) {
                if (!SuperficialValidation.validateElement(element)) {
                    // Skip invalid elements
                    continue;
                }

                if (ElementKind.FIELD.equals(element.getKind())) {
                    Set<Modifier> modifiers = element.getModifiers();
                    if (modifiers.contains(Modifier.PRIVATE)) {
                        errors.put(element, String.format(Locale.ENGLISH,
                                                          "%s cannot be private as it cannot be accessed by generated code",
                                                          element));
                        continue;
                    }

                    TypeElement classForPreservation = findEnclosingTypeElement(element);
                    if (ElementKind.CLASS.equals(classForPreservation.getKind())) {
                        classesForPreservation.add(classForPreservation);

                        TypeName preservationStrategy = stasisMappingConfiguration.getPreservationStrategyForElement(
                                element
                        );

                        if (preservationStrategy == null) {
                            continue;
                        }

                        addPreservedMember(
                                preservedMembersForClasses, classForPreservation, element
                        );

                        addMemberPreservationStrategy(
                                memberPreservationStrategies, element, preservationStrategy
                        );
                    }
                }
            }

            extractedConfiguration = new ProcessingConfiguration(
                    classesForPreservation, preservedMembersForClasses, memberPreservationStrategies
            );
        }


        private void addPreservedMember(Map<Element, Set<Element>> preservedMembersForClasses, TypeElement classForPreservation, Element element) {
            Set<Element> preservedMembers = preservedMembersForClasses.get(
                    classForPreservation
            );
            if (preservedMembers == null) {
                preservedMembers = new HashSet<>();
                preservedMembersForClasses.put(classForPreservation, preservedMembers);
            }
            preservedMembers.add(element);
        }

        private void addMemberPreservationStrategy(Map<Element, TypeName> memberPreservationStrategies,
                                                   Element element, TypeName preservationStrategy) {
            memberPreservationStrategies.put(element, preservationStrategy);
        }


        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public LinkedHashMap<Element, String> getErrors() {
            return errors;
        }

        public ProcessingConfiguration getExtractedConfiguration() {
            return extractedConfiguration;
        }
    }
}
