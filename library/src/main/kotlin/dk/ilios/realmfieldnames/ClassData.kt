package dk.ilios.realmfieldnames

import java.util.TreeMap

/**
 * Class responsible for keeping track of the metadata for each Realm model class.
 */
class ClassData(val packageName: String?, val simpleClassName: String) {

    val fields = TreeMap<String, String?>() // <fieldName, linkedType or null>

    fun addField(field: String, linkedType: String?) {
        fields.put(field, linkedType)
    }

    fun getFields(): Map<String, String?> {
        return fields
    }

    val qualifiedClassName: String
        get() {
            if (packageName != null && !packageName.isEmpty()) {
                return packageName + "." + simpleClassName
            } else {
                return simpleClassName
            }
        }
}
