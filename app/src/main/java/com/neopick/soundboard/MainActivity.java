package com.neopick.soundboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import top.defaults.colorpicker.ColorObserver;
import top.defaults.colorpicker.ColorPickerView;


public class MainActivity extends AppCompatActivity{

    private static Context appContext;

    Button button;
    Button buttonColor;
    ColorPickerView colorPicker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = getApplicationContext();
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button_test);
        button.setText("Le début d'une aventure!");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setText("Youpi!");
                Intent intent = new Intent(MainActivity.this, SoundBoard.class);
                startActivity(intent);
                finish();
            }
        });

        buttonColor = findViewById(R.id.button_main_color);
        buttonColor.setText("Test JSON");
        buttonColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonTest();
            }
        });
        colorPicker = findViewById(R.id.main_colorPicker);
        colorPicker.setInitialColor(Color.WHITE);

        colorPicker.subscribe(new ColorObserver() {
            @Override
            public void onColor(int color, boolean fromUser, boolean shouldPropagate) {
                buttonColor.setBackgroundColor(color);
            }
        });

    }

    public static Context getAppContext() {
        return appContext;
    }

    public void jsonTest(){
        FileBuilder fileBuilder = new FileBuilder(this);
        fileBuilder.create();
        String response = fileBuilder.read();
        Log.v("LECTURE_JSON_AVANT", response);

        try{
            JSONObject messageDetails = new JSONObject(response);
            Boolean isUserExisting = messageDetails.has("Username");
            if(!isUserExisting){
                int id = 323;
                JSONArray newUserMessages = new JSONArray();
                newUserMessages.put(id);
                messageDetails.put("Username", newUserMessages);
            }
            else{
                int id2 = 54621;
                JSONArray userMessages = (JSONArray) messageDetails.get("Username");
                userMessages.put(id2);
            }
            fileBuilder.write(messageDetails.toString());
            response = fileBuilder.read();
            Log.v("LECTURE_JSON_APRES", response);
        }catch(JSONException e){
            e.printStackTrace();
        }
        fileBuilder.scan();
        //fileBuilder.delete();

    }
}
