package co.thanker.data;

import java.util.ArrayList;
import java.util.List;

public class DeletedUser {

    private User user;
    private UserSnippet userSnippet;
    private List<Thanks> thanksGiven;
    private List<Thanks> thanksReceived;
    private List<Message> listMessagesGiven;
    private List<Message> listMessagesReceived;
    private ThanksData thanksData;
    private PremiumData premiumData;

    public DeletedUser(){

    }

    public DeletedUser(User user){
        this.user = user;
        userSnippet = new UserSnippet();
        thanksGiven = new ArrayList<>();
        thanksReceived = new ArrayList<>();
        listMessagesGiven = new ArrayList<>();
        listMessagesReceived = new ArrayList<>();
        premiumData = new PremiumData();
    }

    public User getUser() {
        return user;
    }

    public UserSnippet getUserSnippet() {
        return userSnippet;
    }

    public List<Thanks> getThanksGiven() {
        return thanksGiven;
    }

    public List<Thanks> getThanksReceived() {
        return thanksReceived;
    }

    public List<Message> getListMessagesGiven() {
        return listMessagesGiven;
    }

    public List<Message> getListMessagesReceived() {
        return listMessagesReceived;
    }

    public ThanksData getThanksData() {
        return thanksData;
    }

    public PremiumData getPremiumData() {
        return premiumData;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUserSnippet(UserSnippet userSnippet) {
        this.userSnippet = userSnippet;
    }

    public void setThanksGiven(List<Thanks> thanksGiven) {
        this.thanksGiven = thanksGiven;
    }

    public void setThanksReceived(List<Thanks> thanksReceived) {
        this.thanksReceived = thanksReceived;
    }

    public void setListMessagesGiven(List<Message> listMessages) {
        this.listMessagesGiven = listMessages;
    }

    public void setListMessagesReceived(List<Message> listMessages) {
        this.listMessagesReceived = listMessages;
    }

    public void setThanksData(ThanksData thanksData) {
        this.thanksData = thanksData;
    }

    public void setPremiumData(PremiumData premiumData) {
        this.premiumData = premiumData;
    }

}
