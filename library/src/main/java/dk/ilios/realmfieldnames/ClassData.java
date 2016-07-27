package dk.ilios.realmfieldnames;

import java.util.Set;
import java.util.TreeSet;

/**
 * Class responsible for keeping track of the metadata for each Realm model class.
 */
public class ClassData {
    private String packageName;
    private String simpleClassName;
    private Set<String> fields = new TreeSet<>();

    public ClassData(String packageName, String simpleClassName) {
        this.packageName = packageName;
        this.simpleClassName = simpleClassName;
    }

    public void addField(String field) {
        fields.add(field);
    }

    public Set<String> getFields() {
        return fields;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public String getPackageName() {
        return packageName;
    }
}
