package il.co.yedidia_shmuel.ddb_2.model.dataSource;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

import il.co.yedidia_shmuel.ddb_2.model.entities.Package;
import il.co.yedidia_shmuel.ddb_2.model.entities.Person;

public interface DataSource {
    interface Action<T>{
        void onSuccess(T obj) throws InterruptedException;
        void onFailure(Exception exception);
        void onProgress(String status, double percent);
    }

    interface NotifyDataChange<T>{
        void onDataChange(T obj);
        void onFailure(Exception exception);
    }

    String getNewPackageKey();
    DatabaseReference getUserReference();
    DatabaseReference getPackageReference();
    void addPackage(Package pkg, final Action<Package> action);
    void addPerson(Person person, final Action<Person> action);
    void getPerson(String key,final Action<Person> action);
    void getPackage(String key, final Action<Package> action);


    void notifyToUserList(final NotifyDataChange<HashMap<String,Person>> notifyDataChange);
    void notifyToPackageList(final NotifyDataChange<HashMap<String,Package>> notifyDataChange);
    void stopNotifyToPackageList();
    void stopNotifyToUsersList();
}
