package co.thanker.data;

import java.io.Serializable;

public class ThanksData implements Serializable {

    private long thanksCount;
    private long receivedCount;
    private long thanksCurrency;
    private long diaryThanks;

    private long superThanksGiven;
    private long megaThanksGiven;
    private long powerThanksGiven;
    private long ultraThanksGiven;

    private long superThanksReceived;
    private long megaThanksReceived;
    private long powerThanksReceived;
    private long ultraThanksReceived;

    private long givenThanksValue;
    private long receivedThanksValue;

    private long lastRegisteredReceivedThanks;

    public ThanksData() {

    }

    public ThanksData(boolean something) {
        thanksCount = 0;
        receivedCount = 0;
        thanksCurrency = 1;
        diaryThanks = 0;
        givenThanksValue = 0;
        receivedThanksValue = 0;
        superThanksGiven = 0;
        megaThanksGiven = 0;
        powerThanksGiven = 0;
        ultraThanksGiven = 0;
        superThanksReceived = 0;
        megaThanksReceived = 0;
        powerThanksReceived = 0;
        ultraThanksReceived = 0;
        lastRegisteredReceivedThanks = 0;
    }

    public void updateSpecialReceived(Thanks thanks){
        switch(thanks.getThanksType().toLowerCase()){
            case "super": superThanksReceived++; break;
            case "mega": megaThanksReceived++; break;
            case "power": powerThanksReceived++; break;
            case "ultra": ultraThanksReceived++; break;
        }
    }

    public void updateSpecialGiven(Thanks thanks){
        switch(thanks.getThanksType().toLowerCase()){
            case "super": superThanksGiven++; break;
            case "mega": megaThanksGiven++; break;
            case "power": powerThanksGiven++; break;
            case "ultra": ultraThanksGiven++; break;
        }
    }

    public long getThanksCount() {
        return thanksCount;
    }

    public long getReceivedCount() {
        return receivedCount;
    }

    public long getThanksCurrency() {
        return thanksCurrency;
    }

    public long getDiaryThanks(){
        return diaryThanks;
    }

    public long getGivenThanksValue() {
        return givenThanksValue;
    }

    public long getReceivedThanksValue() {
        return receivedThanksValue;
    }

    public long getSuperThanksGiven() {
        return superThanksGiven;
    }

    public long getMegaThanksGiven() {
        return megaThanksGiven;
    }

    public long getPowerThanksGiven() {
        return powerThanksGiven;
    }

    public long getUltraThanksGiven() {
        return ultraThanksGiven;
    }

    public long getSuperThanksReceived() {
        return superThanksReceived;
    }

    public long getMegaThanksReceived() {
        return megaThanksReceived;
    }

    public long getPowerThanksReceived() {
        return powerThanksReceived;
    }

    public long getUltraThanksReceived() {
        return ultraThanksReceived;
    }

    public long getLastRegisteredReceivedThanks(){
        return lastRegisteredReceivedThanks;
    }

    public void setThanksCount(long thanksCount) {
        this.thanksCount = thanksCount;
    }

    public void setReceivedCount(long receivedCount) {
        this.receivedCount = receivedCount;
    }

    public void setThanksCurrency(long thanksCurrency) {
        this.thanksCurrency = thanksCurrency;
    }

    public void setDiaryThanks(long number){
        diaryThanks = number;
    }

    public void setSuperThanksGiven(long superThanksGiven) {
        this.superThanksGiven = superThanksGiven;
    }

    public void setMegaThanksGiven(long megaThanksGiven) {
        this.megaThanksGiven = megaThanksGiven;
    }

    public void setPowerThanksGiven(long powerThanksGiven) {
        this.powerThanksGiven = powerThanksGiven;
    }

    public void setUltraThanksGiven(long ultraThanksGiven) {
        this.ultraThanksGiven = ultraThanksGiven;
    }

    public void setSuperThanksReceived(long superThanksReceived) {
        this.superThanksReceived = superThanksReceived;
    }

    public void setMegaThanksReceived(long megaThanksReceived) {
        this.megaThanksReceived = megaThanksReceived;
    }

    public void setPowerThanksReceived(long powerThanksReceived) {
        this.powerThanksReceived = powerThanksReceived;
    }

    public void setUltraThanksReceived(long ultraThanksReceived) {
        this.ultraThanksReceived = ultraThanksReceived;
    }

    public void setGivenThanksValue(long givenThanksValue) {
        this.givenThanksValue = givenThanksValue;
    }

    public void setReceivedThanksValue(long receivedThanksValue) {
        this.receivedThanksValue = receivedThanksValue;
    }

    public void setLastRegisteredReceivedThanks(long l){
        lastRegisteredReceivedThanks = l;
    }

}
