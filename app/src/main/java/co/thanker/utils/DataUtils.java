package co.thanker.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import co.thanker.MainActivity;
import co.thanker.R;
import co.thanker.data.CountryDateCategoryValue;
import co.thanker.data.FriendData;
import co.thanker.data.FriendRank;
import co.thanker.data.IdData;
import co.thanker.data.IdEmail;
import co.thanker.data.IdEmailText;
import co.thanker.data.Message;
import co.thanker.data.NameId;
import co.thanker.data.StatsThanks;
import co.thanker.data.Thanks;
import co.thanker.data.ThanksData;
import co.thanker.data.ThanksItem;
import co.thanker.data.ThanksValue;
import co.thanker.data.User;
import co.thanker.data.UserFriend;
import co.thanker.data.UserResult;
import co.thanker.data.UserSnippet;

public class DataUtils {

    private static final String TAG = DataUtils.class.getSimpleName();
    private static final String DB_REFERENCE = "users";
    private static final String MESSAGES_REFERENCE = "messages-list";
    public static final int MSG_SEE_MY_TOP = 1;
    public static final int MSG_SEE_OTHER_TOP = 2;
    public static final int MSG_SEE_PREMIUM = 3;


    //to add a number to a Thanks Type
    public static int addToThanksType(String type){
        int toAdd = 0;

        switch(type){
            case "NORMAL": toAdd = 1; break;
            case "SUPER": toAdd = 10; break;
            case "MEGA": toAdd = 100; break;
            case "POWER": toAdd = 1000; break;
            case "ULTRA": toAdd = 10000; break;
        }

        return toAdd;
    }

    public static int retrieveItem(List<String> strings, String target){

        for(int i = 0; i != strings.size(); i++){
            if(strings.get(i).equalsIgnoreCase(target)){
                return i;
            }
        }

        if(strings.size() > 0){
            return (strings.size() - 1);
        }

        else {
            return 0;
        }

    }

    public static boolean doesListContainItem(List<String> list, String item){

        for(String string: list){
            if(string.equalsIgnoreCase(item)){
                return true;
            }
        }

        return false;
    }

