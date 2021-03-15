package utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import co.thanker.data.Thanks;
import co.thanker.data.User;

public class FirebaseUtils {

    private static final String THANKS_REFERENCE = "thanks-test";
    private static final String USERS_DATABASE = "users-test";

    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static DatabaseReference mUserDataReference = mDatabase.getReference().child(USERS_DATABASE);
    private static DatabaseReference mThanksReference = mDatabase.getReference().child(THANKS_REFERENCE);

    //saves thanks
    public static void saveThanks(User giver, User receiver, Thanks thanks){

        final DatabaseReference dataGiverReference = mUserDataReference.child(giver.getUserId());
        final DatabaseReference dataReceiverReference = mUserDataReference.child(receiver.getUserId());

        final String type = thanks.getThanksType();

        long currentThanksValue = 0;

        switch(type){
            case "NORMAL": currentThanksValue = 1; break;
            case "SUPER": currentThanksValue = 10; break;
            case "MEGA": currentThanksValue = 100; break;
            case "POWER": currentThanksValue = 1000; break;
        }

        final long currentThanksValueConstant = currentThanksValue;

        mThanksReference.child(giver.getUserId()).push().setValue(thanks);

        dataGiverReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    User currentUser = dataSnapshot.getValue(User.class);
                    if (currentUser != null) {
                        long numberOfThanks = currentUser.getThanksCount();
                        long thanksGivenValue = currentUser.getGivenThanksValue();
                        long numberSuperThanks = currentUser.getSuperThanksGot();
                        long numberMegaThanks = currentUser.getMegaThanksGot();
                        long numberPowerThanks = currentUser.getPowerThanksGot();
                        long superThanksGiven = currentUser.getSuperThanksGiven();
                        long megaThanksGiven = currentUser.getMegaThanksGiven();
                        long powerThanksGiven = currentUser.getPowerThanksGiven();

                        numberOfThanks++;
                        thanksGivenValue += currentThanksValueConstant;

                        if ((numberOfThanks % 10) == 0) {
                            numberSuperThanks++;
                        }

                        if ((numberOfThanks % 100) == 0) {
                            numberMegaThanks++;
                        }

                        if (numberOfThanks % 1000 == 0) {
                            numberPowerThanks++;
                        }

                        switch (type) {
                            case "SUPER":
                                numberSuperThanks--;
                                superThanksGiven++;
                                break;

                            case "MEGA":
                                numberMegaThanks--;
                                megaThanksGiven++;
                                break;

                            case "POWER":
                                numberPowerThanks--;
                                powerThanksGiven++;
                                break;
                            default:
                                break;
                        }

                        mUserDataReference.child(currentUser.getUserId()).child("thanksCount").setValue(numberOfThanks);
                        mUserDataReference.child(currentUser.getUserId()).child("givenThanksValue").setValue(thanksGivenValue);
                        mUserDataReference.child(currentUser.getUserId()).child("superThanksGot").setValue(numberSuperThanks);
                        mUserDataReference.child(currentUser.getUserId()).child("megaThanksGot").setValue(numberMegaThanks);
                        mUserDataReference.child(currentUser.getUserId()).child("powerThanksGot").setValue(numberPowerThanks);
                        mUserDataReference.child(currentUser.getUserId()).child("superThanksGiven").setValue(superThanksGiven);
                        mUserDataReference.child(currentUser.getUserId()).child("megaThanksGiven").setValue(megaThanksGiven);
                        mUserDataReference.child(currentUser.getUserId()).child("powerThanksGiven").setValue(powerThanksGiven);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dataReceiverReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);
                    long receivedThanksValue = user.getReceivedThanksValue();
                    long superThanksReceived = user.getSuperThanksReceived();
                    long megaThanksReceived = user.getMegaThanksReceived();
                    long powerThanksReceived = user.getPowerThanksReceived();

                    receivedThanksValue += currentThanksValueConstant;

                    switch(type){
                        case "SUPER": superThanksReceived++;
                                      break;

                        case "MEGA": megaThanksReceived++;
                                     break;

                        case "POWER": powerThanksReceived++;
                                      break;
                        default: break;
                    }

                    dataReceiverReference.child("receivedThanksValue").setValue(receivedThanksValue);
                    dataReceiverReference.child("superThanksReceived").setValue(superThanksReceived);
                    dataReceiverReference.child("megaThanksReceived").setValue(megaThanksReceived);
                    dataReceiverReference.child("powerThanksReceived").setValue(powerThanksReceived);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
