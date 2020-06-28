package il.co.yedidia_shmuel.ddb_2.util;

import androidx.room.TypeConverter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import il.co.yedidia_shmuel.ddb_2.model.entities.MyAddress;
import il.co.yedidia_shmuel.ddb_2.model.entities.PackageType;
import il.co.yedidia_shmuel.ddb_2.model.entities.PackageWeight;
import il.co.yedidia_shmuel.ddb_2.model.entities.Status;

public class Convert {
    @TypeConverter
    public static String typeToString(PackageType type)
    {
        if(type.equals(PackageType.BIG_PACKAGE))
            return "BIG_PACKAGE";
        if(type.equals((PackageType.ENVELOP)))
            return "ENVELOP";
        return "SMALL_PACKAGE";
    }

    @TypeConverter
    public static PackageType stringToType(String string)
    {
        if(string.equals("BIG_PACKAGE"))
            return PackageType.BIG_PACKAGE;
        if(string.equals("ENVELOP"))
            return PackageType.ENVELOP;
        return PackageType.SMALL_PACKAGE;
    }

    @TypeConverter
    public static String statusToString(Status status) {
        if(status.equals(Status.REGISTERED)){
            return "REGISTERED";
        }else if(status.equals(Status.COLLECTION_OFFERED)){
            return "COLLECTION_OFFERED";
        }else if(status.equals(Status.COLLECTION_APPROVAL)){
            return "COLLECTION_APPROVAL";
        }else if(status.equals(Status.ON_THE_WAY)){
            return "ON_THE_WAY";
        }else{
            return "DELIVERED";
        }
    }

    @TypeConverter
    public static Status stringToStatus(String string) {
       if(string.equals("REGISTERED")){
           return Status.REGISTERED;
       }else if(string.equals("COLLECTION_OFFERED")){
           return Status.COLLECTION_OFFERED;
       }else if(string.equals("COLLECTION_APPROVAL")){
           return Status.COLLECTION_APPROVAL;
       }else if(string.equals("ON_THE_WAY")){
           return Status.ON_THE_WAY;
       }else{
           return Status.DELIVERED;
       }
    }

    @TypeConverter
    public static String weightToString(PackageWeight weight)
    {
        if(weight.equals(PackageWeight.UNDER_500_GR))
            return "UNDER_500_GR";
        if(weight.equals(PackageWeight.UNDER_1_KG))
            return "UNDER_1_KG";
        if(weight.equals(PackageWeight.UNDER_5_KG))
            return "UNDER_5_KG";
        if(weight.equals(PackageWeight.UNDER_20_KG))
            return "UNDER_20_KG";
        return "OVER_20_KG";
    }

    @TypeConverter
    public static PackageWeight stringToSWeight(String string)
    {
        if(string.equals("UNDER_500_GR"))
            return PackageWeight.UNDER_500_GR;
        if(string.equals("UNDER_1_KG"))
            return PackageWeight.UNDER_1_KG;
        if(string.equals("UNDER_5_KG"))
            return PackageWeight.UNDER_5_KG;
        if(string.equals("UNDER_20_KG"))
            return PackageWeight.UNDER_20_KG;
        return PackageWeight.OVER_20_KG;
    }

    @TypeConverter
    public static String myAddressToString(MyAddress myAddress){
        if(myAddress != null){
            String longitude = Double.toString(myAddress.get_longitude());
            String latitude = Double.toString(myAddress.get_latitude());
            String address = myAddress.get_address() + "@" + longitude + "@" + latitude;
            return address;
        }else{
            return "";
        }
    }

    @TypeConverter
    public static MyAddress stringToMyAddress(String arr){
        if(arr.length() > 0){
            String[] str = arr.split("@");
            MyAddress address = new MyAddress(str[0],Double.parseDouble(str[1]),Double.parseDouble(str[2]));
            return address;
        }
        return new MyAddress();
    }

    @TypeConverter
    public static String offerToPickUpThePackageToString(ArrayList<String> s){
        StringBuilder sb = new StringBuilder();
        for (String offer : s)
        {
            sb.append(offer);
            sb.append("|");
        }
        return sb.toString();
    }

    @TypeConverter
    public static ArrayList<String> stringToOfferToPickUpThePackage(String s){
        return new ArrayList<String>(Arrays.asList(s.split("|")));
    }


    @TypeConverter
    public static String mapToString(Map<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();
        if(!map.isEmpty()){
            for (String key : map.keySet()) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append("&");
                }
                String value = map.get(key);
                try {
                    stringBuilder.append((key != null ? URLEncoder.encode(key, "UTF-8") : ""));
                    stringBuilder.append("=");
                    stringBuilder.append(value != null ? URLEncoder.encode(value, "UTF-8") : "");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("This method requires UTF-8 encoding support", e);
                }
            }
        }
        return stringBuilder.toString();
    }


    @TypeConverter
    public static Map<String, String> stringToMap(String input) {
        Map<String, String> map = new HashMap<String, String>();
        if(!input.isEmpty()){
            String[] nameValuePairs = input.split("&");
            for (String nameValuePair : nameValuePairs) {
                String[] nameValue = nameValuePair.split("=");
                try {
                    map.put(URLDecoder.decode(nameValue[0], "UTF-8"), nameValue.length > 1 ? URLDecoder.decode(
                            nameValue[1], "UTF-8") : "");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("This method requires UTF-8 encoding support", e);
                }
            }
        }
        return map;
    }
}
