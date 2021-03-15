package utils;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import co.thanker.GMailSender;
import co.thanker.R;
import co.thanker.data.Message;
import co.thanker.data.Thanks;
import co.thanker.data.User;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class DataUtils {

    private static final String DB_REFERENCE = "users-test";
    private static final String MESSAGES_REFERENCE = "messages-list";

    //to add a number to a Thanks Type
    public static int addToThanksType(String type){
        int toAdd = 0;

        switch(type){
            case "NORMAL": toAdd = 1; break;
            case "SUPER": toAdd = 10; break;
            case "MEGA": toAdd = 100; break;
            case "POWER": toAdd = 1000; break;
        }

        return toAdd;
    }

    public static int retrieveItem(List<String> strings, String target){

        for(int i = 0; i != strings.size(); i++){
            if(strings.get(i).equalsIgnoreCase(target)){
                return i;
            }
        }

        return 0;
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

        for(User user: listUsers){
            if(user.getUserId().equals(userData.getUserId())){
                return true;
            }
        }

        return false;
    }

    public static String retrieveCountry(Context context, User user){
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
    }


    public static List<String> retrieveThanksTypesInList(){
        List<String> result = new ArrayList<>();

        result.add("personThanks");
        result.add("brandThanks");
        result.add("businessThanks");
        result.add("natureThanks");
        result.add("healthThanks");
        result.add("foodThanks");
        result.add("associationThanks");
        result.add("venueThanks");
        result.add("scienceThanks");
        result.add("religionThanks");
        result.add("sportsThanks");
        result.add("musicThanks");
        result.add("techThanks");
        result.add("fashionThanks");
        result.add("educationThanks");
        result.add("gamesThanks");
        result.add("travelThanks");
        result.add("landThanks");
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
                case "venueThanks":
                    result = "Venues";
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
                case "musicThanks":
                    result = "Music";
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
                case "landThanks":
                    result = "Land";
                    break;
                case "govThanks":
                    result = "Government";
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
        }

        return value;
    }

    public static boolean isPunctuation(char c){
        return (c == '.' || c == '!' || c == '?');
    }

    public static String capitalize(String string){
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

    public static String getDateString(long dateLong){
        Date date = new Date(dateLong);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateToPresent = sdf.format(date);
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

    public static void createMessage(String userId, String title, String text){
        final DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference().child(DB_REFERENCE).child(userId).child(MESSAGES_REFERENCE);
        Message message = new Message(title, text);
        messageRef.push().setValue(message, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError,
                                   DatabaseReference databaseReference) {
                String messageKey = databaseReference.getKey();
                messageRef.child(messageKey).child("key").setValue(messageKey);
            }
        });
    }

    public static void sendMessageFromThanks(Context context, String receiverId, String giverName, Thanks thanks){
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

                default: break;
            }

            if(!title.equals("") && !body.equals("")){
                DataUtils.createMessage(receiverId, title, body);
            }
        }

    }

    /*
    public static void sendEmail(Context context, String invitedEmail, String senderEmail, String senderName, Thanks thanks){

        String title = capitalize(senderName) + " " + context.getString(R.string.thanked_in_thanker);
        String body = context.getString(R.string.you_have_received_thanks) + " " + capitalize(senderName) + "." + context.getString(R.string.join_now);

        try {
            GMailSender sender = new GMailSender("username@gmail.com", "password");
            sender.sendMail(title,
                    body,
                    senderEmail,
                    invitedEmail);
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }*/

}
