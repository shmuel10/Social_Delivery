/*
package il.co.yedidia_shmuel.ddb_2.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

import il.co.yedidia_shmuel.ddb_2.R;
import il.co.yedidia_shmuel.ddb_2.model.dataSource.DataSource;
import il.co.yedidia_shmuel.ddb_2.model.dataSourceFactory.DataSourceFactory;
import il.co.yedidia_shmuel.ddb_2.model.entities.Person;

public class VerifyPhoneActivity extends AppCompatActivity implements View.OnClickListener{
    private DataSource _dataBase = DataSourceFactory.getDataBase();
    private Button _verify;
    private EditText _verifyCode;
    private String _verificationCodeBySystem;
    private volatile Boolean _isFinish = false;
    private Person _person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_verify_phone);
        _person = (Person) getIntent().getExtras().getSerializable("person");
        _verify = findViewById(R.id.verify);
        _verify.setOnClickListener(this);
        _verifyCode = findViewById(R.id.verifyCode);
       // sendVerificationCode(_person.get_phoneNumber());
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+972" + phoneNumber, // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,   // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            _verificationCodeBySystem = s;

        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(_verificationCodeBySystem, codeByUser);
        signInTheUserByCredentials(credential);
    }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyPhoneActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                           _dataBase.addPerson(_person, new DataSource.Action<Person>() {
                               @Override
                               public void onSuccess(Person obj) throws InterruptedException {
                                   Intent intent = new Intent(VerifyPhoneActivity.this,UserProfileActivity.class);
                                   startActivity(intent);
                               }

                               @Override
                               public void onFailure(Exception exception) {

                               }

                               @Override
                               public void onProgress(String status, double percent) {

                               }
                           });
                        } else {
                            Toast.makeText(VerifyPhoneActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(_verificationCodeBySystem, _verifyCode.getText().toString());
        signInTheUserByCredentials(credential);
    }
}
*/
