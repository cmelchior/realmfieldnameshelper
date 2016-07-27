package dk.ilios.example.realmfieldnames;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import dk.ilios.realmfieldnames.R;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm realm = Realm.getInstance(realmConfig);

        RealmQuery<Person> results = realm.where(Person.class).equalTo("name", "John");
    }
}
