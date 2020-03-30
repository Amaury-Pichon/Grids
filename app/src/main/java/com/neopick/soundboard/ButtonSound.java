package com.neopick.soundboard;

public class ButtonSound extends ButtonModel {

    private String mSource;

    public ButtonSound(String pName, int pColor, String pSource){
        super();
        mName = pName;
        mColor = pColor;
        mSource = pSource;
    }

    public String getSource() {
        return mSource;
    }

    public void setSource(String pSource) {
        mSource = pSource;
    }
}
