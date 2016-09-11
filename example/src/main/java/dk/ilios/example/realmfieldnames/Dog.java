package dk.ilios.example.realmfieldnames;

import io.realm.RealmObject;

public class Dog extends RealmObject {
    public Person owner;
    public String name;
    public int age;
}
