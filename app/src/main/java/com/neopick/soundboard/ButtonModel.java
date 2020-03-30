package com.neopick.soundboard;

import android.graphics.Color;

public class ButtonModel {
    private String mName;
    private Integer mCount;
    private int mColor;
    private boolean isDeletable;
    private int mPosition;
    private int mSwapCount;

    public ButtonModel (String pName, int pColor) {
        mName = pName;
        mColor = pColor;
        mCount = 0;
        isDeletable = false;
        mSwapCount = 0;
        mPosition = 0;
    }

    public String getName() {
        return mName;
    }

    public void setCount() {
        this.mCount ++;
    }

    public int getColor() {
        return mColor;
    }

    public Integer getCount() {
        return mCount;
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
