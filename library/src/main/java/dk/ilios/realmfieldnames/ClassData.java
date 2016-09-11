package dk.ilios.realmfieldnames;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Class responsible for keeping track of the metadata for each Realm model class.
 */
public class ClassData {
    private String packageName;
    private String simpleClassName;
    private TreeMap<String, String> fields = new TreeMap<>(); // <fieldName, linkedType or null>
    public ClassData(String packageName, String simpleClassName) {
        this.packageName = packageName;
        this.simpleClassName = simpleClassName;
    }

    public void addField(String field, String linkedType) {
        fields.put(field, linkedType);
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getQualifiedClassName() {
        if (packageName != null && !packageName.isEmpty()) {
            return packageName + "." + simpleClassName;
        } else {
            return simpleClassName;
        }
    }
}
