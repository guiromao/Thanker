package co.thanker.data;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import co.thanker.R;

public class User implements Serializable {

    public static final String DEFAULT_IMAGE = "https://firebasestorage.googleapis.com/v0/b/thanker-b301f.appspot.com/o/avatarVerde.png?alt=media&token=18dfc445-2329-4850-836e-9b125a49bb17";
    private final long FIVE_DAYS_IN_MILLIS = 432000000;

    private String userId;
    private String email;
    private String phone;
    private String name;
    private long birthday;
    private String imageUrl;
    private String primaryCategory;
    private String secondaryCategory;
    //private String mLivingCity;
    private String livingCountry;
    private String thankerLevel;
    private long lastLogin;
    private boolean isPremium;
    private boolean hasWelcomes;
    private long lastPremiumPayment;
    private long lastWelcomesPayment;
    private List<FriendRank> friends;
    private long unseenRequests;
    private List<FriendRequest> friendRequests;
    private List<UserValue> topUsersGiven;
    private List<UserValue> topUsersReceived;
    private List<Long> recentThanks;

    private boolean isConceptual;

    private long personThanks;
    private long brandThanks;
    private long businessThanks;
    private long natureThanks;
    private long healthThanks;
    private long foodThanks;
    private long associationThanks;
    private long homeThanks;
    private long scienceThanks;
    private long religionThanks;
    private long sportsThanks;
    private long lifestyleThanks;
    private long techThanks;
    private long fashionThanks;
    private long educationThanks;
    private long gamesThanks;
    private long travelThanks;
    private long govThanks;
    private long beautyThanks;
    private long financeThanks;
    private long cultureThanks;

    public User(){
    }

    public User(String userId, String name, boolean conceptual){
        this.userId = userId;
        this.name = name;
        isConceptual = conceptual;
    }

    public User(String id, String email, String phone, String name, long birthday, String imgUrl, String cat, String catSecondary, /*String city,*/ String country) {
        userId = id;
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.birthday = birthday;
        if(imgUrl == null || imgUrl.equals("")){
            this.imageUrl = DEFAULT_IMAGE;
        }
        else {
            this.imageUrl = imgUrl;
        }
        primaryCategory = cat;
        secondaryCategory = catSecondary;
        //mLivingCity = city;
        livingCountry = country;
        thankerLevel = "starter";
        lastLogin = System.currentTimeMillis();
        isPremium = false;
        /*hasWelcomes = NO_PREMIUM;
        lastPremiumPayment = NO_PAYMENT;
        lastWelcomesPayment = NO_PAYMENT;*/
        //lastTimeSpecialThanksSubscriptionCollected = 0;
        isConceptual = false;
        friends = new ArrayList<>();
        friendRequests = new ArrayList<>();
        unseenRequests = 0;
        topUsersGiven = new ArrayList<>();
        topUsersReceived = new ArrayList<>();
        recentThanks = new ArrayList<>();
        personThanks = 0;
        brandThanks = 0;
        businessThanks = 0;
        natureThanks = 0;
        healthThanks = 0;
        foodThanks = 0;
        associationThanks = 0;
        homeThanks = 0;
        scienceThanks = 0;
        religionThanks = 0;
        sportsThanks = 0;
        lifestyleThanks = 0;
        techThanks = 0;
        fashionThanks = 0;
        educationThanks = 0;
        gamesThanks = 0;
        travelThanks = 0;
        govThanks = 0;
        beautyThanks = 0;
        financeThanks = 0;
        cultureThanks = 0;
    }

    /*public List<ThanksValue> getThanksValues(){
        List<ThanksValue> list = new ArrayList<>();

        list.add(new ThanksValue("personThanks", personThanks));
        list.add(new ThanksValue("brandThanks", brandThanks));
        list.add(new ThanksValue("businessThanks", businessThanks));
        list.add(new ThanksValue("healthThanks", healthThanks));
        list.add(new ThanksValue("foodThanks", foodThanks));
        list.add(new ThanksValue("associationThanks", associationThanks));
        list.add(new ThanksValue("homeThanks", homeThanks));
        list.add(new ThanksValue("scienceThanks", scienceThanks));
        list.add(new ThanksValue("religionThanks", religionThanks));
        list.add(new ThanksValue("sportsThanks", sportsThanks));
        list.add(new ThanksValue("lifestyleThanks", lifestyleThanks));
        list.add(new ThanksValue("techThanks", techThanks));
        list.add(new ThanksValue("fashionThanks", fashionThanks));
        list.add(new ThanksValue("educationThanks", educationThanks));
        list.add(new ThanksValue("gamesThanks", gamesThanks));
        list.add(new ThanksValue("govThanks", govThanks));
        list.add(new ThanksValue("beautyThanks", beautyThanks));
        list.add(new ThanksValue("financeThanks", financeThanks));
        list.add(new ThanksValue("cultureThanks", cultureThanks));
        list.add(new ThanksValue("natureThanks", natureThanks));
        list.add(new ThanksValue("travelThanks", travelThanks));

        return list;
    }*/

