package co.thanker.utils;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import co.thanker.R;

public class TextUtils {

    public TextUtils(){

    }

    public static boolean isEmail(Context context, String text){
        return(
                (text.contains(".com")
                || text.contains(".net")
                || text.contains(".org")
                || text.contains(".xyz")
                || text.contains(".io")
                || text.contains(".pt")
                || text.contains(".es")
                || text.contains(".co")
                || text.contains(".us")
                || text.contains(".ru")
                || text.contains(".br")
                || text.contains(".cn")
                || text.contains(".uk"))
                && text.contains(context.getString(R.string.at))
                );
    }

    public static String getStringFromArray(String [] stringArray){
        int size = stringArray.length;
        int randomIndex = new Random().nextInt(size);

        return stringArray[randomIndex];
    }

    public static String firstLetterCapital(String string){
        String result = ("" + string.charAt(0)).toUpperCase();

        for(int i = 1; i != string.length(); i++){
            result += string.charAt(i);
        }
        return result;
    }

    public static String[] removePhoneCode(Context context, String number){
        String [] resultArray = new String[2];
        String result = "";
        String [] phoneCodes = context.getResources().getStringArray(R.array.DialingCountryCode);

        List<String> listPhoneCodes = new ArrayList<>();
        List<String> listCountryCodes = new ArrayList<>();

        for(String code: phoneCodes){
            String [] codeParts = code.split(",");
            listPhoneCodes.add(codeParts[0]);
            listCountryCodes.add(codeParts[1]);
        }

        boolean found = false;
        int index = -1;
        for(int i = 0; i != listPhoneCodes.size() && !found; i++){
            int length = listPhoneCodes.get(i).length();
            String extractCode = number.substring(0, (length));
            Log.v("TextUtils", "Sms Contacts. RemovePhoneCode. Phone Code: " + listPhoneCodes.get(i) + ", ExtractCode: " + extractCode);
            if(extractCode.equals(listPhoneCodes.get(i))){
                result = number.substring(length);
                result = removeOtherChars(result);
                index = i;
                found = true;
            }
        }

        resultArray[0] = result;

        if(index > 0){
            resultArray[1] = listCountryCodes.get(index);
            Log.v("TextUtils", "Sms Contacts. RemovePhoneCode. Got country code: " + resultArray[1]);
        }

        return resultArray;
    }

    public static String removeOtherChars(String string){
        String result = "";
        string = string.trim();

        for(int i = 0; i != string.length(); i++){
            if(string.charAt(i) >= '0' && string.charAt(i) <= '9'){
                result += string.charAt(i);
            }
        }
        return result;
    }


    public static String generateRandomCuteness(Context context) {
        String [] arrayMessages = context.getResources().getStringArray(R.array.group_cute_messages);
        int index = new Random().nextInt(arrayMessages.length);

        return arrayMessages[index];
    }

    public static int countAts(String text) {
        int number = 0;

        for(int i = 0; i != text.length(); i++){
            if(text.charAt(i) == '@'){
                number++;
            }
        }

        return number;
    }

    public static SpannableString underlineText(String text){
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        return content;
    }

    public static int yearIndexDateCode(String string){
        int i = 0;

        for( ; i != string.length(); i++){
            if (string.charAt(i) == '_'){
                return (i + 1);
            }
        }

        return -1;
    }

    public static String monthSubstringDateCode(String dateCode){
        String result = "";
        boolean keepReading = true;

        for(int i = 0; i != dateCode.length() && keepReading; i++){
            if(dateCode.charAt(i) != '_'){
                result += dateCode.charAt(i);
            }

            else {
                keepReading = false;
            }
        }

        return result;
    }

    public static String replaceSignals(String country) {
        String result = "";

        for(int i = 0; i != country.length(); i++){
            if(country.charAt(i) == '.'){
                result += ",";
            }
            else {
                result += country.charAt(i);
            }
        }

        return result;
    }

    public static String putSignals(String country) {
        String result = "";

        for(int i = 0; i != country.length(); i++){
            if(country.charAt(i) == ','){
                result += ".";
            }
            else {
                result += country.charAt(i);
            }
        }

        return result;
    }

    public static String removePlus(String s) {
        String result = "";

        for(int i = 0; i != s.length(); i++){
            if(s.charAt(i) != '+'){
                result += s.charAt(i);
            }
        }
        return result;
    }
}
