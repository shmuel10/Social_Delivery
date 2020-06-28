package il.co.yedidia_shmuel.ddb_2.util;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import il.co.yedidia_shmuel.ddb_2.model.dataSource.DataSource;
import il.co.yedidia_shmuel.ddb_2.model.dataSourceFactory.DataSourceFactory;
import il.co.yedidia_shmuel.ddb_2.model.entities.Package;

public class NewPackageService extends IntentService{
    private DataSource _dataBase = DataSourceFactory.getDataBase();
    private Date _date;
    private DateFormat _dateFormat;
    private String _now;
    private FirebaseAuth _mAuth;
    private FirebaseUser _currentUser;
    private String _currentUserMail;

    @SuppressLint("SimpleDateFormat")
    public NewPackageService(){
        super("NewPackageService");
        _mAuth = FirebaseAuth.getInstance();
        _currentUser = _mAuth.getCurrentUser();
        _currentUserMail = _currentUser.getEmail();
        _date = Calendar.getInstance().getTime();
        _dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        _now = _dateFormat.format(_date).trim();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        _dataBase.getPackageReference().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Package p = dataSnapshot.getValue(Package.class);
                if (p != null) {
                    if ((p.get_recipientMail().trim().equals(_currentUserMail.trim())) &&
                            (p.get_registerTime().trim().compareTo(_now.trim()) > 0)) {
                        sendBroadcast(new Intent("new_package_service"));
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    @Override
    public void setIntentRedelivery(boolean enabled) {
        super.setIntentRedelivery(true);
    }
}
