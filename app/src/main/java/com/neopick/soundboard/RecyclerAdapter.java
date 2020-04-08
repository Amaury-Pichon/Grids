package com.neopick.soundboard;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private ArrayList<ButtonModel> mButtons;
    private  RecyclerActivity recyclerActivity;
    private ButtonModel button;
    private int selectSwapCount = 0;
    private ButtonModel lastGridButtonSelected;

    private MediaPlayer buttonPlayer = RecyclerActivity.getGridPlayer();
    private boolean isMediaPrepared = false;

    private boolean deleteMode;
    private boolean swapMode;
    private String currentSoundName = "";

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView titleView;
        public CheckBox deleteCheck;
        public View view;
        public MyViewHolder(View v){
            super(v);
            titleView = v.findViewById(R.id.tv_sound_button_name);
            deleteCheck = v.findViewById(R.id.checkBox_delete);
            view = v;
        }
    }

    public RecyclerAdapter(ArrayList<ButtonModel> recyclerGrid){
        mButtons = recyclerGrid;
        recyclerActivity = RecyclerRef.mRecyclerActivityRef.get();

    }
    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sound_button,parent,false);

        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        deleteMode = recyclerActivity.isDeleteMode();
        swapMode = recyclerActivity.isSwapMode();

        button = mButtons.get(position);
        button.setPosition(position);

        holder.titleView.setText(button.getName());
        int bgColor = button.getColor();
        holder.view.setBackgroundColor(bgColor);

        if (Color.red(bgColor) < 127 && Color.green(bgColor) < 127 && Color.blue(bgColor) < 127 && Color.alpha(bgColor) > 50) {
            holder.titleView.setTextColor(Color.parseColor("#ffffff"));
        }

        buttonPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isMediaPrepared = true;
                buttonPlayer.start();
            }
        });

        if (!deleteMode && !swapMode) {
            holder.deleteCheck.setVisibility(View.GONE);
            holder.deleteCheck.setChecked(false);
            button.setDeletable(false);
            holder.deleteCheck.setOnCheckedChangeListener(null);
            holder.view.setOnClickListener(null);
            selectSwapCount = 0;
            lastGridButtonSelected = null;

            holder.view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    button = mButtons.get(position);
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        v.setBackgroundColor(button.getColor() + Color.LTGRAY);
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                            if(currentSoundName != button.getName()) {
                                currentSoundName = button.getName();

                                try {
                                    buttonPlayer.reset();
                                    buttonPlayer.setDataSource(((ButtonSound) button).getSource());
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
                                Toast.makeText(v.getContext(), button.getName() + " n'est pas encore prêt", Toast.LENGTH_SHORT).show();
                            }
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                        v.setBackgroundColor(button.getColor());
                    }
                    return true;
                }
            });
        } else if (deleteMode) {
            holder.deleteCheck.setVisibility(View.VISIBLE);
            holder.view.setOnTouchListener(null);
            holder.deleteCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    button = mButtons.get(position);
                    button.setDeletable(isChecked);
                    recyclerActivity.setDeleteButton();
                }
            });
        } else if (swapMode) {

            holder.view.setOnTouchListener(null);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    button = mButtons.get(position);
                    if (button.getSwapCount() % 2 == 0 && selectSwapCount <= 1) {
                        v.setBackgroundColor(Color.GRAY);
                        button.increaseSwapCount();
                        selectSwapCount++;
                    } else if (button.getSwapCount() % 2 == 1 && selectSwapCount <= 2) {
                        v.setBackgroundColor(button.getColor());
                        button.increaseSwapCount();
                        selectSwapCount--;
                    }

                    if (button != lastGridButtonSelected) {
                        if (null == lastGridButtonSelected) {
                            lastGridButtonSelected = button;
                        } else {
                            selectSwapCount = 0;
                            lastGridButtonSelected.setSwapCount(0);
                            button.setSwapCount(0);
                            recyclerActivity.swap(lastGridButtonSelected.getPosition(), button.getPosition());
                            lastGridButtonSelected = null;
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mButtons.size();
    }

    public void resetCurrentSoundName(){
        currentSoundName = null;
    }
}
