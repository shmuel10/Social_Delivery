package il.co.yedidia_shmuel.ddb_2.controller.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseUser;

import il.co.yedidia_shmuel.ddb_2.R;
import il.co.yedidia_shmuel.ddb_2.controller.activities.UserProfileActivity;
import il.co.yedidia_shmuel.ddb_2.model.authentication.AuthService;
import il.co.yedidia_shmuel.ddb_2.model.dataSource.DataSource;
import il.co.yedidia_shmuel.ddb_2.model.dataSourceFactory.DataSourceFactory;
import il.co.yedidia_shmuel.ddb_2.model.entities.MyAddress;
import il.co.yedidia_shmuel.ddb_2.model.entities.Person;
import il.co.yedidia_shmuel.ddb_2.util.Validation;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditUserProfileFragment extends Fragment implements View.OnClickListener{
    private DataSource _dataBase = DataSourceFactory.getDataBase();
    private AuthService _authService = DataSourceFactory.getAuthService();
    private EditText _fName, _lName,_email,_password,_address, _maxDistance;
    private Button _addPerson;
    private Person _newPerson;
    private Person _currentUser;
    private MyAddress _personAddress;
    private ProgressBar _progressBar;
    private String _currentUserID;
    private View view;

    public EditUserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_user_profile, container, false);
        findView(v);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            _currentUserID = bundle.getString("UserID");
        }
        _dataBase.getPerson(_currentUserID, new DataSource.Action<Person>() {
            @Override
            public void onSuccess(Person obj) throws InterruptedException {
                _currentUser = obj;
                initialView(_currentUser);
            }

            @Override
            public void onFailure(Exception exception) {

            }

            @Override
            public void onProgress(String status, double percent) {

            }
        });
        _personAddress = null;
        return v;
    }


    private void findView(View v){
        _fName = v.findViewById(R.id.personFName);
        _lName = v.findViewById(R.id.personLName);
        _email = v.findViewById(R.id.personMail);
        _email.setEnabled(false);
        _password = v.findViewById(R.id.password);
        _address = v.findViewById(R.id.personAddress);
        _maxDistance = v.findViewById(R.id.maxDistanceForCollectPackage);
        _progressBar = v.findViewById(R.id.progressBar);
        _progressBar.setVisibility(View.INVISIBLE);
        _addPerson = v.findViewById(R.id.addPerson);
        _addPerson.setOnClickListener(this);
    }

    private void initialView(Person currentUser){
        _fName.setText(currentUser.get_fName());
        _lName.setText(currentUser.get_lName());
        _email.setText(currentUser.get_mail());
        _address.setText(currentUser.get_address().get_address());
        _maxDistance.setText(currentUser.get_maxDistanceToTakePackage());
        _progressBar.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(View v) {
        _addPerson.setEnabled(false);
        if (validDetails()) {
            _progressBar.setVisibility(View.VISIBLE);
            try {
                _personAddress = MyAddress.locationByAddress(getContext(), _address.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (_personAddress != null) {
                _newPerson = new Person(_fName.getText().toString(),
                        _lName.getText().toString(), _email.getText().toString().trim(),
                        _personAddress,_maxDistance.getText().toString().trim());
                _authService.updateUserPassword(_password.getText().toString().trim(), new AuthService.Action<FirebaseUser>() {
                    @Override
                    public void onSuccess(FirebaseUser obj) {
                        _dataBase.addPerson(_newPerson, new DataSource.Action<Person>() {
                            @Override
                            public void onSuccess(Person obj) throws InterruptedException {
                                Toast.makeText(getContext(),"הפרטים עודכנו בהצלחה",Toast.LENGTH_LONG).show();
                                _progressBar.setVisibility(View.INVISIBLE);
                                _authService.signInUserWithEmailAndPassword(_email.getText().toString(),
                                        _password.getText().toString(), new AuthService.Action<FirebaseUser>() {
                                    @Override
                                    public void onSuccess(FirebaseUser obj) {
                                        Intent intent = new Intent(getContext(), UserProfileActivity.class);
                                     //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.putExtra("mail", _email.getText().toString().trim());
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(FirebaseUser obj, String msg) {

                                    }
                                });
                            }

                            @Override
                            public void onFailure(Exception exception) {
                                Toast.makeText(getContext(), exception.getMessage().toString(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onProgress(String status, double percent) {

                            }
                        });
                    }

                    @Override
                    public void onFailure(FirebaseUser obj, String msg) {

                    }
                });
            } else {
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
            _maxDistance.setError("נדרש לפחות 6 ק״מ");
            view = _maxDistance;
            valid = false;
            _addPerson.setEnabled(true);
            _maxDistance.requestFocus();
        }
        return valid;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((UserProfileActivity) requireActivity()).setActionBarTitle("הפרטים שלי");
    }
}
