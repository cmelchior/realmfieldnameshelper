package dk.ilios.example.realmfieldnames

import io.realm.RealmObject
import io.realm.annotations.RealmClass

open class Dog : RealmObject() {
    open var owner: Person? = null
    open var name: String? = null
    open var age: Int = 0
}
