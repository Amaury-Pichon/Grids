package com.neopick.soundboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import top.defaults.colorpicker.ColorObserver;
import top.defaults.colorpicker.ColorPickerView;


public class MainActivity extends AppCompatActivity{

    Button button;
    Button buttonColor;

    ColorPickerView colorPicker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        colorPicker = findViewById(R.id.main_colorPicker);
        colorPicker.setInitialColor(Color.WHITE);

        colorPicker.subscribe(new ColorObserver() {
            @Override
            public void onColor(int color, boolean fromUser, boolean shouldPropagate) {
                buttonColor.setBackgroundColor(color);
            }
        });




    }
}
