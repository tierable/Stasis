package com.tierable.stasis;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;

import static com.tierable.stasis.StasisProcessor.ANNOTATION_NAME_NULLABLE;
import static com.tierable.stasis.StasisProcessor.CLASS_NAME_STASIS_PRESERVATION_STRATEGY;


/**
 * @author Aniruddh Fichadia
 * @date 2017-07-30
 */
public class StatisPreservationStrategyClassBuilder {
    private static final String PARAMETER_NAME_PRESERVED = "preserved";
    private static final String METHOD_NAME_FREEZE       = "freeze";
    private static final String METHOD_NAME_UN_FREEZE    = "unFreeze";


    private final TypeSpec.Builder       generatedClassBuilder;
    private final ClassName              classForPreservationClassName;
    private final Set<Element>           preservedMembers;
    private final Map<Element, TypeName> memberPreservationStrategies;


    public StatisPreservationStrategyClassBuilder(Builder generatedClassBuilder,
                                                  ClassName classForPreservationClassName,
                                                  Set<Element> preservedMembers,
                                                  Map<Element, TypeName> memberPreservationStrategies) {
        this.generatedClassBuilder = generatedClassBuilder;
        this.classForPreservationClassName = classForPreservationClassName;
        this.preservedMembers = preservedMembers;
        this.memberPreservationStrategies = memberPreservationStrategies;
    }


    public StatisPreservationStrategyClassBuilder applyClassDefinitions() {
        generatedClassBuilder.addSuperinterface(
                ParameterizedTypeName.get(
                        CLASS_NAME_STASIS_PRESERVATION_STRATEGY,
                        classForPreservationClassName
                )
        );

        return this;
    }

    public StatisPreservationStrategyClassBuilder applyFields() {
        for (Element preservedMember : preservedMembers) {
            TypeName preservationStrategy = memberPreservationStrategies.get(preservedMember);

            String fieldNameFieldPreservationStrategy = getFieldNameForPreservationStrategy(
                    preservedMember
            );

            generatedClassBuilder.addField(
                    FieldSpec.builder(preservationStrategy, fieldNameFieldPreservationStrategy,
                                      Modifier.PRIVATE, Modifier.FINAL)
                             .initializer(CodeBlock.of("new $T()", preservationStrategy))
                             .build()
            );
        }

        return this;
    }

    public StatisPreservationStrategyClassBuilder applyMethods() {
        CodeBlock.Builder freezeCodeBuilder = CodeBlock.builder();
        CodeBlock.Builder unFreezeCodeBuilder = CodeBlock.builder();

        for (Element preservedMember : preservedMembers) {
            String fieldNameFieldPreservationStrategy = getFieldNameForPreservationStrategy(
                    preservedMember
            );

            boolean memberIsNullable = isNullable(preservedMember);

            if (memberIsNullable) {
                freezeCodeBuilder.beginControlFlow("if ($L.$L != null)", PARAMETER_NAME_PRESERVED,
                                                   preservedMember);
                unFreezeCodeBuilder.beginControlFlow("if ($L.$L != null)", PARAMETER_NAME_PRESERVED,
                                                     preservedMember);
            }

            freezeCodeBuilder.addStatement("$L.$L($L.$L)", fieldNameFieldPreservationStrategy,
                                           METHOD_NAME_FREEZE, PARAMETER_NAME_PRESERVED,
                                           preservedMember);
            unFreezeCodeBuilder.addStatement("$L.$L($L.$L)", fieldNameFieldPreservationStrategy,
                                             METHOD_NAME_UN_FREEZE, PARAMETER_NAME_PRESERVED,
                                             preservedMember);

            if (memberIsNullable) {
                freezeCodeBuilder.endControlFlow();
                unFreezeCodeBuilder.endControlFlow();
            }
        }

        generatedClassBuilder.addMethod(
                MethodSpec.methodBuilder(METHOD_NAME_FREEZE)
                          .addModifiers(Modifier.PUBLIC)
                          .addAnnotation(Override.class)
                          .addParameter(
                                  ParameterSpec.builder(classForPreservationClassName,
                                                        PARAMETER_NAME_PRESERVED)
                                               .build()
                          )
                          .addCode(freezeCodeBuilder.build())
                          .build()
        );

        generatedClassBuilder.addMethod(
                MethodSpec.methodBuilder(METHOD_NAME_UN_FREEZE)
                          .addModifiers(Modifier.PUBLIC)
                          .addAnnotation(Override.class)
                          .addParameter(
                                  ParameterSpec.builder(classForPreservationClassName,
                                                        PARAMETER_NAME_PRESERVED)
                                               .build()
                          )
                          .addCode(unFreezeCodeBuilder.build())
                          .build()
        );

        return this;
    }

    private boolean isNullable(Element element) {
        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            Name annotationName = annotationMirror.getAnnotationType().asElement().getSimpleName();
            if (ANNOTATION_NAME_NULLABLE.equalsIgnoreCase(annotationName.toString())) {
                return true;
            }
        }

        return false;
    }


    private String getFieldNameForPreservationStrategy(Element preservedMember) {
        return preservedMember.getSimpleName() + CLASS_NAME_STASIS_PRESERVATION_STRATEGY.simpleName();
    }
}
