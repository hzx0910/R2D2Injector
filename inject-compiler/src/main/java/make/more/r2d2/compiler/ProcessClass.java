package make.more.r2d2.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

import make.more.r2d2.annotation.InjectSameId;
import make.more.r2d2.annotation.Injector;
import make.more.r2d2.annotation.OnClickSameId;

public class ProcessClass {
    private static final ClassName ONCLICK = ClassName.bestGuess("android.view.View.OnClickListener");
    private static final ClassName VIEW = ClassName.bestGuess("android.view.View");
    private static final ClassName ACTIVITY = ClassName.bestGuess("android.app.Activity");
    private static final ClassName DIALOG = ClassName.bestGuess("android.app.Dialog");
    private ClassName className;
    private List<VariableElement> fieldList = new ArrayList<>();
    private List<ExecutableElement> methodList = new ArrayList<>();

    public ProcessClass(String name) {
        this.className = ClassName.bestGuess(name);
        fieldList.clear();
        methodList.clear();
    }

    public JavaFile generateCode() {
        FieldSpec.Builder fieldTarget = FieldSpec.builder(className, "target", Modifier.PRIVATE);
        TypeVariableName typeT = TypeVariableName.get("T", VIEW);
        MethodSpec.Builder methodFindView = MethodSpec.methodBuilder("find")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.OBJECT, "source")
                .addParameter(TypeName.INT, "id")
                .beginControlFlow("if (source instanceof $T)", VIEW)
                .addStatement("return (T) (($T) source).findViewById(id)", VIEW)
                .nextControlFlow("else if (source instanceof $T) ", ACTIVITY)
                .addStatement("return (T) (($T) source).findViewById(id)", ACTIVITY)
                .nextControlFlow("else if (source instanceof $T) ", DIALOG)
                .addStatement("return (T) (($T) source).findViewById(id)", DIALOG)
                .endControlFlow()
                .addStatement("return null")
                .addTypeVariable(typeT)
                .returns(typeT);

        MethodSpec.Builder methodClick = MethodSpec.methodBuilder("onClick")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .addParameter(VIEW, "view");

        MethodSpec.Builder methodInject = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(className, "target")
                .addParameter(TypeName.OBJECT, "source")
                .addStatement("this.target=target");

        MethodSpec.Builder methodInjectSimple = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(className, "target")
                .addStatement("inject(target,target)");

        for (VariableElement field : fieldList) {
            InjectSameId annotation = field.getAnnotation(InjectSameId.class);
            TypeName type;
            String name;
            try {
                type = ClassName.get(annotation.value());
                name = annotation.value().getName();
            } catch (MirroredTypeException mte) {
                type = TypeName.get(mte.getTypeMirror());
                DeclaredType declaredType = (DeclaredType) mte.getTypeMirror();
                TypeElement typeElement = (TypeElement) declaredType.asElement();
                name = typeElement.getQualifiedName().toString();
            }
            if (name.endsWith("id")) {
                methodInject.addStatement("target.$N=find(source,$T.$N)"
                        , field.getSimpleName(), type, field.getSimpleName());
            } else {
                methodInject.addStatement("target.$N=find(source,$T.id.$N)"
                        , field.getSimpleName(), type, field.getSimpleName());
            }
        }

        if (methodList.size() > 0) {
            CodeBlock.Builder caseBlock = CodeBlock.builder()
                    .beginControlFlow("switch(view.getId())");
            for (ExecutableElement method : methodList) {
                caseBlock.add("case R.id.$N:\n", method.getSimpleName()).indent();
                List<? extends VariableElement> parameters = method.getParameters();
                if (parameters.size() == 0) {
                    caseBlock.addStatement("target.$N()", method.getSimpleName());
                } else if (parameters.size() == 1) {

                    caseBlock.addStatement("target.$N(($T)view)"
                            , method.getSimpleName()
                            , TypeName.get(parameters.get(0).asType()));
                }
                caseBlock.addStatement("break").unindent();
                OnClickSameId annotation = method.getAnnotation(OnClickSameId.class);
                TypeName type;
                String name;
                try {
                    type = ClassName.get(annotation.value());
                    name = annotation.value().getName();
                } catch (MirroredTypeException mte) {
                    type = TypeName.get(mte.getTypeMirror());
                    DeclaredType declaredType = (DeclaredType) mte.getTypeMirror();
                    TypeElement typeElement = (TypeElement) declaredType.asElement();
                    name = typeElement.getQualifiedName().toString();
                }
                if (name.endsWith("id")) {
                    methodInject.addStatement("find(source,$T.$N).setOnClickListener(this)"
                            , type, method.getSimpleName());
                } else {
                    methodInject.addStatement("find(source,$T.id.$N).setOnClickListener(this)"
                            , type, method.getSimpleName());
                }
            }
            caseBlock.endControlFlow();
            methodClick.addCode(caseBlock.build());
        }


        TypeSpec finderClass = TypeSpec.classBuilder(className.simpleName() + "Injector")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Injector.class), className))
                .addSuperinterface(ONCLICK)
                .addField(fieldTarget.build())
                .addMethod(methodInjectSimple.build())
                .addMethod(methodInject.build())
                .addMethod(methodClick.build())
                .addMethod(methodFindView.build())
                .build();
        return JavaFile.builder(className.packageName(), finderClass).build();
    }

    public void addField(VariableElement field) {
        fieldList.add(field);
    }

    public void addMethod(ExecutableElement method) {
        methodList.add(method);
    }

}
