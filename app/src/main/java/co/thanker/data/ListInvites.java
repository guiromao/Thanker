package co.thanker.data;

import java.util.List;

public class ListInvites {

    private List<ThanksInvite> listInvites;

    public ListInvites(){

    }

    public ListInvites(List<ThanksInvite> list){
        listInvites = list;
    }

    public List<ThanksInvite> getListInvites() {
        return listInvites;
    }

    public void setListInvites(List<ThanksInvite> listInvites) {
        this.listInvites = listInvites;
    }

}
