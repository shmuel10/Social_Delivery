package il.co.yedidia_shmuel.ddb_2.controller.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

import il.co.yedidia_shmuel.ddb_2.R;
import il.co.yedidia_shmuel.ddb_2.model.authentication.AuthService;
import il.co.yedidia_shmuel.ddb_2.model.dataSourceFactory.DataSourceFactory;
import il.co.yedidia_shmuel.ddb_2.util.CheckConnectionService;
import il.co.yedidia_shmuel.ddb_2.util.NewPackageService;
import il.co.yedidia_shmuel.ddb_2.util.Validation;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private AuthService _authService = DataSourceFactory.getAuthService();
    private EditText _email, _password;
    private Button _signIn, _signUp, _forgotPassword;
    private View view;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        startService(new Intent(LoginActivity.this, CheckConnectionService.class));
        if(_authService.isLoggedIn() && _authService.isUserVerifyMail()){
            Intent intent = new Intent(LoginActivity.this, UserProfileActivity.class);
            intent.putExtra("mail",_authService.getCurrentUser().getEmail());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startService(new Intent(LoginActivity.this, NewPackageService.class));
            startActivity(intent);
        }
        initialView();
    }

    private void initialView() {
        _email = findViewById(R.id.email);
        _password = findViewById(R.id.password);
        _signIn = findViewById(R.id.signIn);
        _signUp = findViewById(R.id.signUp);
        _forgotPassword = findViewById(R.id.forgotPassword);
        _forgotPassword.setOnClickListener(this);
        _signIn.setOnClickListener(this);
        _signUp.setOnClickListener(this);
        _email.setText("");
        _password.setText("");
        _signIn.setEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        _email.setText("");
        _password.setText("");
        _signIn.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signIn:
                _signIn.setEnabled(false);
                if (Validation.isMailValid(_email.getText().toString().trim())) {
                    _email.setError("כתובת מייל אינה תקינה");
                    view = _email;
                    _email.requestFocus();
                    _signIn.setEnabled(true);
                    return;
                }
                if (_password.getText().toString().trim().length() == 0) {
                    _password.setError("הכנס סיסמה");
                    _password.requestFocus();
                    _signIn.setEnabled(true);
                    return;
                }

                _authService.signInUserWithEmailAndPassword(_email.getText().toString().trim().toLowerCase(),
                        _password.getText().toString().trim(), new AuthService.Action<FirebaseUser>() {
                            @Override
                            public void onSuccess(FirebaseUser obj) {
                                if (obj.isEmailVerified()) {
                                    startService(new Intent(LoginActivity.this, NewPackageService.class));
                                    Intent intent = new Intent(LoginActivity.this, UserProfileActivity.class);
                                    intent.putExtra("mail", _email.getText().toString().trim());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginActivity.this, "בדוק את המייל שלך, לא אישרת את לינק ההרשמה", Toast.LENGTH_LONG).show();
                                    _signIn.setEnabled(true);
                                }
                            }

                            @Override
                            public void onFailure(FirebaseUser obj, String msg) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle("ERROR");
                                builder.setMessage("דוא״ל או סיסמה לא תקינים");
                                builder.setIcon(R.drawable.ic_error_24dp);
                                builder.setCancelable(true);
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                _signIn.setEnabled(true);
                            }
                        });
                break;
            case R.id.signUp:
                _email.clearFocus();
                _password.clearFocus();
                _signUp.setEnabled(false);
                final Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                _signUp.setEnabled(true);
                _signIn.setEnabled(true);
                break;
            case R.id.forgotPassword:
                _forgotPassword.setEnabled(false);
                if (Validation.isMailValid(_email.getText().toString())) {
                    _email.setError("כתובת מייל אינה תקינה");
                    _email.requestFocus();
                    _forgotPassword.setEnabled(true);
                    return;
                }
                Toast.makeText(LoginActivity.this, "שלחנו לך מייל לאיפוס סיסמה", Toast.LENGTH_LONG).show();
                _authService.resetUserPassword(_email.getText().toString().trim().toLowerCase(), new AuthService.Action<FirebaseUser>() {
                    @Override
                    public void onSuccess(FirebaseUser obj) {
                        setContentView(R.layout.login_activity);
                        initialView();
                    }

                    @Override
                    public void onFailure(FirebaseUser obj, String msg) {

                    }
                });
                _forgotPassword.setEnabled(true);
                _signIn.setEnabled(true);
                break;
        }
    }
}
