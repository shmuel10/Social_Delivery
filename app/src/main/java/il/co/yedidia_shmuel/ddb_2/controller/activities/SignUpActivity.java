package il.co.yedidia_shmuel.ddb_2.controller.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

import il.co.yedidia_shmuel.ddb_2.R;
import il.co.yedidia_shmuel.ddb_2.model.authentication.AuthService;
import il.co.yedidia_shmuel.ddb_2.model.dataSource.DataSource;
import il.co.yedidia_shmuel.ddb_2.model.dataSourceFactory.DataSourceFactory;
import il.co.yedidia_shmuel.ddb_2.model.entities.MyAddress;
import il.co.yedidia_shmuel.ddb_2.model.entities.Person;
import il.co.yedidia_shmuel.ddb_2.util.Validation;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{
    private DataSource _dataBase = DataSourceFactory.getDataBase();
    private AuthService _authService = DataSourceFactory.getAuthService();
    private EditText _fName, _lName,_email,_password,_address, _maxDistance;
    private Button _addPerson,_newSignIn;
    private Person _newPerson;
    private MyAddress _personAddress;
    private ProgressBar _progressBar;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        _personAddress = null;
        findView();
    }

    private void findView(){
        _fName = findViewById(R.id.personFName);
        _lName = findViewById(R.id.personLName);
        _email = findViewById(R.id.personMail);
        _password = findViewById(R.id.password);
        _address = findViewById(R.id.personAddress);
        _maxDistance = findViewById(R.id.maxDistanceForCollectPackage);
        _progressBar = findViewById(R.id.progressBar);
        _progressBar.setVisibility(View.INVISIBLE);
        _newSignIn = findViewById(R.id.newSignIn);
        _addPerson = findViewById(R.id.addPerson);
        _addPerson.setOnClickListener(this);
    }

    private void initialView(){
        _fName.setText("");
        _lName.setText("");
        _email.setText("");
        _password.setText("");
        _address.setText("");
        _maxDistance.setText("");
        _progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        _addPerson.setEnabled(false);
        if (validDetails()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _progressBar.setVisibility(View.VISIBLE);
                }
            });
            try {
                _personAddress = MyAddress.locationByAddress(SignUpActivity.this, _address.getText().toString());
            }catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(SignUpActivity.this,"מיד יישלח לינק אימות למייל...",Toast.LENGTH_LONG).show();

            if(_personAddress != null) {
                _newPerson = new Person(_fName.getText().toString(),
                        _lName.getText().toString(), _email.getText().toString().trim().toLowerCase(),
                        _personAddress, _maxDistance.getText().toString().trim());
                _authService.registerUserWithEmailAndPassword(_email.getText().toString().trim(), _password.getText().toString().trim(),
                        new AuthService.Action<FirebaseUser>() {
                            @Override
                            public void onSuccess(final FirebaseUser obj) {
                                _dataBase.addPerson(_newPerson, new DataSource.Action<Person>() {
                                    @Override
                                    public void onSuccess(final Person p) throws InterruptedException {
                                        Toast.makeText(SignUpActivity.this,"בדוק את המייל שלך, ואשר את הלינק",Toast.LENGTH_LONG).show();
                                        _addPerson.setVisibility(View.INVISIBLE);
                                        _newSignIn.setVisibility(View.VISIBLE);
                                        _progressBar.setVisibility(View.INVISIBLE);
                                        _newSignIn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                obj.reload();
                                                if(obj.isEmailVerified()){
                                                    Intent intent = new Intent(SignUpActivity.this, UserProfileActivity.class);
                                                    intent.putExtra("mail", p.get_mail().trim());
                                                    startActivity(intent);
                                                    finish();
                                                }else{
                                                    Toast.makeText(SignUpActivity.this,"בדוק את המייל שלך, ואשר את הלינק",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(Exception exception) {
                                        _authService.getCurrentUser().delete();
                                    }

                                    @Override
                                    public void onProgress(String status, double percent) {

                                    }
                                });
                            }

                            @Override
                            public void onFailure(FirebaseUser obj, String msg) {
                                if (msg.contains("password")) {
                                    Toast.makeText(SignUpActivity.this, "סיסמה לא תקינה", Toast.LENGTH_LONG).show();
                                    _password.setError(msg);
                                    _password.setText("");
                                    _progressBar.setVisibility(View.INVISIBLE);
                                    _password.requestFocus();
                                    _addPerson.setEnabled(true);
                                }else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                    builder.setTitle("ERROR");
                                    builder.setMessage("אופס! מישהו כבר נרשם עם המייל הזה");
                                    builder.setIcon(R.drawable.ic_error_24dp);
                                    builder.setCancelable(true);
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    _progressBar.setVisibility(View.INVISIBLE);
                                    _addPerson.setEnabled(true);
                                }
                            }
                        });

            }else{
                _address.setError("לא הצלחנו למצוא את הכתובת שרשמת, אנא הכנס/י עיר, רחוב ומספר בניין");
                _address.setText("");
                _address.requestFocus();
                _progressBar.setVisibility(View.INVISIBLE);
                _addPerson.setEnabled(true);
            }
        }
    }

    private boolean validDetails(){
        boolean valid = true;
        view = null;
        _password.setError(null);
        _fName.setError(null);
        _lName.setError(null);
        _email.setError(null);
        _address.setError(null);

        if(Validation.isFirstNameEmpty(_fName.getText().toString())){
            _fName.setError("הכנס שם פרטי");
            view = _fName;
            valid = false;
            _addPerson.setEnabled(true);
            _fName.requestFocus();
        }

        if(Validation.isLastNameEmpty(_lName.getText().toString())){
            _lName.setError("הכנס שם משפחה");
            view = _lName;
            valid = false;
            _addPerson.setEnabled(true);
            _lName.requestFocus();
        }

        if (Validation.isMailValid(_email.getText().toString())){
            _email.setError("כתובת מייל אינה תקינה");
            view = _email;
            valid = false;
            _addPerson.setEnabled(true);
            _email.requestFocus();
        }

        if(Validation.isAddressEmpty(_address.getText().toString())) {
            _address.setError("כתובת נדרשת");
            view = _address;
            valid = false;
            _addPerson.setEnabled(true);
            _address.requestFocus();
        }

        if(Validation.isPasswordCorrect(_password.getText().toString())){
            _password.setError("סיסמה חייבת להיות בת 6 תווים לפחות");
            view = _password;
            valid = false;
            _addPerson.setEnabled(true);
            _password.requestFocus();
        }

        if(_maxDistance.getText().length() == 0 || Validation.isDistanceValid(Integer.parseInt(_maxDistance.getText().toString()))){
            _maxDistance.setError("נדרש לפחות 5 ק״מ");
            view = _maxDistance;
            valid = false;
            _addPerson.setEnabled(true);
            _maxDistance.requestFocus();
        }
        return valid;
    }

    @Override
    protected void onDestroy() {
        _authService.logout();
        super.onDestroy();
    }
}
