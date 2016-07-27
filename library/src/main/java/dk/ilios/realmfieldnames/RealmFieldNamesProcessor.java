package dk.ilios.realmfieldnames;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * The Realm Field Names Generator is a processor that looks at all available Realm model classes
 * and create an companion class with easy, type-safe access to all field names.
 */
@SupportedAnnotationTypes({
        "io.realm.annotations.RealmClass"
})
public class RealmFieldNamesProcessor extends AbstractProcessor {

    private static final boolean CONSUME_ANNOTATIONS = false;

    private Set<ClassData> classes = new HashSet<>();
    private Types typeUtils;
    private Messager messager;
    private TypeMirror ignoreAnnotation;
//    private TypeMirror realmModelInterface;
//    private TypeMirror realmProxyInterface;
    private FileGenerator fileGenerator;
    private boolean done = false;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
        Elements elementUtils = processingEnv.getElementUtils();
        ignoreAnnotation = elementUtils.getTypeElement("io.realm.annotations.Ignore").asType();
//        realmModelInterface = elementUtils.getTypeElement("io.realm.RealmModel").asType();
//        realmProxyInterface = elementUtils.getTypeElement("io.realm.internal.RealmObjectProxy").asType();
        fileGenerator = new FileGenerator(processingEnv.getFiler());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (done) {
            return CONSUME_ANNOTATIONS;
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "Creating field classes");

        // Create all proxy classes
        TypeElement realmClassAnnotation = annotations.iterator().next();
        for (Element classElement : roundEnv.getElementsAnnotatedWith(realmClassAnnotation)) {
            ClassData classData = processClass((TypeElement) classElement);
            classes.add(classData);
        }

        done = fileGenerator.generate(classes);
        return CONSUME_ANNOTATIONS;
    }

    private ClassData processClass(TypeElement classElement) {
        String packageName = getPackageName(classElement);
        String className = classElement.getSimpleName().toString();
        ClassData data = new ClassData(packageName, className);

        // Find all appropriate fields
        for (Element element : classElement.getEnclosedElements()) {
            ElementKind elementKind = element.getKind();
            if (elementKind.equals(ElementKind.FIELD)) {
                VariableElement variableElement = (VariableElement) element;

                Set<Modifier> modifiers = variableElement.getModifiers();
                if (modifiers.contains(Modifier.STATIC)) {
                    continue; // completely ignore any static fields
                }

                // Don't add any fields marked with @Ignore
                List<? extends AnnotationMirror> elementAnnotations = variableElement.getAnnotationMirrors();
                boolean ignoreField = false;
                for (AnnotationMirror elementAnnotation : elementAnnotations) {
                    DeclaredType annotationType = elementAnnotation.getAnnotationType();
                    if (typeUtils.isAssignable(annotationType, ignoreAnnotation)) {
                        ignoreField = true;
                        break;
                    }
                }

                if (!ignoreField) {
                    data.addField(element.getSimpleName().toString());
                }
            }
        }

        return data;
    }

    private String getPackageName(TypeElement classElement) {
        Element enclosingElement = classElement.getEnclosingElement();

        if (!enclosingElement.getKind().equals(ElementKind.PACKAGE)) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    "Could not determine the package name. Enclosing element was: " + enclosingElement.getKind());
            return null;
        }

        PackageElement packageElement = (PackageElement) enclosingElement;
        return packageElement.getQualifiedName().toString();
    }
}
