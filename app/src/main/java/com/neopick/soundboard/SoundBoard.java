package com.neopick.soundboard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;

public class SoundBoard extends AppCompatActivity {

    private static MediaPlayer gridPlayer;

    GridView soundBoard;
    ArrayList<ButtonModel> gridButtons;
    ButtonAdapter adapter;
    int lastBgColor = Color.WHITE;
    private boolean deleteMode = false;
    private boolean swapMode = false;
    ArrayList<View> gridOptions = new ArrayList<View>();
    ArrayList<View> deleteOptions = new ArrayList<View>();
    ArrayList<View> swapOptions = new ArrayList<View>();

    private File file;

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
        SoundBoardRef.updateActivity(this);

        file = new File(this.getExternalFilesDir(null), FileBuilder.JSON_BUTTONS);

        gridPlayer = new MediaPlayer();

        soundBoard = findViewById(R.id.grid_button);


        if(file.exists()){
            JSONParser parser = new JSONParser(this);
            gridButtons = parser.init();
        }
        else{
            gridButtons = new ArrayList<ButtonModel>();
        }

//        gridButtons.add(new ButtonSound("Murloc", Color.GREEN, "http://sp.athanyl.net/sound/murloc/1.mp3"));
//        gridButtons.add(new ButtonSound("Oui", Color.CYAN, "http://sp.athanyl.net/sound/oui/1.mp3"));
//        gridButtons.add(new ButtonSound("Trap", Color.BLACK, "http://sp.athanyl.net/sound/trap/1.mp3"));
//        gridButtons.add(new ButtonSound("Turret", Color.YELLOW, "http://sp.athanyl.net/sound/rand/1.mp3"));

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
        final Button json = findViewById(R.id.button_json_test);
        json.setText("JSON!");
        final Button cancelMode = findViewById((R.id.button_cancel_mode));
        cancelMode.setText("Cancel");
        final Button confirmDeletion = findViewById(R.id.confirm_deletion);
        confirmDeletion.setText("DELETE");
        confirmDeletion.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        gridOptions.add(addButton);
        gridOptions.add(addButtonSound);
        gridOptions.add(delete);
        gridOptions.add(swap);
        gridOptions.add(json);
        deleteOptions.add(cancelMode);
        deleteOptions.add(confirmDeletion);
        swapOptions.add(cancelMode);

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

        json.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonTest();
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        gridPlayer = new MediaPlayer();
        adapter.resetCurrentSoundName();
    }

    @Override
    protected void onStop(){
        super.onStop();

        gridPlayer.release();
        gridPlayer = null;
    }

    public static MediaPlayer getGridPlayer() {
        return gridPlayer;
    }

    protected void toToggle(ArrayList<View> toActivate, ArrayList<View> toDeactivate){
        for (View option : toActivate){
            option.setVisibility(View.VISIBLE);
        }
        for (View option : toDeactivate){
            option.setVisibility(View.GONE);
        }
    }

    public void jsonTest(){
        JSONBuilder jsonBuilder = new JSONBuilder(this, gridButtons);
        jsonBuilder.init();
    }

    public void swap(int firstPos, int secondPos){
        ButtonModel first = gridButtons.get(firstPos);
        ButtonModel second = gridButtons.get(secondPos);
        gridButtons.set(firstPos, second);
        gridButtons.set(secondPos, first);
        soundBoard.setAdapter(adapter);
    }

    public boolean isDeleteMode() {
        return deleteMode;
    }

    public boolean isSwapMode() {
        return swapMode;
    }
}

