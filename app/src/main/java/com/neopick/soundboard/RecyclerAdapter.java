package com.neopick.soundboard;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private static final int BUTTON_COUNT = 1;
    private static final int BUTTON_SOUND = 2;

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
        public int bgColor;

        public MyViewHolder(View v){
            super(v);
            deleteCheck = v.findViewById(R.id.checkBox_delete);
            view = v;
        }

        public void setup(ButtonModel button){
            titleView.setText(button.getName());
            bgColor = button.getColor();
            view.setBackgroundColor(bgColor);

            if (Color.red(bgColor) < 127 && Color.green(bgColor) < 127 && Color.blue(bgColor) < 127 && Color.alpha(bgColor) > 50) {
                titleView.setTextColor(Color.parseColor("#ffffff"));
            }


        }
    }

    public static class SoundViewHolder extends RecyclerAdapter.MyViewHolder{

        public SoundViewHolder(View v){
            super(v);
            titleView = v.findViewById(R.id.tv_sound_button_name);
        }
    }

    public static class CountViewHolder extends RecyclerAdapter.MyViewHolder{
        public TextView countView;

        public CountViewHolder(View v){
            super(v);
            titleView = v.findViewById(R.id.tv_button_title);
            countView = v.findViewById(R.id.tv_button_count);
        }

        public void setupCountButton(ButtonCount button){
            setup(button);
            if(button.getName().isEmpty()){
                makeViewOnlyButton(titleView, countView);
            }
            countView.setText(button.getCount());

            if (Color.red(bgColor) < 127 && Color.green(bgColor) < 127 && Color.blue(bgColor) < 127 && Color.alpha(bgColor) > 50) {
                countView.setTextColor(Color.parseColor("#ffffff"));
            }
        }

        public void setCount(ButtonCount button){
            button.setCount();
            countView.setText(button.getCount());
        }

        private void makeViewOnlyButton(TextView toDisapear, TextView toStay){

            toDisapear.setVisibility(View.GONE);
            toStay.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50.45f);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) toStay.getLayoutParams();
            params.gravity = Gravity.CENTER;
            toStay.setLayoutParams(params);

        }
    }

    public RecyclerAdapter(ArrayList<ButtonModel> recyclerGrid){
        mButtons = recyclerGrid;
        recyclerActivity = RecyclerRef.mRecyclerActivityRef.get();

    }
    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        MyViewHolder vh = null;
        if (viewType == BUTTON_COUNT){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_button,parent,false);
            vh = new CountViewHolder(v);
        }
        else if (viewType == BUTTON_SOUND){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sound_button,parent,false);
            vh = new SoundViewHolder(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        deleteMode = recyclerActivity.isDeleteMode();
        swapMode = recyclerActivity.isSwapMode();

        button = mButtons.get(position);
        button.setPosition(position);

        if(getItemViewType(position) == BUTTON_COUNT){
            ((CountViewHolder) holder).setupCountButton((ButtonCount) button);
        }
        else{
            holder.setup(button);
        }



//        holder.titleView.setText(button.getName());
//        int bgColor = button.getColor();
//        holder.view.setBackgroundColor(bgColor);

//        if (Color.red(bgColor) < 127 && Color.green(bgColor) < 127 && Color.blue(bgColor) < 127 && Color.alpha(bgColor) > 50) {
//            holder.titleView.setTextColor(Color.parseColor("#ffffff"));
//        }

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
                        if(getItemViewType(position) == BUTTON_COUNT){
                            ((CountViewHolder) holder).setCount((ButtonCount) button);
                        }
                        else if(getItemViewType(position) == BUTTON_SOUND){
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
    public int getItemViewType(int position) {
        int result = 0;
        if(mButtons.get(position) instanceof ButtonCount){
            result = BUTTON_COUNT;
        }
        else if (mButtons.get(position) instanceof ButtonSound){
            result = BUTTON_SOUND;
        }
        return result;
    }

    @Override
    public int getItemCount() {
        return mButtons.size();
    }

    public void resetCurrentSoundName(){
        currentSoundName = null;
    }
}
