package il.co.yedidia_shmuel.ddb_2.model.entities;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity(tableName = "Package_table")
public class Package {
    @PrimaryKey
    @NonNull
    private String _packageID;

    public Status get_status() {
        return _status;
    }

    public void set_status(Status _status) {
        this._status = _status;
    }

    private Status _status;
    private PackageType _type;
    private Boolean _isFragile;
    private PackageWeight _weight;
    private String _recipientMail;
    private String _recipientFName;
    private String _recipientLName;
    private MyAddress _recipientAddress;
    private MyAddress _distributionCenterAddress;
    private String _registerTime;
    private String _mailOfDeliver;
    private String _dayOfCollect;
    private Boolean _isDelivered;
    private Map<String, String> _offerToPickUpThePackage;

    @SuppressLint("SimpleDateFormat")
    public Package(PackageType type, Boolean isFragile, PackageWeight weight, String packageID, String recipientMail,
                   String recipientFName, String recipientLName, MyAddress recipientAddress, MyAddress distributionCenter) {
        _type = type;
        _isFragile = isFragile;
        _weight = weight;
        _packageID = packageID;
        _recipientMail = recipientMail;
        _recipientAddress = recipientAddress;
        _recipientLName = recipientLName;
        _recipientFName = recipientFName;
        _distributionCenterAddress = distributionCenter;
        _offerToPickUpThePackage = new HashMap<String, String>();
        _isDelivered = false;
        _status = Status.REGISTERED;
        _mailOfDeliver = "";
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        _registerTime = dateFormat.format(date).trim();
    }

    public Package() {
        _offerToPickUpThePackage = new HashMap<>();
        _packageID = "";
        _recipientMail = "";
        _recipientFName = "";
        _recipientLName = "";
        _recipientAddress = new MyAddress();
        _distributionCenterAddress = new MyAddress();
        _mailOfDeliver = "";
        _dayOfCollect = "";
        _registerTime = "";
        _isDelivered = false;
        _offerToPickUpThePackage = new HashMap<>();
    }



    public PackageType get_type() {
        return _type;
    }

    public void set_type(PackageType _Type) {
        this._type = _Type;
    }

    public Boolean get_isFragile() {
        return _isFragile;
    }

    public void set_isFragile(Boolean _isFragile) {
        this._isFragile = _isFragile;
    }

    public PackageWeight get_weight() {
        return _weight;
    }

    public void set_weight(PackageWeight _weight) {
        this._weight = _weight;
    }

    public String get_packageID() {
        return _packageID;
    }

    public void set_packageID(String _packageID) {
        this._packageID = _packageID;
    }

    public String get_recipientMail() {
        return _recipientMail;
    }

    public void set_recipientMail(String _recipientMail) {
        this._recipientMail = _recipientMail;
    }

    public MyAddress get_recipientAddress() {
        return _recipientAddress;
    }

    public void set_recipientAddress(MyAddress _recipientAddress) {
        this._recipientAddress = _recipientAddress;
    }

    public MyAddress get_distributionCenterAddress() {
        return _distributionCenterAddress;
    }

    public void set_distributionCenterAddress(MyAddress _distributionCenterAddress) {
        this._distributionCenterAddress = _distributionCenterAddress;
    }

    public String get_recipientFName() {
        return _recipientFName;
    }

    public void set_recipientFName(String _recipientFName) {
        this._recipientFName = _recipientFName;
    }

    public String get_recipientLName() {
        return _recipientLName;
    }

    public void set_recipientLName(String _recipientLName) {
        this._recipientLName = _recipientLName;
    }

    public Map<String, String> get_offerToPickUpThePackage() {
        return _offerToPickUpThePackage;
    }

    public void set_offerToPickUpThePackage(Map<String, String> _offerToPickUpThePackage) {
        this._offerToPickUpThePackage = _offerToPickUpThePackage;
        updateStatus();
    }

    public String get_mailOfDeliver() {
        return _mailOfDeliver;
    }

    public void set_mailOfDeliver(String _mailOfDeliver) {
        this._mailOfDeliver = _mailOfDeliver;
        updateStatus();
    }

    public String get_dayOfCollect() {
        return _dayOfCollect;
    }

    public void set_dayOfCollect(String _dayOfCollect) {
        this._dayOfCollect = _dayOfCollect;
        updateStatus();
    }

    public void insertNewOffer(String offerMail) {
        _offerToPickUpThePackage.put(offerMail.replace(".", "|"), "N");
        updateStatus();
    }

    public void removeOffer(String offerMail) {
        if (_offerToPickUpThePackage.containsKey(offerMail.replace(".", "|"))) {
            _offerToPickUpThePackage.remove(offerMail.replace(".", "|"));
            updateStatus();
        }
    }

    public void confirmOffer(String offerMail) {
        _offerToPickUpThePackage.put(offerMail.replace(".", "|"), "T");
        updateStatus();
    }

    public void unConfirmOffer(String offerMail) {
        _offerToPickUpThePackage.put(offerMail.replace(".", "|"), "F");
        updateStatus();
    }

    public Boolean get_isDelivered() {
        return _isDelivered;
    }

    public void set_isDelivered(Boolean _isDelivered) {
        this._isDelivered = _isDelivered;
        updateStatus();
    }

    public String get_registerTime() {
        return _registerTime;
    }

    public void set_registerTime(String _registerTime) {
        this._registerTime = _registerTime;
    }

    private void updateStatus() {
        if(_mailOfDeliver.length() > 0 && _mailOfDeliver.trim().equals(_recipientMail.trim()) && _isDelivered){
            _status = Status.DELIVERED;
            return;
        }

        if(!(_offerToPickUpThePackage.size() >= 1)){
            _status = Status.REGISTERED;
            return;
        }

        if(_offerToPickUpThePackage.size() >= 1){
            if(_offerToPickUpThePackage.containsValue("T") &&  _mailOfDeliver.length() > 0 && _isDelivered){
                _status = Status.DELIVERED;
                return;
            }

            if(_offerToPickUpThePackage.containsValue("T") && _mailOfDeliver.length() > 0 && !_isDelivered){
                _status = Status.ON_THE_WAY;
                return;
            }

            if(_offerToPickUpThePackage.containsValue("T") && (_mailOfDeliver == null || !(_mailOfDeliver.length() > 0)) && !_isDelivered){
                _status = Status.COLLECTION_APPROVAL;
                return;
            }

            if(!(_offerToPickUpThePackage.containsValue("T")) && (_mailOfDeliver == null || !(_mailOfDeliver.length() > 0)) && !_isDelivered){
                _status = Status.COLLECTION_OFFERED;
                return;
            }
        }
    }
}
