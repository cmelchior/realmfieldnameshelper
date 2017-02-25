package dk.ilios.realmfieldnames

import java.util.HashSet

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

/**
 * The Realm Field Names Generator is a processor that looks at all available Realm model classes
 * and create an companion class with easy, type-safe access to all field names.
 */

@SupportedAnnotationTypes("io.realm.annotations.RealmClass")
class RealmFieldNamesProcessor : AbstractProcessor() {

    private val classes = HashSet<ClassData>()
    private var typeUtils: Types? = null
    private var messager: Messager? = null
    private var elementUtils: Elements? = null
    private var ignoreAnnotation: TypeMirror? = null
    private var realmModelClass: TypeMirror? = null
    private var realmListClass: DeclaredType? = null
    private var fileGenerator: FileGenerator? = null
    private var done = false

    @Synchronized override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        typeUtils = processingEnv.typeUtils
        messager = processingEnv.messager
        elementUtils = processingEnv.elementUtils
        ignoreAnnotation = elementUtils!!.getTypeElement("io.realm.annotations.Ignore").asType()
        realmModelClass = elementUtils!!.getTypeElement("io.realm.RealmModel").asType()
        realmListClass = typeUtils!!.getDeclaredType(elementUtils!!.getTypeElement("io.realm.RealmList"),
                typeUtils!!.getWildcardType(null, null))
        fileGenerator = FileGenerator(processingEnv.filer)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (done) {
            return CONSUME_ANNOTATIONS
        }

        // Create all proxy classes
        val realmClassAnnotation = annotations.iterator().next()
        for (classElement in roundEnv.getElementsAnnotatedWith(realmClassAnnotation)) {
            val classData = processClass(classElement as TypeElement)
            classes.add(classData)
        }

        done = fileGenerator!!.generate(classes)
        return CONSUME_ANNOTATIONS
    }

    private fun processClass(classElement: TypeElement): ClassData {
        val packageName = getPackageName(classElement)
        val className = classElement.simpleName.toString()
        val data = ClassData(packageName, className)

        // Find all appropriate fields
        classElement.enclosedElements.forEach {
            val elementKind = it.kind
            if (elementKind == ElementKind.FIELD) {
                val variableElement = it as VariableElement

                val modifiers = variableElement.modifiers
                if (modifiers.contains(Modifier.STATIC)) {
                    return@forEach // completely ignore any static fields
                }

                // Don't add any fields marked with @Ignore
                val elementAnnotations = variableElement.annotationMirrors
                var ignoreField = false
                for (elementAnnotation in elementAnnotations) {
                    val annotationType = elementAnnotation.annotationType
                    if (typeUtils!!.isAssignable(annotationType, ignoreAnnotation)) {
                        ignoreField = true
                        break
                    }
                }

                if (!ignoreField) {
                    data.addField(it.getSimpleName().toString(), getLinkedFieldType(it))
                }
            }
        }

        return data
    }

    /**
     * Returns the qualified name of the linked Realm class field or `null` if it is not a linked
     * class.
     */
    private fun getLinkedFieldType(field: Element): String? {
        if (typeUtils!!.isAssignable(field.asType(), realmModelClass)) {
            // Object link
            val typeElement = elementUtils!!.getTypeElement(field.asType().toString())
            return typeElement.qualifiedName.toString()
        } else if (typeUtils!!.isAssignable(field.asType(), realmListClass)) {
            // List link
            val fieldType = field.asType()
            val typeArguments = (fieldType as DeclaredType).typeArguments
            if (typeArguments.size == 0) {
                return null
            }
            return typeArguments[0].toString()
        } else {
            return null
        }
    }

    private fun getPackageName(classElement: TypeElement): String? {
        val enclosingElement = classElement.enclosingElement

        if (enclosingElement.kind != ElementKind.PACKAGE) {
            messager!!.printMessage(Diagnostic.Kind.ERROR,
                    "Could not determine the package name. Enclosing element was: " + enclosingElement.kind)
            return null
        }

        val packageElement = enclosingElement as PackageElement
        return packageElement.qualifiedName.toString()
    }

    companion object {
        private const val CONSUME_ANNOTATIONS = false
    }
}
