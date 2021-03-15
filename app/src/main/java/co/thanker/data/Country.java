package co.thanker.data;

public class Country {

    private String countryName;

    public Country(){

    }

    public Country(String string){
        countryName = string;
    }

    public String getCountryName(){
        return countryName;
    }

    public void setCountryName(String s){
        countryName = s;
    }
}
