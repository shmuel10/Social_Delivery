package il.co.yedidia_shmuel.ddb_2.model.authentication;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class FirebaseAuth implements AuthService{
    private com.google.firebase.auth.FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();

    public void registerUserWithEmailAndPassword(final String email, final String password, final Action<FirebaseUser> action) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            /*Sign in success, update UI with the signed-in user's information*/
                            Objects.requireNonNull(mAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                final FirebaseUser user = mAuth.getCurrentUser();

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        action.onSuccess(user);
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            action.onFailure(null, Objects.requireNonNull(task.getException()).toString());
                        }
                    }
                });
    }

    public void resetUserPassword(String userMail, final Action<FirebaseUser> action){
        mAuth.sendPasswordResetEmail(userMail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                final FirebaseUser user = mAuth.getCurrentUser();
                action.onSuccess(user);
            }
        });
    }

    public void updateUserPassword(String newPassword, final Action<FirebaseUser> action){
            final FirebaseUser user = mAuth.getCurrentUser();
            if(newPassword != null){
                assert user != null;
                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        action.onSuccess(user);
                    }
                });
            }
    }

    @Override
    public boolean isUserVerifyMail() {
        return Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified();
    }

    public void signInUserWithEmailAndPassword(String email, String password, final Action<FirebaseUser> action) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            action.onSuccess(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            action.onFailure(null, Objects.requireNonNull(task.getException()).toString());
                        }
                    }
                });
    }

    public boolean isLoggedIn(){ return mAuth.getCurrentUser() != null; }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void logout(){
        if(mAuth != null){
            mAuth.signOut();
        }
    }
}
