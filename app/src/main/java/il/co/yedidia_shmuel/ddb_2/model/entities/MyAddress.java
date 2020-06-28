package il.co.yedidia_shmuel.ddb_2.model.entities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class MyAddress implements Serializable {
    private String _address;
    private double _latitude;
    private double _longitude;

    public MyAddress() { }

    public MyAddress(String address,double longitude, double latitude){
        _address = address;
        _latitude = latitude;
        _longitude = longitude;
    }

    public String get_address() {
        return _address;
    }

    public void set_address(String _address) {
        this._address = _address;
    }

    public double get_latitude() {
        return _latitude;
    }

    public void set_latitude(double _latitude) {
        this._latitude = _latitude;
    }

    public double get_longitude() {
        return _longitude;
    }

    public void set_longitude(double _longitude) {
        this._longitude = _longitude;
    }


    public static MyAddress locationByAddress(Context context,String address) throws Exception {
        Geocoder coder = new Geocoder(context);
        List<Address> listAddress = coder.getFromLocationName(address, 1);
        if(listAddress != null && listAddress.size() > 0){
            Address location = listAddress.get(0);
            return new MyAddress(addressByLocation(context, location.getLatitude(), location.getLongitude()),
                    location.getLongitude(),location.getLatitude());
        }else {
            throw new Exception("Fail to find your address, please enter City, Street, num of build");
        }
    }

    public static String addressByLocation(Context context, double latitude, double longitude) throws Exception {
        List<android.location.Address> addresses;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        addresses = geocoder.getFromLocation(latitude, longitude, 1);
        if(addresses != null && addresses.size() > 0){
            return addresses.get(0).getAddressLine(0);
        }else{
            throw new Exception("Fail to find your location");
        }
    }
}