    public boolean isFriendOf(User user){

        for(FriendRank friend: friends){
            if(friend.getUserId().equalsIgnoreCase(user.getUserId())){
                return true;
            }
        }

        return false;
    }

    public boolean isFriendOf(String userId){

        for(FriendRank friend: friends){
            if(friend.getUserId().equalsIgnoreCase(userId)){
                return true;
            }
        }

        return false;
    }

    public long getThanksFrom(User user){

        if (isFriendOf(user)){
            for(FriendRank friend: friends){
                if(friend.getUserId().equalsIgnoreCase(user.getUserId())){
                    return friend.getThanksReceivedFrom();
                }
            }
        }

        return 0;
    }

    public long getThanksFrom(String userId){

        if (isFriendOf(userId)){
            for(FriendRank friend: friends){
                if(friend.getUserId().equalsIgnoreCase(userId)){
                    return friend.getThanksReceivedFrom();
                }
            }
        }

        return 0;
    }

    public long getThanksGivenTo(User user){

        if (isFriendOf(user)){
            for(FriendRank friend: friends){
                if(friend.getUserId().equalsIgnoreCase(user.getUserId())){
                    return friend.getThanksGivenTo();
                }
            }
        }

        return 0;
    }

    public long getThanksFromTop(String userId){

        if (isOnTopReceived(userId)){
            for(UserValue friend: topUsersReceived){
                if(friend.getUserId().equalsIgnoreCase(userId)){
                    return friend.getValueThanks();
                }
            }
        }

        return 0;
    }

    public long getThanksGivenTopTo(String userId){

        if (isOnTopGiven(userId)){
            for(UserValue friend: topUsersGiven){
                if(friend.getUserId().equalsIgnoreCase(userId)){
                    return friend.getValueThanks();
                }
            }
        }

        return 0;
    }

    public boolean isOnTopReceived(String userId){
        for(UserValue user: topUsersReceived){
            if(user.getUserId().equalsIgnoreCase(userId)){
                return true;
            }
        }
        return false;
    }

    public boolean isOnTopGiven(String userId){
        for(UserValue user: topUsersGiven){
            if(user.getUserId().equalsIgnoreCase(userId)){
                return true;
            }
        }
        return false;
    }

    public boolean hasReceivedRequestFrom(User user){
        for(FriendRequest request: friendRequests){
            if(request.getUserId().equalsIgnoreCase(user.getUserId())){
                return true;
            }
        }

        return false;
    }

    public void removeRequest(FriendRequest request) {
        boolean found = false;
        for(int i = 0; i != friendRequests.size() && !found; i++){
            if(friendRequests.get(i).getUserId().equalsIgnoreCase(request.getUserId())){
                friendRequests.remove(i);
                found = true;
            }
        }
    }

    public long getNumberRecentThanks(){
        int result = 0;
        List<Long> newList = new ArrayList<>();

        for(long date: recentThanks){
            if(date >= (System.currentTimeMillis() - FIVE_DAYS_IN_MILLIS)){
                newList.add(date);
                result++;
            }
        }

        setRecentThanks(newList);

        return result;
    }

    public enum Category {
        BLANK("", 99),
        PERSON("Person", 0),
        BRAND("Brand", 1),
        BUSINESS("Business", 2),
        NATURE("Nature", 3),
        HEALTH("Health", 4),
        FOOD("Food", 5),
        ASSOCIATION("Association", 6),
        HOME("Home", 7),
        SCIENCE("Science", 8),
        RELIGION("Religion", 9),
        SPORTS("Sports", 10),
        LIFESTYLE("Lifestyle", 11),
        TECHNOLOGY("Technology", 12),
        FASHION("Fashion", 13),
        EDUCATION("Education", 14),
        GAMES("Games", 15),
        TRAVEL("Travel", 16),
        GOVERNMENT("Institutional", 17),
        BEAUTY("Beauty", 18),
        CULTURE("Culture", 19),
        FINANCE("Finance", 20);


        private String categoryString;
        private int categoryInt;

        private Category(String categoryName, int integer) {
            categoryString = categoryName;
            categoryInt = integer;
        }

        @Override
        public String toString() {
            if (categoryString == null) {
                return null;
            }
            return categoryString;
        }
    }

    ;

    public String getInfo() {
        String result =
                "User ID: " + userId + "\n"
                        + "Email: " + email + "\n"
                        + "Birthday: " + birthday + "\n"
                        + "Primary Category: " + primaryCategory + "\n"
                        + "Secondary Category: " + secondaryCategory;

        return result;
    }

