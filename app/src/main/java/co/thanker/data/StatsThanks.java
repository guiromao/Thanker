package co.thanker.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.thanker.utils.DataUtils;

public class StatsThanks {

    private long date;
    private String country;
    private String year;
    private String month;
    private String day;
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

    public StatsThanks(){
        date = System.currentTimeMillis();
        year = DataUtils.generateYear();
        month = DataUtils.generateMonth();
        day = DataUtils.generateDay();
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

    public StatsThanks(String country, String primaryCategory, String secondaryCategory, String thanksType){
        date = System.currentTimeMillis();
        this.country = country;
        year = DataUtils.generateYear();
        month = DataUtils.generateMonth();
        day = DataUtils.generateDay();
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
        setValueOnCategory(primaryCategory, 0, thanksType);
        if(secondaryCategory != null){
            if(!secondaryCategory.equals("")){
                setValueOnCategory(secondaryCategory, 1, thanksType);
            }
        }

    }

    public StatsThanks(StatsThanks statsOne, StatsThanks statsTwo){
        date = firstDate(statsOne, statsTwo);
        country = statsOne.getCountry();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(date));
        year = DataUtils.generateYear(cal);
        month = DataUtils.generateMonth(cal);
        day = DataUtils.generateDay(cal);
        personThanks = statsOne.getPersonThanks() + statsTwo.getPersonThanks();
        brandThanks = statsOne.getBrandThanks() + statsTwo.getBrandThanks();
        businessThanks = statsOne.getBusinessThanks() + statsTwo.getBusinessThanks();
        natureThanks = statsOne.getNatureThanks() + statsTwo.getNatureThanks();
        healthThanks = statsOne.getHealthThanks() + statsTwo.getHealthThanks();
        foodThanks = statsOne.getFoodThanks() + statsTwo.getFoodThanks();
        associationThanks = statsOne.getAssociationThanks() + statsTwo.getAssociationThanks();
        homeThanks = statsOne.getHomeThanks() + statsTwo.getHomeThanks();
        scienceThanks = statsOne.getScienceThanks() + statsTwo.getScienceThanks();
        religionThanks = statsOne.getReligionThanks() + statsTwo.getReligionThanks();
        sportsThanks = statsOne.getSportsThanks() + statsTwo.getSportsThanks();
        lifestyleThanks = statsOne.getLifestyleThanks() + statsTwo.getLifestyleThanks();
        techThanks = statsOne.getTechThanks() + statsTwo.getTechThanks();
        fashionThanks = statsOne.getFashionThanks() + statsTwo.getFashionThanks();
        educationThanks = statsOne.getEducationThanks() + statsTwo.getEducationThanks();
        gamesThanks = statsOne.getGamesThanks() + statsTwo.getGamesThanks();
        travelThanks = statsOne.getTravelThanks() + statsTwo.getTravelThanks();
        govThanks = statsOne.getGovThanks() + statsTwo.getGovThanks();
        beautyThanks = statsOne.getBeautyThanks() + statsTwo.getBeautyThanks();
        financeThanks = statsOne.getFinanceThanks() + statsTwo.getFinanceThanks();
        cultureThanks = statsOne.getCultureThanks() + statsTwo.getCultureThanks();
    }

    public void addStatsThanksOf(StatsThanks statsTwo){
        personThanks += statsTwo.getPersonThanks();
        brandThanks += statsTwo.getBrandThanks();
        businessThanks += statsTwo.getBusinessThanks();
        natureThanks += statsTwo.getNatureThanks();
        healthThanks += statsTwo.getHealthThanks();
        foodThanks += statsTwo.getFoodThanks();
        associationThanks += statsTwo.getAssociationThanks();
        homeThanks += statsTwo.getHomeThanks();
        scienceThanks += statsTwo.getScienceThanks();
        religionThanks += statsTwo.getReligionThanks();
        sportsThanks += statsTwo.getSportsThanks();
        lifestyleThanks += statsTwo.getLifestyleThanks();
        techThanks += statsTwo.getTechThanks();
        fashionThanks += statsTwo.getFashionThanks();
        educationThanks += statsTwo.getEducationThanks();
        gamesThanks += statsTwo.getGamesThanks();
        travelThanks += statsTwo.getTravelThanks();
        govThanks += statsTwo.getGovThanks();
        beautyThanks += statsTwo.getBeautyThanks();
        financeThanks += statsTwo.getFinanceThanks();
        cultureThanks += statsTwo.getCultureThanks();
    }

    public long firstDate(StatsThanks one, StatsThanks two){
        return (one.getDate() < two.getDate() ? one.getDate() : two.getDate());
    }

    public void setValueOnCategory(String category, int kindCategory, String typeThanks){

        int value;
        String type = typeThanks.toLowerCase().trim();

        //type is Primary Category
        if(kindCategory == 0){
            value = 2;
        }

        else {
            value = 1;
        }

        switch(type){
            case "normal": break;
            case "super": value *= 10; break;
            case "mega": value *= 100; break;
            case "power": value *= 1000; break;
            case "ultra": value *= 10000; break;
        }

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

    public long getRightCategoryValue(String category){

        long value = 0;

        switch(category.toLowerCase()){
            case "person": value = personThanks; break;
            case "brand": value = brandThanks; break;
            case "business": value = businessThanks; break;
            case "nature": value = natureThanks; break;
            case "health": value = healthThanks; break;
            case "food": value = foodThanks; break;
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

    /*public List<CategoryValue> getCategoryValues(){
        List<CategoryValue> result = new ArrayList<>();

        result.add(new CategoryValue("personThanks", personThanks));
        result.add(new CategoryValue("brandThanks", brandThanks));
        result.add(new CategoryValue("businessThanks", businessThanks));
        result.add(new CategoryValue("natureThanks", natureThanks));
        result.add(new CategoryValue("healthThanks", healthThanks));
        result.add(new CategoryValue("foodThanks", foodThanks));
        result.add(new CategoryValue("associationThanks", associationThanks));
        result.add(new CategoryValue("homeThanks", homeThanks));
        result.add(new CategoryValue("scienceThanks", scienceThanks));
        result.add(new CategoryValue("religionThanks", religionThanks));
        result.add(new CategoryValue("sportsThanks", sportsThanks));
        result.add(new CategoryValue("lifestyleThanks", lifestyleThanks));
        result.add(new CategoryValue("techThanks", techThanks));
        result.add(new CategoryValue("fashionThanks", fashionThanks));
        result.add(new CategoryValue("educationThanks", educationThanks));
        result.add(new CategoryValue("gamesThanks", gamesThanks));
        result.add(new CategoryValue("travelThanks", travelThanks));
        result.add(new CategoryValue("govThanks", govThanks));
        result.add(new CategoryValue("beautyThanks", beautyThanks));
        result.add(new CategoryValue("financeThanks", financeThanks));
        result.add(new CategoryValue("cultureThanks", cultureThanks));

        return result;
    }*/

    public long getDate(){
        return date;
    }

    public String getCountry(){
        return country;
    }

    public String getYear(){
        return year;
    }

    public String getMonth(){
        return month;
    }

    public String getDay(){
        return day;
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

    public void setDate(long date){
        this.date = date;
    }

    public void setCountry(String nation){
        country = nation;
    }

    public void setYear(String y){
        year = y;
    }

    public void setMonth(String m){
        month = m;
    }

    public void setDay(String d)
    {
        day = d;
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

}
