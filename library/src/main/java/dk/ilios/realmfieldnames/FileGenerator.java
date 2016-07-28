package dk.ilios.realmfieldnames;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

/**
 * Class responsible for creating the final output files.
 */
public class FileGenerator {

    private final Filer filer;
    private final FieldNameFormatter formatter;

    public FileGenerator(Filer filer) {
        this.filer = filer;
        this.formatter = new FieldNameFormatter();
    }

    /**
     * Generates all the "&lt;class&gt;Fields" fields with field name references.
     * @param fileData Files to create.
     * @return {@code true} if the files where generated, {@code false} if not.
     */
    public boolean generate(Set<ClassData> fileData) {
        for (ClassData classData : fileData) {
            if (!generateFile(classData)) {
                return false;
            }
        }

        return true;
    }

    private boolean generateFile(ClassData classData) {

        TypeSpec.Builder fileBuilder = TypeSpec.classBuilder(classData.getSimpleClassName() + "Fields")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("This class enumerate all queryable fields in $S.$S",
                        classData.getPackageName(), classData.getSimpleClassName());


        // Add a static field reference to each queryable field in the Realm model class
        for (String fieldName : classData.getFields()) {
            FieldSpec field = FieldSpec.builder(String.class, formatter.format(fieldName))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", fieldName)
                    .build();
            fileBuilder.addField(field);
        }

        JavaFile javaFile = JavaFile.builder(classData.getPackageName(), fileBuilder.build()).build();
        try {
            javaFile.writeTo(filer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
