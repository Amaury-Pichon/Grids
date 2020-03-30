package com.neopick.soundboard;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ButtonAdapter extends ArrayAdapter<ButtonModel> {

    private ButtonModel gridButton;
    private int bgColor = Color.GRAY;
    private int selectSwapCount = 0;
    private ButtonModel lastGridButtonSelected;

    TextView soundButtonName ;
    TextView gridButtonName;
    TextView gridButtonCount;

    public ButtonAdapter(Context context, ArrayList<ButtonModel> gridButtons){
        super(context, 0 , gridButtons);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        gridButton = getItem(position);
        gridButton.setPosition(position);



        if(gridButton instanceof ButtonCount){
            if(null == convertView){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_button, parent, false);
            }
            gridButtonName = convertView.findViewById(R.id.tv_button_title);
            gridButtonCount = convertView.findViewById(R.id.tv_button_count);

            if(gridButton.getName().isEmpty()){
                makeViewOnlyButton(gridButtonName, gridButtonCount);
            }
            else{
                gridButtonName.setText(gridButton.getName());
            }

            gridButtonCount.setText(((ButtonCount)gridButton).getCount().toString());
        }
        else if(gridButton instanceof ButtonSound){
            if(null == convertView){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_sound_button, parent, false);
            }
            soundButtonName = convertView.findViewById(R.id.tv_sound_button_name);
            soundButtonName.setText(gridButton.getName());
        }

        CheckBox deleteCheck = convertView.findViewById(R.id.checkBox_delete);
        bgColor = gridButton.getColor();

        convertView.setBackgroundColor(bgColor);

        if(Color.red(bgColor) < 127 && Color.green(bgColor) < 127 && Color.blue(bgColor)<127 && Color.alpha(bgColor)>50){
            if(gridButton instanceof ButtonCount){
                gridButtonName.setTextColor(Color.parseColor("#ffffff"));
                gridButtonCount.setTextColor(Color.parseColor("#ffffff"));
            }
            else if(gridButton instanceof ButtonSound){
                soundButtonName.setTextColor(Color.parseColor("#ffffff"));
            }
        }

        if(!SoundBoard.deleteMode && !SoundBoard.swapMode){
            deleteCheck.setVisibility(View.GONE);
            deleteCheck.setChecked(false);
            deleteCheck.setOnCheckedChangeListener(null);
            convertView.setOnClickListener(null);
            selectSwapCount = 0;
            lastGridButtonSelected = null;

            convertView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    gridButton = getItem(position);
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        v.setBackgroundColor(gridButton.getColor() + Color.LTGRAY);
                    }
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        if(gridButton instanceof ButtonCount){
                            ((ButtonCount)gridButton).setCount();
                            gridButtonCount.setText(((ButtonCount)gridButton).getCount().toString());
                        }
                    }
                    if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
                        v.setBackgroundColor(gridButton.getColor());
                    }
                    return true;
                }
            });
        }
        else if(SoundBoard.deleteMode){
            deleteCheck.setVisibility(View.VISIBLE);
            convertView.setOnTouchListener(null);
            deleteCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    gridButton = getItem(position);
                    gridButton.setDeletable(isChecked);
                }
            });
        }
        else if(SoundBoard.swapMode){

            convertView.setOnTouchListener(null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int interPos = 0;
                    gridButton = getItem(position);
                    if(gridButton.getSwapCount()%2 == 0 && selectSwapCount <= 1){
                        v.setBackgroundColor(Color.GRAY);
                        gridButton.increaseSwapCount();
                        selectSwapCount ++;
                    }
                    else if(gridButton.getSwapCount()%2 == 1 && selectSwapCount <= 2) {
                        v.setBackgroundColor(gridButton.getColor());
                        gridButton.increaseSwapCount();
                        selectSwapCount--;
                    }

                    if(gridButton != lastGridButtonSelected){
                        if(null == lastGridButtonSelected){
                            lastGridButtonSelected = gridButton;
                        }
                        if(selectSwapCount==2 && gridButton.getSwapCount()%2 ==1) {
                            interPos = gridButton.getPosition();
                            gridButton.setPosition(lastGridButtonSelected.getPosition());
                            lastGridButtonSelected.setPosition(interPos);
                        }
                        lastGridButtonSelected = gridButton;
                    }
                }
            });

        }

        return convertView;
    }

    private void makeViewOnlyButton(TextView toDisapear, TextView toStay){

        toDisapear.setVisibility(View.GONE);
        toStay.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50.45f);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) toStay.getLayoutParams();
        params.gravity = Gravity.CENTER;
        toStay.setLayoutParams(params);

    }
}
