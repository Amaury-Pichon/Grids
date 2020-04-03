package com.neopick.soundboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ButtonAdapter extends ArrayAdapter<ButtonModel> {

    private ButtonModel gridButton;
    private int bgColor = Color.GRAY;
    private int selectSwapCount = 0;
    private ButtonModel lastGridButtonSelected;

    private MediaPlayer buttonPlayer = SoundBoard.getGridPlayer();
    private boolean isMediaPrepared = false;
    private String currentSoundName = "";

    private TextView soundButtonName = null ;
    private TextView gridButtonName = null;
    private TextView gridButtonCount = null;

    private SoundBoard soundBoardActivity;
    private boolean deleteMode;
    private boolean swapMode;

    public ButtonAdapter(Context context, ArrayList<ButtonModel> gridButtons){
        super(context, 0 , gridButtons);
        soundBoardActivity = SoundBoardRef.mSoundBoardActivityRef.get();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

        deleteMode = soundBoardActivity.isDeleteMode();
        swapMode = soundBoardActivity.isSwapMode();

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

        buttonPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isMediaPrepared = true;
                buttonPlayer.start();
            }
        });

        if(!deleteMode && !swapMode){
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
                            gridButtonCount = v.findViewById(R.id.tv_button_count);
                            ((ButtonCount)gridButton).setCount();
                            gridButtonCount.setText(((ButtonCount)gridButton).getCount().toString());
                        }
                        else if(gridButton instanceof ButtonSound){
                            if(currentSoundName != gridButton.getName()) {
                                currentSoundName = gridButton.getName();

                                try {
                                    buttonPlayer.reset();
                                    buttonPlayer.setDataSource(((ButtonSound) gridButton).getSource());
                                    buttonPlayer.prepareAsync();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                            }
                            if(isMediaPrepared && buttonPlayer.isPlaying()){
                                buttonPlayer.seekTo(0);
                            }
                            else if (isMediaPrepared){
                                buttonPlayer.start();
                            }
                            else{
                                Toast.makeText(v.getContext(), gridButton.getName() + " n'est pas encore prêt", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
                        v.setBackgroundColor(gridButton.getColor());
                    }
                    return true;
                }
            });
        }
        else if(deleteMode){
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
        else if(swapMode){

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
                        else {
                            selectSwapCount = 0;
                            lastGridButtonSelected.setSwapCount(0);
                            gridButton.setSwapCount(0);
                            soundBoardActivity.swap(lastGridButtonSelected.getPosition(), gridButton.getPosition());
                            lastGridButtonSelected = null;
                        }
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

    public void resetCurrentSoundName(){
        currentSoundName = null;
    }
}
