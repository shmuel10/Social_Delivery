package il.co.yedidia_shmuel.ddb_2.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {
    public static boolean isIdValid(String id){
        if(id != null && !id.contains("#") && id.length() == 9) {
            char[] theID = new char[id.length()];
            for (int i = 0; i < id.length(); i++) {
                theID[i] = id.charAt(i);
            }


            int[] iD = new int[9];

            for (int i = 0; i < 9; ++i)
            {
                iD[i] = Character.getNumericValue(theID[i]);
            }

            int[] arr = new int[9];

            for (int i = 8; i >= 0; --i)
            {
                if (i % 2 == 0)
                    arr[i] = 1;
                if (i % 2 != 0)
                    arr[i] = 2;
            }

            for (int i = 0; i <= 8; ++i)
            {
                int tmp = iD[i] * arr[i];
                iD[i] = tmp / 10 + tmp % 10;
            }

            int count = 0;
            for (int i = 0; i < 9; ++i)
            {
                count += iD[i];
            }


            return count % 10 == 0;
        }else if(id.contains("#")){
            return true;
        }
        return false;
    }

    public static boolean isMailValid (String mail) {
        if(mail != null && !mail.contains("@")){
            return true;
        }else if(mail != null && !(mail.contains(".com") || mail.contains(".co.il") || mail.contains(".net.il") ||
                mail.contains(".ac.il"))){
            return true;
        }
        return mail != null && mail.isEmpty();
    }

    public static boolean isBirthdayValid(String birthday) {
        return birthday != null && birthday.isEmpty();
    }

    public static boolean isAgeValid(String age) {
        return age != null && age.isEmpty();
    }

    public static boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber != null && (phoneNumber.length() == 10) &&
                phoneNumber.startsWith("05");
    }

    public static boolean isPhoneNumberEmpty(String phoneNumber) {
        return phoneNumber != null && phoneNumber.isEmpty();
    }

    public static boolean isFirstNameEmpty(String firstName) {
        return firstName != null && firstName.isEmpty();
    }

    public static boolean isLastNameEmpty(String lastName) {
        return lastName != null && lastName.isEmpty();
    }

    public static boolean isAddressEmpty(String fromAddress) {
        return fromAddress != null && fromAddress.isEmpty();
    }

    public static boolean isPackageTypeChosen(String packageType) {
        return packageType != null && packageType == "Select_package_type";
    }

    public static boolean isPackageWeightChosen(String packageWeight) {
        return packageWeight != null && packageWeight == "Select_package_weight";
    }

    public static boolean isGenderChosen (String gender){
        return gender != null && gender == "Select_gender";
    }

    public static boolean isDistanceValid(int distance){
        return  distance <= 5;
    }

    public static boolean isPasswordCorrect (String password){
        if(password.length() <= 5){
            return true;
        }
        Pattern p = Pattern.compile("[A-Za-z0-9]");
        Matcher m = p.matcher(password);
        return password != null && !m.find();
    }
}
