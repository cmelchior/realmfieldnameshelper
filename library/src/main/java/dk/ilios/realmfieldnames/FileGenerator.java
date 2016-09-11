package dk.ilios.realmfieldnames;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Map;
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
            if (!generateFile(classData, fileData)) {
                return false;
            }
        }

        return true;
    }

    private boolean generateFile(ClassData classData, Set<ClassData> classPool) {

        TypeSpec.Builder fileBuilder = TypeSpec.classBuilder(classData.getSimpleClassName() + "Fields")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("This class enumerate all queryable fields in $L.$L\n",
                        classData.getPackageName(), classData.getSimpleClassName());


        // Add a static field reference to each queryable field in the Realm model class
        for (Map.Entry<String, String> entry : classData.getFields().entrySet()) {

            String fieldName = entry.getKey();
            if (entry.getValue() != null) {
                // Add linked field names (only up to depth 1)
                for (ClassData data : classPool) {
                    if (data.getQualifiedClassName().equals(entry.getValue())) {
                        TypeSpec.Builder linkedTypeSpec = TypeSpec.classBuilder(formatter.format(fieldName))
                                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);
                        Map<String, String> linkedClassFields = data.getFields();
                        addField(linkedTypeSpec, "$", fieldName);
                        for (String linkedFieldName : linkedClassFields.keySet()) {
                            addField(linkedTypeSpec, linkedFieldName, fieldName + "." + linkedFieldName);
                        }
                        fileBuilder.addType(linkedTypeSpec.build());
                    }
                }
            } else {
                // Add normal field name
                addField(fileBuilder, fieldName, fieldName);
            }
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

    private void addField(TypeSpec.Builder fileBuilder, String fieldName, String fieldNameValue) {
        FieldSpec field = FieldSpec.builder(String.class, formatter.format(fieldName))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", fieldNameValue)
                .build();
        fileBuilder.addField(field);
    }
}
