package com.neopick.soundboard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class RecyclerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private static MediaPlayer gridPlayer;

    ImageButton confirmDeletion;
    ArrayList<ButtonModel> recyclerGridButtons;
    int lastBgColor = Color.WHITE;
    private boolean deleteMode = false;
    private boolean swapMode = false;
    ArrayList<View> gridOptions = new ArrayList<View>();
    ArrayList<View> cancelOptions = new ArrayList<View>();

    private File file;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        lastBgColor = data.getIntExtra("BG_COLOR", 0);

        if (resultCode != RESULT_CANCELED) {
            String buttonName = data.getStringExtra("BUTTON_NAME");


            if(requestCode == 1){
                recyclerGridButtons.add(new ButtonCount(buttonName, lastBgColor));
            }
            else
                if(requestCode == 2){
                String soundSource = data.getStringExtra("SOUND_SOURCE");
                recyclerGridButtons.add(new ButtonSound(buttonName, lastBgColor, soundSource));
            }

            recyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_activity);
        RecyclerRef.updateActivity(this);

        file = new File(this.getExternalFilesDir(null), FileBuilder.NEW_BUTTONS);


        gridPlayer = new MediaPlayer();
        recyclerView = findViewById(R.id.recycler_view);

        layoutManager = new GridLayoutManager(this,4);
        recyclerView.setLayoutManager(layoutManager);

        if(file.exists()){
            JSONParser parser = new JSONParser(this, FileBuilder.NEW_BUTTONS);
            recyclerGridButtons = parser.init();
        }
        else{
            recyclerGridButtons = new ArrayList<ButtonModel>();
            recyclerGridButtons.add(new ButtonSound("Murloc", Color.GREEN, "http://sp.athanyl.net/sound/murloc/1.mp3"));
            recyclerGridButtons.add(new ButtonSound("Oui", Color.CYAN, "http://sp.athanyl.net/sound/oui/1.mp3"));
            recyclerGridButtons.add(new ButtonSound("Trap", Color.BLACK, "http://sp.athanyl.net/sound/trap/1.mp3"));
            recyclerGridButtons.add(new ButtonSound("Turret", Color.YELLOW, "http://sp.athanyl.net/sound/rand/1.mp3"));
        }

        mAdapter = new RecyclerAdapter(recyclerGridButtons);
        recyclerView.setAdapter(mAdapter);

        final Button addButton = findViewById(R.id.recycler_button_add);
        addButton.setText("+ ");
        final Button addButtonSound = findViewById(R.id.recycler_button_add_sound);
        addButtonSound.setText("+ ");
        final ImageButton delete = findViewById(R.id.recycler_button_delete);
        final ImageButton swap = findViewById(R.id.recycler_button_swap);
//        swap.setText("Swap");
        final ImageButton json = findViewById(R.id.recycler_button_json_test);
//        json.setText("SAVE");
        final ImageButton cancelMode = findViewById((R.id.recycler_button_cancel_mode));
//        cancelMode.setText("Cancel");
        confirmDeletion = findViewById(R.id.recycler_confirm_deletion);
//        confirmDeletion.setText("DELETE");
//        confirmDeletion.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        gridOptions.add(addButton);
        gridOptions.add(addButtonSound);
        gridOptions.add(delete);
        gridOptions.add(swap);
        gridOptions.add(json);
        cancelOptions.add(cancelMode);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(RecyclerActivity.this, ButtonSettingActivity.class);
                    intent.putExtra("LAST_BG_COLOR", lastBgColor);
                    intent.setIdentifier("CountButton");
                    startActivityForResult(intent, 1);
            }
        });

        addButtonSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(RecyclerActivity.this, ButtonSettingActivity.class);
                    intent.putExtra("LAST_BG_COLOR", lastBgColor);
                    intent.setIdentifier("SoundButton");
                    startActivityForResult(intent, 2);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toToggle(cancelOptions,gridOptions);
                deleteMode = true;
                recyclerView.setAdapter(mAdapter);
            }
        });

        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toToggle(cancelOptions, gridOptions);
                swapMode = true;
                recyclerView.setAdapter(mAdapter);
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
                    toToggle(gridOptions,cancelOptions);
                    confirmDeletion.setVisibility(View.GONE);
                    deleteMode = false;
                }
                if(swapMode){
                    toToggle(gridOptions, cancelOptions);
                    for(ButtonModel button : recyclerGridButtons){
                        button.setSwapCount(0);
                    }
                    swapMode = false;
                }
                recyclerView.setAdapter(mAdapter);
            }
        });

        confirmDeletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ButtonModel> toDelete = new ArrayList<ButtonModel>();
                for(ButtonModel button : recyclerGridButtons){
                    if(button.isDeletable()){
                        toDelete.add(button);
                    }
                }
                recyclerGridButtons.removeAll(toDelete);
                recyclerView.setAdapter(mAdapter);
                toToggle(gridOptions,cancelOptions);
                confirmDeletion.setVisibility(View.GONE);
                deleteMode = false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        gridPlayer = new MediaPlayer();
        mAdapter.resetCurrentSoundName();
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
        JSONBuilder jsonBuilder = new JSONBuilder(this, FileBuilder.NEW_BUTTONS, recyclerGridButtons);
        jsonBuilder.init();
    }

    public void swap(int firstPos, int secondPos){
        ButtonModel first = recyclerGridButtons.get(firstPos);
        ButtonModel second = recyclerGridButtons.get(secondPos);
        recyclerGridButtons.set(firstPos, second);
        recyclerGridButtons.set(secondPos, first);
        recyclerView.setAdapter(mAdapter);
    }

    public boolean isDeleteMode() {
        return deleteMode;
    }

    public boolean isSwapMode() {
        return swapMode;
    }

    public void setDeleteButton(){
        int checkCount = 0;
        for(ButtonModel button : recyclerGridButtons){
            if(button.isDeletable()){
                checkCount ++;
            }
        }
        if(checkCount != 0){
            confirmDeletion.setVisibility(View.VISIBLE);
        }
        else {
            confirmDeletion.setVisibility(View.GONE);
        }
    }
}
