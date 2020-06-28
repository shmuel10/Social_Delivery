package il.co.yedidia_shmuel.ddb_2.model.authentication;

import com.google.firebase.auth.FirebaseUser;

public interface AuthService {
    public interface Action<T>{
        void onSuccess(T obj);
        void onFailure(T obj, String msg);
    }

    void registerUserWithEmailAndPassword(String email, String password, final Action<FirebaseUser> action);
    void signInUserWithEmailAndPassword(String email, String password, final Action<FirebaseUser> action);

    void resetUserPassword(String userMail, final Action<FirebaseUser> action);
    void updateUserPassword(String newPassword, final Action<FirebaseUser> action);
    boolean isUserVerifyMail();

    FirebaseUser getCurrentUser();
    boolean isLoggedIn();
    void logout();
}
