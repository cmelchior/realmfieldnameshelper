package dk.ilios.example.realmfieldnames;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import dk.ilios.example.library.LibraryModule;
import dk.ilios.realmfieldnames.R;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .modules(new AppModule(), new LibraryModule())
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(realmConfig);

        // Standard references
        RealmResults<Person> results = realm.where(Person.class)
                .equalTo(PersonFields.NAME, "John")
                .findAll();

        // Linked field references
        RealmResults<Person> results2 = realm.where(Person.class)
                .equalTo(PersonFields.FAVORITE_DOG.NAME, "Fido")
                .findAll();

        // Kotlin class references
        RealmResults<Dog> results3 = realm.where(Dog.class)
                .equalTo(DogFields.NAME, "Fido")
                .findAll();

        // Library references
        RealmResults<Person> results4 = realm.where(Person.class)
                .equalTo(PersonFields.LIBRARY_REF.LIBRARY_FIELD, "Bar")
                .findAll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