    //Getters

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone(){
        return phone;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getPrimaryCategory() {
        return primaryCategory;
    }

    public String getSecondaryCategory(){
        return secondaryCategory;
    }

    public long getBirthday(){
        return birthday;
    }

   /* public String getLivingCity() {
        return mLivingCity;
    }*/

    public String getLivingCountry() {
        return livingCountry;
    }

    public String getThankerLevel(){
        return thankerLevel;
    }

    public long getLastLogin(){
        return lastLogin;
    }

    public boolean getIsPremium() {
        return isPremium;
    }

    public boolean getHasWelcomes() {
        return hasWelcomes;
    }

    public long getLastPremiumPayment() {
        return lastPremiumPayment;
    }

    public long getLastWelcomesPayment() {
        return lastWelcomesPayment;
    }

    /*public long getLastTimeSpecialSubscriptionCollected(){
        return lastTimeSpecialThanksSubscriptionCollected;
    } */

    public boolean getIsConceptual(){
        return isConceptual;
    }

    public List<FriendRank> getFriends(){
        return friends;
    }

    public List<FriendRequest> getFriendRequests(){
        return friendRequests;
    }

    public long getUnseenRequests(){
        return unseenRequests;
    }

    public List<UserValue> getTopUsersGiven(){
        return topUsersGiven;
    }

    public List<UserValue> getTopUsersReceived(){
        return topUsersReceived;
    }

    public List<Long> getRecentThanks(){
        return recentThanks;
    }

    //Setters

    public void setUserId(String mUserId) {
        this.userId = mUserId;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setEmail(String mail){
        email = mail;
    }

    public void setPhone(String contact){
        phone = contact;
    }

    public void setImageUrl(String url){
        imageUrl = url;
    }

    public void setPrimaryCategory(String cat) {
        this.primaryCategory = cat;
    }

    public void setSecondaryCategory(String cat){
        secondaryCategory = cat;
    }

    public void setBirthday(long birthday){
        this.birthday = birthday;
    }

    /*public void setLivingCity(String mLivingCity) {
        this.mLivingCity = mLivingCity;
    }*/

    public void setLivingCountry(String mLivingCountry) {
        this.livingCountry = mLivingCountry;
    }

    public void setThankerLevel(String s){
        thankerLevel = s;
    }

    public void setIsPremium(boolean mIsPremium) {
        this.isPremium = mIsPremium;
    }

    public void setHasWelcomes(boolean mHasWelcomes) {
        this.hasWelcomes = mHasWelcomes;
    }

    public void setLastPremiumPayment(long mLastPremiumPayment) {
        this.lastPremiumPayment = mLastPremiumPayment;
    }

    public void setLastWelcomesPayment(long mLastWelcomesPayment) {
        this.lastWelcomesPayment = mLastWelcomesPayment;
    }

    public void setIsConceptual(boolean b){
        isConceptual = b;
    }

    public long getRightCategoryValue(String category){

        long value = 0;

        switch(category.toLowerCase()){
            case "people":
            case "person": value = personThanks; break;
            case "brands":
            case "brand": value = brandThanks; break;
            case "business": value = businessThanks; break;
            case "nature": value = natureThanks; break;
            case "health": value = healthThanks; break;
            case "food": value = foodThanks; break;
            case "associations":
            case "association": value = associationThanks; break;
            case "home": value = homeThanks; break;
            case "science": value = scienceThanks; break;
            case "religion": value = religionThanks; break;
            case "sports": value = sportsThanks; break;
            case "lifestyle": value = lifestyleThanks; break;
            case "technology": value = techThanks; break;
            case "fashion": value = fashionThanks; break;
            case "education": value = educationThanks; break;
            case "games": value = gamesThanks; break;
            case "travel": value = travelThanks; break;
            case "institutional": value = govThanks; break;
            case "beauty": value = beautyThanks; break;
            case "finance": value = financeThanks; break;
            case "culture": value = cultureThanks; break;
        }

        return value;
    }

    public void addValueOnCategory(long value, String category){

        switch(category.toLowerCase()){
            case "people":
            case "person": personThanks += value; break;
            case "brands":
            case "brand": brandThanks += value; break;
            case "business": businessThanks += value; break;
            case "nature": natureThanks += value; break;
            case "health": healthThanks += value; break;
            case "food": foodThanks += value; break;
            case "associations":
            case "association": associationThanks += value; break;
            case "home": homeThanks += value; break;
            case "science": scienceThanks += value; break;
            case "religion": religionThanks += value; break;
            case "sports": sportsThanks += value; break;
            case "lifestyle": lifestyleThanks += value; break;
            case "technology": techThanks += value; break;
            case "fashion": fashionThanks += value; break;
            case "education": educationThanks += value; break;
            case "games": gamesThanks += value; break;
            case "travel": travelThanks += value; break;
            case "institutional": govThanks += value; break;
            case "beauty": beautyThanks += value; break;
            case "finance": financeThanks += value; break;
            case "culture": cultureThanks += value; break;
        }

    }

    public boolean hasBeenThankedBy(User user){
        for(UserValue userValue: topUsersReceived){
            if(userValue.getUserId().equalsIgnoreCase(user.getUserId())){
                return true;
            }
        }
        return false;
    }

    public boolean hasThankedTo(User user){
        for(UserValue userValue: topUsersGiven){
            if(userValue.getUserId().equalsIgnoreCase(user.getUserId())){
                return true;
            }
        }
        return false;
    }

    public long getPersonThanks(){
        return personThanks;
    }

    public long getBrandThanks(){
        return brandThanks;
    }

    public long getBusinessThanks(){
        return businessThanks;
    }

    public long getNatureThanks(){
        return natureThanks;
    }

    public long getHealthThanks(){
        return healthThanks;
    }

    public long getFoodThanks(){
        return foodThanks;
    }

    public long getHomeThanks(){
        return homeThanks;
    }

    public long getAssociationThanks(){
        return associationThanks;
    }

    public long getScienceThanks(){
        return scienceThanks;
    }

    public long getReligionThanks(){
        return religionThanks;
    }

    public long getSportsThanks(){
        return sportsThanks;
    }

    public long getLifestyleThanks(){
        return lifestyleThanks;
    }

    public long getTechThanks(){
        return techThanks;
    }

    public long getEducationThanks(){
        return educationThanks;
    }

    public long getFashionThanks(){
        return fashionThanks;
    }

    public long getGamesThanks(){
        return gamesThanks;
    }

    public long getTravelThanks(){
        return travelThanks;
    }

    public long getGovThanks(){
        return govThanks;
    }

    public long getBeautyThanks(){
        return beautyThanks;
    }

    public long getFinanceThanks(){
        return financeThanks;
    }

    public long getCultureThanks(){
        return cultureThanks;
    }

    public void setPersonThanks(long personThanks) {
        this.personThanks = personThanks;
    }

    public void setBrandThanks(long brandThanks) {
        this.brandThanks = brandThanks;
    }

    public void setBusinessThanks(long businessThanks) {
        this.businessThanks = businessThanks;
    }

    public void setNatureThanks(long natureThanks) {
        this.natureThanks = natureThanks;
    }

    public void setHealthThanks(long healthThanks) {
        this.healthThanks = healthThanks;
    }

    public void setFoodThanks(long foodThanks) {
        this.foodThanks = foodThanks;
    }

    public void setAssociationThanks(long associationThanks) {
        this.associationThanks = associationThanks;
    }

    public void setHomeThanks(long homeThanks) {
        this.homeThanks = homeThanks;
    }

    public void setScienceThanks(long scienceThanks) {
        this.scienceThanks = scienceThanks;
    }

    public void setReligionThanks(long religionThanks) {
        this.religionThanks = religionThanks;
    }

    public void setSportsThanks(long sportsThanks) {
        this.sportsThanks = sportsThanks;
    }

    public void setLifestyleThanks(long lifestyleThanks) {
        this.lifestyleThanks = lifestyleThanks;
    }

    public void setTechThanks(long techThanks) {
        this.techThanks = techThanks;
    }

    public void setFashionThanks(long fashionThanks) {
        this.fashionThanks = fashionThanks;
    }

    public void setEducationThanks(long educationThanks) {
        this.educationThanks = educationThanks;
    }

    public void setGamesThanks(long gamesThanks) {
        this.gamesThanks = gamesThanks;
    }

    public void setTravelThanks(long travelThanks) {
        this.travelThanks = travelThanks;
    }

    public void setGovThanks(long govThanks) {
        this.govThanks = govThanks;
    }

    public void setBeautyThanks(long beautyThanks) {
        this.beautyThanks = beautyThanks;
    }

    public void setFinanceThanks(long financeThanks) {
        this.financeThanks = financeThanks;
    }

    public void setCultureThanks(long cultureThanks) {
        this.cultureThanks = cultureThanks;
    }

    public void setFriends(List<FriendRank> list){
        friends = list;
    }

    public void setFriendRequests(List<FriendRequest> list){
        friendRequests = list;
    }

    public void setTopUsersGiven(List<UserValue> list){
        topUsersGiven = list;
    }

    public void setTopUsersReceived(List<UserValue> list){
        topUsersReceived = list;
    }

    public void setUnseenRequests(long l){
        unseenRequests = l;
    }

    public void setRecentThanks(List<Long> thanks){
        recentThanks = thanks;
    }

}
