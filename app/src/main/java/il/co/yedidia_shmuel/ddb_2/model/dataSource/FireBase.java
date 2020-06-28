package il.co.yedidia_shmuel.ddb_2.model.dataSource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

import il.co.yedidia_shmuel.ddb_2.model.entities.Package;
import il.co.yedidia_shmuel.ddb_2.model.entities.Person;

public class FireBase implements DataSource , Serializable {
    private static FirebaseDatabase _database;

    private static DatabaseReference _distributionCenter;
    private static DatabaseReference _registeredPackage;
    private static DatabaseReference _users;

    private static HashMap<String,Package> _packageList;
    private static HashMap<String,Person> _usersList;

    private static ChildEventListener _registeredPackageRefChildEventListener;
    private static ChildEventListener _usersRefChildEventListener;

    static {
        _database = FirebaseDatabase.getInstance();
        _distributionCenter = _database.getReference("Distribution center");
        _registeredPackage = _distributionCenter.child("Registered packages");
        _users = _distributionCenter.child("Users");

        _packageList = new HashMap<>();
        _usersList = new HashMap<>();
    }

    @Override
    public String getNewPackageKey() {
        return _distributionCenter.push().getKey();
    }

    @Override
    public void addPackage(final Package pkg, final Action<Package> action) {
        final String key = pkg.get_packageID();
        _registeredPackage.child(key).setValue(pkg).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                try {
                    action.onSuccess(pkg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                action.onProgress("upload package data", 100);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
                action.onProgress("Error upload package data", 100);
            }
        });
    }

    @Override
    public void getPackage(String key, final Action<Package> action) {
        _registeredPackage.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Package pkg = dataSnapshot.getValue(Package.class);
                try {
                    action.onSuccess(pkg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                action.onFailure(new Exception(databaseError.getMessage()));
            }
        });
    }

    @Override
    public void addPerson(final Person person, final Action<Person> action) {
        final String key = person.get_mail().replace(".","|");
        _users.child(key).setValue(person).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                try {
                    action.onSuccess(person);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                action.onProgress("upload package data", 100);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
                action.onProgress("Error upload person data", 100);
            }
        });
    }

    @Override
    public void getPerson(String key, final Action<Person> action) {
        _users.child(key.replace(".","|")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Person person = dataSnapshot.getValue(Person.class);
                if(person != null){
                    person.set_mail(Objects.requireNonNull(dataSnapshot.getKey()));
                }
                try {
                    action.onSuccess(person);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                action.onFailure(new Exception(databaseError.getMessage()));
            }
        });
    }

    @Override
    public void notifyToUserList(final NotifyDataChange<HashMap<String,Person>> notifyDataChange) {
        if (notifyDataChange != null) {
            if (_usersRefChildEventListener != null) {
                notifyDataChange.onFailure(new Exception("first unNotify users list"));
                return;
            }
            _usersList.clear();
            _usersRefChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Person person = dataSnapshot.getValue(Person.class);
                    if(person != null){
                        person.set_mail(Objects.requireNonNull(dataSnapshot.getKey()));
                        _usersList.put(person.get_mail(),person);
                        notifyDataChange.onDataChange(_usersList);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Person person = dataSnapshot.getValue(Person.class);
                    if(person != null){
                        person.set_mail(Objects.requireNonNull(dataSnapshot.getKey()));
                        _usersList.put(person.get_mail(),person);
                        notifyDataChange.onDataChange(_usersList);
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Person person = dataSnapshot.getValue(Person.class);
                    if(person != null){
                        person.set_mail(Objects.requireNonNull(dataSnapshot.getKey()));
                        _usersList.put(person.get_mail(),person);
                        notifyDataChange.onDataChange(_usersList);
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    notifyDataChange.onFailure(databaseError.toException());
                }
            };
            _users.addChildEventListener(_usersRefChildEventListener);
        }
    }

    @Override
    public void notifyToPackageList(final NotifyDataChange<HashMap<String,Package>> notifyDataChange) {
        if (notifyDataChange != null) {
            if (_registeredPackageRefChildEventListener != null) {
                notifyDataChange.onFailure(new Exception("first unNotify users list"));
                return;
            }
            _packageList.clear();
            _registeredPackageRefChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Package pkg = dataSnapshot.getValue(Package.class);
                    if(pkg != null){
                        pkg.set_packageID(dataSnapshot.getKey());
                        _packageList.put(pkg.get_packageID(),pkg);
                        notifyDataChange.onDataChange(_packageList);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Package pkg = dataSnapshot.getValue(Package.class);
                    if (pkg != null) {
                        pkg.set_packageID(dataSnapshot.getKey());
                        _packageList.put(pkg.get_packageID(), pkg);
                        notifyDataChange.onDataChange(_packageList);
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Package pkg = dataSnapshot.getValue(Package.class);
                    if (pkg != null) {
                        pkg.set_packageID(dataSnapshot.getKey());
                        _packageList.put(pkg.get_packageID(), pkg);
                        notifyDataChange.onDataChange(_packageList);
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    notifyDataChange.onFailure(databaseError.toException());
                }
            };
            _registeredPackage.addChildEventListener(_registeredPackageRefChildEventListener);
        }
    }
    
    @Override
    public void stopNotifyToUsersList(){
        if(_usersRefChildEventListener != null){
            _users.removeEventListener(_usersRefChildEventListener);
            _usersRefChildEventListener = null;
        }
    }

    @Override
    public void stopNotifyToPackageList(){
        if(_registeredPackageRefChildEventListener != null){
            _registeredPackage.removeEventListener(_registeredPackageRefChildEventListener);
            _registeredPackageRefChildEventListener = null;
        }
    }

    @Override
    public DatabaseReference getUserReference() {
        return _users;
    }

    @Override
    public DatabaseReference getPackageReference() {
        return _registeredPackage;
    }
}
