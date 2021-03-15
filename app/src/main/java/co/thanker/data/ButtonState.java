package co.thanker.data;

public class ButtonState {

    private long mPosition;
    private boolean mState;

    public ButtonState(long position, boolean state){
        mPosition = position;
        mState = state;
    }

    public long getPosition(){
        return mPosition;
    }

    public boolean getState(){
        return mState;
    }

    public void setPosition(long l){
        mPosition = l;
    }

    public void setState(boolean b){
        mState = b;
    }

}
