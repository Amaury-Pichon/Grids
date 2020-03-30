package com.neopick.soundboard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import java.util.ArrayList;

public class SoundBoard extends AppCompatActivity {

    GridView soundBoard;
    ArrayList<ButtonModel> gridButtons;
    ButtonAdapter adapter;
    int lastBgColor = Color.WHITE;
    public static boolean deleteMode = false;
    public static boolean swapMode = false;
    ArrayList<View> gridOptions = new ArrayList<View>();
    ArrayList<View> deleteOptions = new ArrayList<View>();
    ArrayList<View> swapOptions = new ArrayList<View>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        lastBgColor = data.getIntExtra("BG_COLOR", 0);

        if (resultCode != RESULT_CANCELED) {
            String buttonName = data.getStringExtra("BUTTON_NAME");


            if(requestCode == 1){
                gridButtons.add(new ButtonCount(buttonName, lastBgColor));
            }
            else if(requestCode == 2){
                String soundSource = data.getStringExtra("SOUND_SOURCE");
                gridButtons.add(new ButtonSound(buttonName, lastBgColor, soundSource));
            }

            soundBoard.setAdapter(adapter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundboard);

        soundBoard = findViewById(R.id.grid_button);
        gridButtons = new ArrayList<ButtonModel>();

        adapter = new ButtonAdapter(this, gridButtons);
        soundBoard.setAdapter(adapter);

        final Button addButton = findViewById(R.id.button_add);
        addButton.setText("+ Count");
        final Button addButtonSound = findViewById(R.id.button_add_sound);
        addButtonSound.setText("+ Sound");
        final Button delete = findViewById(R.id.button_delete);
        delete.setText("-");
        final Button swap = findViewById(R.id.button_swap);
        swap.setText("Swap");
        final Button confirmSwap = findViewById(R.id.confirm_swap);
        confirmSwap.setText("SWAP!!");
        confirmSwap.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        final Button cancelMode = findViewById((R.id.button_cancel_mode));
        cancelMode.setText("Cancel");
        final Button confirmDeletion = findViewById(R.id.confirm_deletion);
        confirmDeletion.setText("DELETE");
        confirmDeletion.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        gridOptions.add(addButton);
        gridOptions.add(addButtonSound);
        gridOptions.add(delete);
        gridOptions.add(swap);
        deleteOptions.add(cancelMode);
        deleteOptions.add(confirmDeletion);
        swapOptions.add(cancelMode);
        swapOptions.add(confirmSwap);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SoundBoard.this, ButtonSettingActivity.class);
                intent.putExtra("LAST_BG_COLOR", lastBgColor);
                intent.setIdentifier("CountButton");
                startActivityForResult(intent, 1);
            }
        });

        addButtonSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SoundBoard.this, ButtonSettingActivity.class);
                intent.putExtra("LAST_BG_COLOR", lastBgColor);
                intent.setIdentifier("SoundButton");
                startActivityForResult(intent, 2);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toToggle(deleteOptions,gridOptions);
                deleteMode = true;
                soundBoard.setAdapter(adapter);
            }
        });

        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toToggle(swapOptions, gridOptions);
                swapMode = true;
                soundBoard.setAdapter(adapter);
            }
        });

        cancelMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteMode){
                    toToggle(gridOptions,deleteOptions);
                    deleteMode = false;
                }
                if(swapMode){
                    toToggle(gridOptions, swapOptions);
                    for(ButtonModel button : gridButtons){
                        button.setSwapCount(0);
                    }
                    swapMode = false;
                }
                soundBoard.setAdapter(adapter);
            }
        });

        confirmDeletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ButtonModel> toDelete = new ArrayList<ButtonModel>();
                for(ButtonModel button : gridButtons){
                    if(button.isDeletable()){
                        toDelete.add(button);
                    }
                }
                gridButtons.removeAll(toDelete);
                soundBoard.setAdapter(adapter);
                toToggle(gridOptions,deleteOptions);
                deleteMode = false;
            }
        });

        confirmSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ButtonModel> toMove = new ArrayList<ButtonModel>();
                for(ButtonModel button : gridButtons){
                    toMove.add(button);
                    button.setSwapCount(0);
                }
                for(ButtonModel button : toMove){
                    gridButtons.set(button.getPosition(), button);
                }
                soundBoard.setAdapter(adapter);
                toToggle(gridOptions,swapOptions);
                swapMode = false;
            }
        });
    }

    protected void toToggle(ArrayList<View> toActivate, ArrayList<View> toDeactivate){
        for (View option : toActivate){
            option.setVisibility(View.VISIBLE);
        }
        for (View option : toDeactivate){
            option.setVisibility(View.GONE);
        }
    }
}

