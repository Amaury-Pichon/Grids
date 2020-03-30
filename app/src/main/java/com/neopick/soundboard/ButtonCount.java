package com.neopick.soundboard;

public class ButtonCount extends ButtonModel {

    private int layout;

    public ButtonCount(String pName, int pColor){
        super();
        mName = pName;
        mColor = pColor;
        layout = R.layout.item_button;
    }

    public int getLayout() {
        return layout;
    }
}