    public static boolean doesListContainUser(List <User> listUsers, User userData){

        if(userData != null){
            for(User user: listUsers){
                if(user.getUserId() != null && userData.getUserId() != null){
                    if(user.getUserId().equals(userData.getUserId()) || user.getEmail().contains(userData.getEmail())){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean doesListContainUserEmail(List <User> listUsers, User userData){

        if(userData != null){
            for(User user: listUsers){
                if(user != null){
                    if(user.getEmail().contains(userData.getEmail())){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean doesListContainEmail(List<IdEmail> list, String email){
        for(IdEmail item: list){
            if(item.getEmail().trim().equalsIgnoreCase(email.trim())){
                return true;
            }
        }
        return false;
    }

    public static void terminateActivity(Activity activity) {
        Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityCompat.finishAffinity(activity);
        activity.startActivity(intent);
        //activity.finishAffinity();
    }

    /*public static String retrieveCountry(Context context, User user){
        String currentCountry = "";

        if(context.getApplicationContext() != null) {
            currentCountry = context.getApplicationContext().getResources().getConfiguration().locale.getDisplayCountry();
        }


        if(currentCountry != null) {
            if (currentCountry.equals("")) {
                currentCountry = user.getLivingCountry();
            }
        }
        else {
            currentCountry = user.getLivingCountry();
        }

        return currentCountry;
    }*/


    public static List<String> retrieveThanksTypesInList(){
        List<String> result = new ArrayList<>();

        result.add("personThanks");
        result.add("brandThanks");
        result.add("businessThanks");
        result.add("natureThanks");
        result.add("healthThanks");
        result.add("foodThanks");
        result.add("associationThanks");
        result.add("homeThanks");
        result.add("scienceThanks");
        result.add("religionThanks");
        result.add("sportsThanks");
        result.add("lifestyleThanks");
        result.add("techThanks");
        result.add("fashionThanks");
        result.add("educationThanks");
        result.add("gamesThanks");
        result.add("travelThanks");
        result.add("govThanks");
        result.add("beautyThanks");
        result.add("financeThanks");
        result.add("cultureThanks");

        return result;
    }

    public static String thanksCategoryToStringCategory(String s) {
        String result = "";

        if (s != null) {
            switch (s) {
                case "personThanks":
                    result = "People";
                    break;
                case "brandThanks":
                    result = "Brands";
                    break;
                case "businessThanks":
                    result = "Business";
                    break;
                case "natureThanks":
                    result = "Nature";
                    break;
                case "healthThanks":
                    result = "Health";
                    break;
                case "foodThanks":
                    result = "Food";
                    break;
                case "associationThanks":
                    result = "Associations";
                    break;
                case "homeThanks":
                    result = "Home";
                    break;
                case "scienceThanks":
                    result = "Science";
                    break;
                case "religionThanks":
                    result = "Religion";
                    break;
                case "sportsThanks":
                    result = "Sports";
                    break;
                case "lifestyleThanks":
                    result = "Lifestyle";
                    break;
                case "techThanks":
                    result = "Technology";
                    break;
                case "fashionThanks":
                    result = "Fashion";
                    break;
                case "educationThanks":
                    result = "Education";
                    break;
                case "gamesThanks":
                    result = "Games";
                    break;
                case "travelThanks":
                    result = "Travel";
                    break;
                case "govThanks":
                    result = "Institutional";
                    break;
                case "beautyThanks":
                    result = "Beauty";
                    break;
                case "cultureThanks":
                    result = "Culture";
                    break;
                case "financeThanks":
                    result = "Finance";
                    break;
            }
        }

        return result;
    }

    public static String categoryToString(String category){
        String result = "";

        switch (category) {
            case "Person":
            case "People":
                result = "personThanks";
                break;
            case "Brand":
            case "Brands":
                result = "brandThanks";
                break;
            case "Business":
                result = "businessThanks";
                break;
            case "Nature":
                result = "natureThanks";
                break;
            case "Health":
                result = "healthThanks";
                break;
            case "Food":
                result = "foodThanks";
                break;
            case "Association":
            case "Associations":
                result = "associationThanks";
                break;
            case "Home":
                result = "homeThanks";
                break;
            case "Science":
                result = "scienceThanks";
                break;
            case "Religion":
                result = "religionThanks";
                break;
            case "Sports":
                result = "sportsThanks";
                break;
            case "Lifestyle":
                result = "lifestyleThanks";
                break;
            case "Technology":
                result = "techThanks";
                break;
            case "Fashion":
                result = "fashionThanks";
                break;
            case "Education":
                result = "educationThanks";
                break;
            case "Games":
                result = "gamesThanks";
                break;
            case "Travel":
                result = "travelThanks";
                break;
            case "Institutional":
                result = "govThanks";
                break;
            case "Beauty":
                result = "beautyThanks";
                break;
            case "Finance":
                result = "financeThanks";
                break;
            case "Culture":
                result = "cultureThanks";
                break;
        }

        return result;
    }

    public static String thanksTypeToString(Context context, String thanksType) {
        String type = "";
        if (thanksType != null) {
            if (!thanksType.equals("")) {
                switch (thanksType) {
                    case "NORMAL":
                        type = context.getString(R.string.thanked_colour);
                        break;
                    case "SUPER":
                        type = context.getString(R.string.super_thanked_colour);
                        break;
                    case "MEGA":
                        type = context.getString(R.string.mega_thanked_colour);
                        break;
                    case "POWER":
                        type = context.getString(R.string.power_thanked_colour);
                        break;

                    case "ULTRA":
                        type = context.getString(R.string.ultra_thanked_colour);
                        break;
                }
            }
        }
        return type;
    }

    public static long thanksTypeToLong(Thanks thanks) {
        int value = 0;

        switch (thanks.getThanksType()) {
            case "NORMAL":
                value = 1;
                break;
            case "SUPER":
                value = 10;
                break;
            case "MEGA":
                value = 100;
                break;
            case "POWER":
                value = 1000;
                break;

            case "ULTRA":
                value = 10000;
                break;
        }

        return value;
    }

    public static boolean isPunctuation(char c){
        return (c == '.' || c == '!' || c == '?');
    }

    public static String capitalize(String string){
        if(string != null){
            string = string.trim();
            String result = ("" + string.charAt(0)).toUpperCase();

            for(int i = 1; i != string.length(); i++){
                if(string.charAt(i-1) == ' '){
                    result += ("" + string.charAt(i)).toUpperCase();
                }
                else {
                    result += string.charAt(i);
                }
            }

            return result;
        }

        else {
            return "";
        }
    }

    public static String decapitalize(String string){
        string = string.trim();
        String result = "";

        if(string.length() > 0){
            result = ("" + string.charAt(0)).toUpperCase();

            for(int i = 1; i != string.length(); i++){
                result += ("" + string.charAt(i)).toLowerCase();
            }
        }

        return result;
    }

    public static String getDateString(Context context, long dateLong){
        Date date = new Date(dateLong);
        Date now = new Date(System.currentTimeMillis());
        String dateToPresent;

        Calendar thenCal = Calendar.getInstance();
        Calendar nowCal = Calendar.getInstance();

        thenCal.setTime(date);
        nowCal.setTime(now);
        Calendar helpThenCal = thenCal;
        Calendar helpNowCal = nowCal;

        helpThenCal.set(Calendar.HOUR_OF_DAY, 10);
        helpNowCal.set(Calendar.HOUR_OF_DAY, 10);

        if(helpThenCal.get(Calendar.MONTH) == helpNowCal.get(Calendar.MONTH) &&
                helpThenCal.get(Calendar.YEAR) == helpNowCal.get(Calendar.YEAR) &&
                helpThenCal.get(Calendar.DAY_OF_MONTH) == helpNowCal.get(Calendar.DAY_OF_MONTH)) {
            dateToPresent = capitalize(context.getString(R.string.today));
            Log.v(TAG, "Difference in days. I'm in Today: " + TimeUnit.MILLISECONDS.toDays(helpNowCal.getTimeInMillis() - helpThenCal.getTimeInMillis()));
        }

        else if (helpThenCal.get(Calendar.MONTH) == helpNowCal.get(Calendar.MONTH) &&
                helpThenCal.get(Calendar.YEAR) == helpNowCal.get(Calendar.YEAR) &&
                (helpThenCal.get(Calendar.DAY_OF_MONTH) + 1) == helpNowCal.get(Calendar.DAY_OF_MONTH))
        /*(Math.round(TimeUnit.MILLISECONDS.toDays(helpNowCal.getTimeInMillis() - helpThenCal.getTimeInMillis())) >= 0 &&
                Math.round(TimeUnit.MILLISECONDS.toDays(helpNowCal.getTimeInMillis() - helpThenCal.getTimeInMillis())) <= 1)*/{
            Log.v(TAG, "Difference in days. I'm in Yesterday: " + TimeUnit.MILLISECONDS.toDays(helpNowCal.getTimeInMillis() - helpThenCal.getTimeInMillis()));
            dateToPresent = context.getString(R.string.yesterday);
            Log.v(TAG, "Checking Calendar Utils. helpThen day " + helpThenCal.get(Calendar.DAY_OF_MONTH));
        }

        else {
            Log.v(TAG, "Difference in days. I'm in Other Dates: " + TimeUnit.MILLISECONDS.toDays(helpNowCal.getTimeInMillis() - helpThenCal.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            dateToPresent = capitalize(sdf.format(date));
        }

        return (dateToPresent);
    }

    public static void sortThanksByDateDesc(List<Thanks> listThanks){
        Collections.sort(listThanks, new Comparator<Thanks>() {
            @Override
            public int compare(Thanks o1, Thanks o2) {
                return Long.compare(o2.getDate(), o1.getDate());
            }
        });
    }

    public static void createMessage(String userId, String title, String text, String fromUserId, int type){
        final CollectionReference messageCollection = FirebaseFirestore.getInstance().collection(MESSAGES_REFERENCE);
        Message message = new Message(title, text, fromUserId, userId, type);
        messageCollection.add(message).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                if(documentReference != null){
                    String key = documentReference.getId();
                    documentReference.update("key", key);
                }
            }
        });
    }

    public static void sendMessageFromThanks(Context context, String receiverId, String giverName, String giverId, Thanks thanks, int msgType){
        //Send message if the Thanks are Special Ones

        String type = thanks.getThanksType().trim();

        if(type.equals("SUPER") || type.equals("MEGA") || type.equals("POWER")){

            String title = capitalize(giverName);
            String body = "";

            switch(type){
                case "SUPER":
                    title += " " + context.getString(R.string.sent_you_super_thanks);
                    body = context.getString(R.string.body_message_super_thanks);
                    break;

                case "MEGA":
                    title += " " + context.getString(R.string.sent_you_mega_thanks);
                    body = context.getString(R.string.body_message_mega_thanks);
                    break;

                case "POWER":
                    title += " " + context.getString(R.string.sent_you_power_thanks);
                    body = context.getString(R.string.body_message_power_thanks);
                    break;

                case "ULTRA":
                    title += " " + context.getString(R.string.sent_you_ultra_thanks);
                    body = context.getString(R.string.body_message_ultra_thanks);
                    break;

                default: break;
            }

            if(!title.equals("") && !body.equals("")){
                DataUtils.createMessage(receiverId, title, body, giverId, msgType);
            }
        }

    }

    public static String generateDateCode() {

        String dateCodeString = "";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat day = new SimpleDateFormat("dd");
        SimpleDateFormat month = new SimpleDateFormat("MMMM", Locale.US);
        SimpleDateFormat year = new SimpleDateFormat("YYYY");

        String dayString = day.format(cal.getTime());
        String monthString = month.format(cal.getTime()).toLowerCase();
        String yearString = year.format(cal.getTime());

        dateCodeString = monthString + "_" + yearString;

        return dateCodeString;
    }

    public static String generateDateCode(Calendar cal) {

        String dateCodeString = "";
        SimpleDateFormat day = new SimpleDateFormat("dd");
        SimpleDateFormat month = new SimpleDateFormat("MMMM", Locale.US);
        SimpleDateFormat year = new SimpleDateFormat("YYYY");

        String dayString = day.format(cal.getTime());
        String monthString = month.format(cal.getTime()).toLowerCase();
        String yearString = year.format(cal.getTime());

        dateCodeString = monthString + "_" + yearString;

        return dateCodeString;
    }

    public static String generateYear(Calendar cal){
        String year = "";

        SimpleDateFormat yearFormat = new SimpleDateFormat("YYYY");
        year = yearFormat.format(cal.getTime());

        return year;
    }

    public static String generateMonth(Calendar cal){
        String month = "";

        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.US);
        month = monthFormat.format(cal.getTime());

        return month;
    }

    public static String generateYear(){
        String year = "";

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat yearFormat = new SimpleDateFormat("YYYY");
        year = yearFormat.format(cal.getTime());

        return year;
    }

    public static String generateMonth(){
        String month = "";

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.US);
        month = monthFormat.format(cal.getTime());

        return month;
    }

    public static String generateDay(){
        String day = "";

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        day = dayFormat.format(cal.getTime());

        return day;
    }

    public static String generateDay(Calendar cal){
        String day = "";

        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        day = dayFormat.format(cal.getTime());

        return day;
    }

    public static String getLocationCountry(Context context, double lattitude, double longitude) {

        String results [] = new String[2];
        String cityName = "Not Found";
        String countryName = "Not Found";
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        try {

            List<Address> addresses = gcd.getFromLocation(lattitude, longitude,
                    10);

            for (Address adrs : addresses) {
                if (adrs != null) {

                    String city = adrs.getLocality();
                    String country = adrs.getCountryName();
                    if (city != null && !city.equals("") && country != null && !country.equals("")) {
                        cityName = city;
                        countryName = country;
                        Log.v("DataUtils", "city ::  " + cityName);
                    } else {

                    }
                    // // you should also try with addresses.get(0).toSring();

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        results[0] = cityName;
        results[1] = countryName;

        return results[1];

    }

    public static String getTranslatedString(Context context, String pageName) {

        String result = "";

        int stringId = context.getResources().getIdentifier(pageName, "string", context.getPackageName());

        //String [] names = context.getResources().getStringArray(R.array.translatable_pages);

        result = context.getResources().getString(stringId);
        Log.v("DataUtils", "Translatable pages. Page is confirmed Conceptual. Name not found");

        return result;
    }

    @NonNull
    public static Resources getEnglishResources(Context context) {
        Configuration conf = context.getResources().getConfiguration();
        conf = new Configuration(conf);
        conf.setLocale(new Locale("en"));
        Context localizedContext = context.createConfigurationContext(conf);
        return localizedContext.getResources();
    }

    public static List<String> getTranslatableStrings(Context context){
        List<String> results = new ArrayList<>();
        String [] arrayPages = getEnglishResources(context).getStringArray(R.array.translatable_pages);

        for(String word: arrayPages){
            results.add(word);
            Log.v(TAG, "Added word to results: " + word);
        }

        return results;
    }

    public static String translate(Context context, String word){
        List<String> results = new ArrayList<>();
        String [] arrayInOwnLanguage = context.getResources().getStringArray(R.array.translatable_pages);
        List<String> possibleResults = getTranslatableStrings(context);

        for(int i = 0; i != arrayInOwnLanguage.length; i++){
            if(word.toUpperCase().equals(arrayInOwnLanguage[i].toUpperCase())){
                return possibleResults.get(i);
            }
        }

        return "";
    }

    public static String translateToOwnLanguage(Context context, String word){

        List<String> englishStrings = DataUtils.getTranslatableStrings(context);
        String [] ourLanguageStrings = context.getResources().getStringArray(R.array.translatable_pages);

        if(word.toUpperCase().equals("PERSON")){
            word = "PEOPLE";
        }

        else if(word.toUpperCase().equalsIgnoreCase("Brand")){
            word = "Brands";
        }

        else if(word.equalsIgnoreCase("Association")){
            word = "Associations";
        }

        for(int i = 0; i != englishStrings.size(); i++){
            if(word.equalsIgnoreCase(englishStrings.get(i))){
                return ourLanguageStrings[i];
            }
        }

        return "";
    }

    public static String translateToEnglish(Context context, String categoryString) {
        String result = "";

        Log.v(TAG, "Going to translate: " + categoryString);

        Configuration conf = context.getResources().getConfiguration();
        conf = new Configuration(conf);
        conf.setLocale(new Locale("en"));
        Context englishContext = context.createConfigurationContext(conf);

        if (categoryString.equals("")) {
            result = categoryString;
        } else if (categoryString.equalsIgnoreCase(context.getString(R.string.person))) {
            result = englishContext.getString(R.string.person);
        } else if (categoryString.equalsIgnoreCase(context.getString(R.string.brand))) {
            result = englishContext.getString(R.string.brand);
        } else if (categoryString.equalsIgnoreCase(context.getString(R.string.association))) {
            result = englishContext.getString(R.string.association);
        } else {
            String[] stringArray = context.getResources().getStringArray(R.array.translatable_pages);
            String[] englishArray = englishContext.getResources().getStringArray(R.array.translatable_pages);
            boolean found = false;

            for (int i = 0; i != stringArray.length && !found; i++) {
                if (categoryString.equalsIgnoreCase(stringArray[i])) {
                    result = DataUtils.decapitalize(englishArray[i]);
                    found = true;
                }
            }
        }

        Log.v(TAG, "Going to translate: " + categoryString + ". Translated to: " + result);

        return result;
    }

    public static String translateAndFormat(Context context, String word){
        return decapitalize(translateToOwnLanguage(context, word));
    }

    public static String getTwoCodeCountry(String country){
        Locale [] locales = Locale.getAvailableLocales();

        for(Locale loc : locales){
            String compareCountry = loc.getDisplayCountry();
            if(country.equalsIgnoreCase(compareCountry)){
                return loc.getCountry();
            }
        }
        return null;
    }

    public static boolean existsInCountryThanksCodes(List<CountryDateCategoryValue> list, CountryDateCategoryValue c){
        for(CountryDateCategoryValue code: list){
            if(code.isSameCDCV(c)){
                return true;
            }
        }
        return false;
    }

    public static CountryDateCategoryValue getCorrectThanksCode(List<CountryDateCategoryValue> list, CountryDateCategoryValue c){
        CountryDateCategoryValue nullResult = null;

        for(CountryDateCategoryValue item: list){
            if(item.isSameCDCV(c)){
                return item;
            }
        }

        return nullResult;
    }

    public static int getCorrectThanksCodeIndex(List<CountryDateCategoryValue> list, CountryDateCategoryValue c){
        int index = 0;

        for(CountryDateCategoryValue item: list){
            if(item.isSameCDCV(c)){
                return index;
            }
            index++;
        }

        return -1;
    }

    public static boolean isThereThanksCodeInstance(List<CountryDateCategoryValue> list, CountryDateCategoryValue c){

        for(CountryDateCategoryValue item: list){
            if(item.getCountry().equals(c.getCountry())
                && item.getDate().equals(c.getDate())
                && item.getDay().equals(c.getDay())) {

                return true;
            }
        }

        return false;
    }

    public static boolean doesListContainThanksUser(List<Thanks> listThanks, String userId) {

        for(Thanks item: listThanks){
            if(item.getFromUserId().equals(userId)){
                return true;
            }
        }

        return false;
    }

    public static boolean doesListContainUserFriend(List<UserFriend> friendsList, User user) {
        for(UserFriend friend: friendsList){
            if(friend.getUser().getUserId().equals(user.getUserId())){
                return true;
            }
        }

        return false;
    }

    public static boolean doesListContainNameId(List<NameId> list, NameId item) {

        if(item.getId() != null){
            for(NameId nameId: list){
                if(nameId.getId().equals(item.getId())){
                    if(item.getName().length() <= nameId.getName().length())
                    return true;
                }
            }
        }

        else {
            if(item.getName().contains("@")){
                for(NameId nameId: list){
                    if(nameId.getName().toLowerCase().contains(item.getName().toLowerCase())
                        || item.getName().toLowerCase().contains(nameId.getName().toLowerCase())){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean doesListContainUserResult(List<UserResult> list, UserResult user) {

        if(user.getUserId() != null){
            for(UserResult item: list){
                if(user.getUserId().equals(item.getUserId())){
                    if(item.getName().length() >= user.getName().length())
                        return true;
                }
            }
        }

        else {
            if(user.getEmail().contains("@")){
                for(UserResult item: list){
                    if(user.getEmail().toLowerCase().contains(item.getEmail().toLowerCase())
                            || item.getEmail().toLowerCase().contains(user.getEmail().toLowerCase())){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static List<ThanksValue> getSortedThanksValues(StatsThanks stats){
        List<ThanksValue> thanksValues = new ArrayList<>();
        thanksValues.add(new ThanksValue("personThanks", stats.getPersonThanks()));
        thanksValues.add(new ThanksValue("brandThanks", stats.getBrandThanks()));
        thanksValues.add(new ThanksValue("businessThanks", stats.getBusinessThanks()));
        thanksValues.add(new ThanksValue("natureThanks", stats.getNatureThanks()));
        thanksValues.add(new ThanksValue("healthThanks", stats.getHealthThanks()));
        thanksValues.add(new ThanksValue("foodThanks", stats.getFoodThanks()));
        thanksValues.add(new ThanksValue("associationThanks", stats.getAssociationThanks()));
        thanksValues.add(new ThanksValue("homeThanks", stats.getHomeThanks()));
        thanksValues.add(new ThanksValue("scienceThanks", stats.getScienceThanks()));
        thanksValues.add(new ThanksValue("religionThanks", stats.getReligionThanks()));
        thanksValues.add(new ThanksValue("sportsThanks", stats.getSportsThanks()));
        thanksValues.add(new ThanksValue("lifestyleThanks", stats.getLifestyleThanks()));
        thanksValues.add(new ThanksValue("techThanks", stats.getTechThanks()));
        thanksValues.add(new ThanksValue("fashionThanks", stats.getFashionThanks()));
        thanksValues.add(new ThanksValue("educationThanks", stats.getEducationThanks()));
        thanksValues.add(new ThanksValue("gamesThanks", stats.getGamesThanks()));
        thanksValues.add(new ThanksValue("travelThanks", stats.getTravelThanks()));
        thanksValues.add(new ThanksValue("govThanks", stats.getGovThanks()));
        thanksValues.add(new ThanksValue("beautyThanks", stats.getBeautyThanks()));
        thanksValues.add(new ThanksValue("financeThanks", stats.getFinanceThanks()));
        thanksValues.add(new ThanksValue("cultureThanks", stats.getCultureThanks()));

        Collections.sort(thanksValues, new Comparator<ThanksValue>() {
            @Override
            public int compare(ThanksValue o1, ThanksValue o2) {
                return Long.compare(o2.getValue(), o1.getValue());
            }
        });

        return thanksValues;
    }

    public static String retrieveThanksType(Context context, Thanks thanks){
        String type = "";
        if(thanks.getThanksType() != null) {
            if(!thanks.getThanksType().equals("")) {
                switch (thanks.getThanksType()) {
                    case "NORMAL":
                        type = context.getString(R.string.thanked_string);
                        break;
                    case "SUPER":
                        type = context.getString(R.string.super_thanked_string);
                        break;
                    case "MEGA":
                        type = context.getString(R.string.mega_thanked_string);
                        break;
                    case "POWER":
                        type = context.getString(R.string.power_thanked_string);
                        break;
                }
            }
        }
        return type;
    }

    public static String returnLevel(Context context, String thankerLevel) {
        String level = thankerLevel.toUpperCase();
        String result = "";

        switch(level){
            case "STARTER": result = context.getString(R.string.starter_thanker); break;
            case "WALKER": result = context.getString(R.string.walker_thanker); break;
            case "EXPLORER": result = context.getString(R.string.explorer_thanker); break;
            case "TRUE": result = context.getString(R.string.true_thanker); break;
            case "MASTER": result = context.getString(R.string.master_thanker); break;
        }

        return result;
    }

    public static boolean doesListContainFriendRank(List<FriendRank>list, FriendRank friend){
        for(FriendRank item: list){
            if(friend.getUserId().equals(item.getUserId())){
                return true;
            }
        }

        return false;
    }

    public static boolean doesListContainFriendData(List<FriendData> list, String id){
        for(FriendData item: list){
            if(item.getFriendRank().getUserId().equals(id)){
                return true;
            }
        }
        return false;
    }

    public static boolean doesListContainName(List<FriendData>list, String text){
        for(FriendData item: list){
            if(item.getName().toLowerCase().contains(text.toLowerCase())){
                return true;
            }
        }

        return false;
    }

    public static boolean doesListContainIdEmail(List<IdEmail> list, IdEmail idEmail) {

        if(idEmail.getId() != null){
            for(IdEmail item: list){
                if(item.getId() != null){
                    if(item.getId().trim().equalsIgnoreCase(idEmail.getId().trim())){
                        return true;
                    }
                }

                else {
                    if(item.getEmail().trim().contains(idEmail.getEmail())){
                        return true;
                    }
                }

            }
        }

        else {
            for(IdEmail item: list){
                if(item.getEmail() != null){
                    if(item.getEmail().trim().equalsIgnoreCase(idEmail.getEmail().trim())){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean doesListContainIdEmailText(List<IdEmailText> list, IdEmailText idEmailText) {

        for(IdEmailText item: list){
            if(item.getText().toLowerCase().contains(idEmailText.getText().toLowerCase())){
                return true;
            }
        }

        return false;
    }

    public static double generateRankFactor(ThanksData data, long given, long received){
        double result = 0;

        result += given;
        result += ((double) received / 2);
        result += (data.getThanksCount() * 0.001);
        result += (data.getSuperThanksGiven() * 0.01);
        result += (data.getMegaThanksGiven() * 0.1);
        result += (data.getPowerThanksGiven());
        result += (data.getThanksCurrency() * 0.001);
        //Commented to run


        double dateVar = System.currentTimeMillis();
        double timeVar = (dateVar) / 100000000.0;

        result += timeVar;

        Log.v(TAG, "TimeVar: " + timeVar);

        return result;
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static long getDate(int numberMonths, long date){

        int numberDays = 1;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));
        calendar.add(Calendar.DAY_OF_YEAR, numberDays);
        calendar.add(Calendar.MONTH, numberMonths);

        return calendar.getTimeInMillis();
    }

    public static String firstName(String value) {
        String name = "";
        value = value.trim();
        int countSpaces = 1;
        String firstLetters = value.substring(0, 4).toLowerCase();

        if(firstLetters.contains("the") || firstLetters.contains("le") || firstLetters.contains("la")){
            countSpaces = 2;
        }

        for(int i = 0, countX = 0; i != value.length() && (value.charAt(i) != ' ' || countX != countSpaces); i++){

            if(i == 0){
                name += ("" + value.charAt(i)).toUpperCase();
            }

            else if(i > 0){
                    if(value.charAt(i - 1) == ' '){
                        name += ("" + value.charAt(i)).toUpperCase();
                    }
                    else {
                        name += value.charAt(i);
                    }
            }

            if(value.charAt(i) == ' '){
                countX++;
            }
        }

        if(countSpaces > 1){
            name = name.substring(0, name.length() - 2);
        }

        return name.trim();
    }

    public static List<String> reverse(List<String> listStrings) {
        List<String> results = new ArrayList<>();

        for(int n = listStrings.size() - 1; n >= 0; n--){
            results.add(listStrings.get(n));
        }

        return results;
    }

    public static boolean isEmail(String text) {
        boolean isEmail = true;
        boolean continueLoop = true;

        if(!text.contains("@")){
            return false;
        }

        if(TextUtils.countAts(text) > 1){
            return false;
        }

        for(int i = 0; i != text.length(); i++){
            if(text.charAt(i) == ' '){
                return false;
            }
        }

        for(int n = 0; n != text.length() && continueLoop; n++){
            if(text.charAt(n) == '@'){
                continueLoop = false;
                String subText = text.substring(n);
                Log.v(TAG, "Found result. Verifying substring: " + subText);
                if(subText.contains(".")){
                    Log.v(TAG, "Found result. Length of subText: " + subText.length());
                    boolean continueOtherLoop = true;
                    if(subText.charAt(1) == '.'){
                        return false;
                    }
                    for(int x = 0; x != subText.length() && continueOtherLoop; x++){
                        Log.v(TAG, "Found result. Value of x: " + x);
                        if(subText.charAt(x) == '.'){
                            continueOtherLoop = false;
                            String domain = subText.substring(x);
                            Log.v(TAG, "Found result. Verifying domain: " + domain);
                            if(domain.length() < 2){
                                return false;
                            }
                        }
                    }
                }
                else {
                    Log.v(TAG, "Found result. No dot");
                    return false;
                }
            }
        }

        Log.v(TAG, "Found result. Going to return true");
        return isEmail;
    }

    public static String getEnglishCountry(Context context, String country){
        String result = "";
        Locale outLocale = new Locale("en");
        Locale inLocale = context.getResources().getConfiguration().locale;
        for (Locale l : Locale.getAvailableLocales()) {
            if (l.getDisplayCountry(inLocale).equals(country)) {
                result = l.getDisplayCountry(outLocale);
            }
        }
        return result;
    }

    public static String getTranslatedCountry(Context context, String country){
        String result = "";
        Locale outLocale = context.getResources().getConfiguration().locale;
        Locale inLocale = new Locale("en");
        for (Locale l : Locale.getAvailableLocales()) {
            if (l.getDisplayCountry(inLocale).equals(country)) {
                result = l.getDisplayCountry(outLocale);
            }
        }
        return result;
    }

    public static boolean doesListContainUserSnippetResult(List<UserSnippet> userList, UserSnippet user) {

        if(user != null && user.getUserId() != null){
            for(UserSnippet item: userList){
                if(user.getUserId() != null){
                    if(user.getUserId().equalsIgnoreCase(item.getUserId())){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean doesListContainUserSnippetEmail(List<UserSnippet> userList, UserSnippet user) {
        if(user != null){
            for(UserSnippet item: userList){
                if(item.getEmail() != null){
                    if(item.getEmail().toLowerCase().trim().contains(user.getEmail().toLowerCase().trim())){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static void sortRecentThanks(List<ThanksItem> list){
        Collections.sort(list, new Comparator<ThanksItem>() {
            @Override
            public int compare(ThanksItem o1, ThanksItem o2) {
                return Long.compare(o2.getDate(), o1.getDate());
            }
        });
    }

    public static void sortThanksValues(List<ThanksValue> list){
        Collections.sort(list, new Comparator<ThanksValue>() {
            @Override
            public int compare(ThanksValue o1, ThanksValue o2) {
                return Long.compare(o2.getValue(), o1.getValue());
            }
        });
    }

    public static boolean doesListContainIdData(List<IdData> listData, IdData idData) {
        for(IdData item: listData){
            if(item.getId().equalsIgnoreCase(idData.getId())){
                return true;
            }
        }
        return false;
    }

}
