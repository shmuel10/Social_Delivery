package il.co.yedidia_shmuel.ddb_2.model.entities;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Person implements Serializable {
    private String _fName;
    private String _lName;
    private String _mail = "$";
    private String _maxDistanceToTakePackage;
    private MyAddress _address;
    private Map<String, String> _friendsList;

    public Person(String fName, String lName, String mail,MyAddress address, String maxDistanceToTakePackages){
        _fName = fName;
        _lName = lName;
        _mail = mail;
        _address = address;
        _maxDistanceToTakePackage = maxDistanceToTakePackages;
        _friendsList = new HashMap<>();
    }

    public Person(){ _friendsList = new HashMap<>(); }

    public String get_key() {
        return _mail.replace(".","|");
    }

    @Exclude
    public String get_mail() {
        return _mail;
    }

    public void set_mail(String _mail) {
        this._mail = _mail.replace("|",".");
    }

    public String get_fName() {
        return _fName;
    }

    public void set_fName(String _fName) {
        this._fName = _fName;
    }

    public String get_lName() {
        return _lName;
    }

    public void set_lName(String _lName) {
        this._lName = _lName;
    }

    public MyAddress get_address() {
        return _address;
    }

    public void set_address(MyAddress _address) {
        this._address = _address;
    }

    public String get_maxDistanceToTakePackage() {
        return _maxDistanceToTakePackage;
    }

    public void set_maxDistanceToTakePackage(String _maxDistanceToTakePackage) {
        this._maxDistanceToTakePackage = _maxDistanceToTakePackage;
    }

    public Map<String, String> get_friendsList() {
        return _friendsList;
    }

    public void set_friendsList(Map<String, String> _friendsList) {
        this._friendsList = _friendsList;
    }

    public void requestFriend(String offerMail) {
        _friendsList.put(offerMail.replace(".", "|"), "N");
    }

    public void unRequestFriend(String offerMail) {
        Objects.requireNonNull(_friendsList.remove(offerMail.replace(".", "|")));
    }

    public void confirmFriend(String offerMail) {
        _friendsList.put(offerMail.replace(".", "|"), "T");
    }

    public void unFriend(String offerMail) {
        Objects.requireNonNull(_friendsList.remove(offerMail.replace(".", "|")));
    }
}
