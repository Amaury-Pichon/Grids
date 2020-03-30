package com.neopick.soundboard;

import android.graphics.Color;

public class ButtonModel {
    protected String mName;

    protected int mColor;
    protected boolean isDeletable;
    protected int mPosition;
    protected int mSwapCount;

    public ButtonModel(){
        mName = "";
        mColor = Color.WHITE;
        isDeletable = false;
        mSwapCount = 0;
        mPosition = 0;
    }

    public ButtonModel (String pName, int pColor) {
        mName = pName;
        mColor = pColor;
        isDeletable = false;
        mSwapCount = 0;
        mPosition = 0;
    }

    public String getName() {
        return mName;
    }



    public int getColor() {
        return mColor;
    }

    public boolean isDeletable() {
        return isDeletable;
    }

    public void setDeletable(boolean deletable) {
        isDeletable = deletable;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int pPosition) {
        mPosition = pPosition;
    }

    public int getSwapCount() {
        return mSwapCount;
    }

    public void increaseSwapCount() {
        this.mSwapCount ++;
    }

    public void setSwapCount(int pSwapCount) {
        this.mSwapCount = pSwapCount;
    }
}
