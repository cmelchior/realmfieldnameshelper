package dk.ilios.example.library;

import io.realm.RealmObject;

public class LibraryModel extends RealmObject {
    public String libraryPublicField;
    private boolean libraryPrivateField;

    public boolean isLibraryPrivateField() {
        return libraryPrivateField;
    }

    public void setLibraryPrivateField(boolean libraryPrivateField) {
        this.libraryPrivateField = libraryPrivateField;
    }
}
