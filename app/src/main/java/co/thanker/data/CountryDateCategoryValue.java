package co.thanker.data;

public class CountryDateCategoryValue {

    private String mCountry;
    private String mDate;
    private String mDay;
    private String mCategory;
    private long mValue;

    public CountryDateCategoryValue(String country, String date, String day, String category, long value){
        mCountry = country;
        mDate = date;
        mDay = day;
        mCategory = category;
        mValue = value;
    }

    public boolean isSameCDCV(CountryDateCategoryValue anotherCDCV){
        return (mCountry.equals(anotherCDCV.getCountry())
                && mDate.equals(anotherCDCV.getDate())
                && mDay.equals(anotherCDCV.getDay())
                && mCategory.equals(anotherCDCV.getCategory()));
    }

    public void addValue(long v){
        mValue += v;
    }

    public String getCountry(){
        return mCountry;
    }

    public String getDate(){
        return mDate;
    }

    public String getDay(){
        return mDay;
    }

    public String getCategory(){
        return mCategory;
    }

    public long getValue(){
        return mValue;
    }

    public void setCategory(String string){
        mCategory = string;
    }

    public void setValue(long value){
        mValue = value;
    }
}
