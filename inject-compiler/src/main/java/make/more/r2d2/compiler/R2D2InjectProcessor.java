package make.more.r2d2.compiler;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import make.more.r2d2.annotation.InjectSameId;
import make.more.r2d2.annotation.OnClickSameId;

@AutoService(Processor.class)
public class R2D2InjectProcessor extends AbstractProcessor {

    private Filer filer;
    private Map<String, ProcessClass> processClasses = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(InjectSameId.class.getCanonicalName());
        annotations.add(OnClickSameId.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment environment) {
        processBindView(environment);
        processOnClick(environment);
        try {
            for (ProcessClass annotatedClass : processClasses.values()) {
                annotatedClass.generateCode().writeTo(filer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        processClasses.clear();
        return true;
    }

    private void processBindView(RoundEnvironment environment) {
        Set<? extends Element> elementsBindView = environment.getElementsAnnotatedWith(InjectSameId.class);
        for (Element element : elementsBindView) {
            ProcessClass annotatedClass = getAnnotatedClass(element);
            annotatedClass.addField((VariableElement) element);
        }

    }

    private void processOnClick(RoundEnvironment environment) {
        Set<? extends Element> elementsOnClick = environment.getElementsAnnotatedWith(OnClickSameId.class);
        for (Element element : elementsOnClick) {
            ProcessClass annotatedClass = getAnnotatedClass(element);
            annotatedClass.addMethod((ExecutableElement) element);
        }
    }

    private ProcessClass getAnnotatedClass(Element element) {
        String qualifiedName = ((TypeElement) element.getEnclosingElement()).getQualifiedName().toString();
        ProcessClass annotatedClass = processClasses.get(qualifiedName);
        if (annotatedClass == null) {
            annotatedClass = new ProcessClass(qualifiedName);
            processClasses.put(qualifiedName, annotatedClass);
        }
        return annotatedClass;
    }
}
