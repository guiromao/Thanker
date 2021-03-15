package co.thanker.data;

import com.google.firebase.Timestamp;

public class PremiumData {

    private long initialOrRenewalDate;
    //private long expiringDate;
    private Timestamp lastTimeThankersClaimed;
    private long consecutiveRenewals;
    private boolean warnedRenewal;

    public PremiumData(){

    }

    public PremiumData (long enteredDate, long expiring){
        initialOrRenewalDate = enteredDate;
        //expiringDate = expiring;
        lastTimeThankersClaimed = null;
        consecutiveRenewals = 0;
        warnedRenewal = false;
    }

    public PremiumData(long enteredDate){
        initialOrRenewalDate = enteredDate;
        lastTimeThankersClaimed = null;
        consecutiveRenewals = 0;
        warnedRenewal = false;
    }


    /*public PremiumData(long enteredDate, long retrievedDate){
        initialOrRenewalDate = enteredDate;
        lastTimeThankersClaimed = retrievedDate;
        consecutiveRenewals = 0;
    }*/

    public long getInitialOrRenewalDate(){
        return initialOrRenewalDate;
    }

    /*public long getExpiringDate(){
        return expiringDate;
    }*/

    public Timestamp getLastTimeThankersClaimed(){
        return lastTimeThankersClaimed;
    }

    public long getConsecutiveRenewals(){
        return consecutiveRenewals;
    }

    private boolean getWarnedRenewal(){
        return warnedRenewal;
    }

    public void setInitialOrRenewalDate(long date){
        initialOrRenewalDate = date;
    }

    /*public void setExpiringDate(long date){
        expiringDate = date;
    }*/

    public void setLastTimeThankersClaimed(Timestamp anotherDate){
        lastTimeThankersClaimed = anotherDate;
    }

    public void setConsecutiveRenewals(long number){
        consecutiveRenewals = number;
    }

    public void setWarnedRenewal(boolean warned){
        warnedRenewal = warned;
    }
}
