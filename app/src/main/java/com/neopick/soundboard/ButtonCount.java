package com.neopick.soundboard;

public class ButtonCount extends ButtonModel {

    protected Integer mCount;


    public ButtonCount(String pName, int pColor){
        super();
        mName = pName;
        mColor = pColor;
        mCount = 0;
    }

    public ButtonCount(String pName, int pColor, int pCount){
        super();
        mName = pName;
        mColor = pColor;
        mCount = pCount;
    }

    public void setCount() {
        this.mCount ++;
    }

    public Integer getCount() {
        return mCount;
    }

}
