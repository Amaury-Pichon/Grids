package com.neopick.soundboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import top.defaults.colorpicker.ColorObserver;
import top.defaults.colorpicker.ColorPickerView;

public class ButtonSettingActivity extends AppCompatActivity {

    private Intent incomingIntent;
    Intent leavingIntent = new Intent();
    private int bgColor = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_setting);

        this.setFinishOnTouchOutside(false);

        incomingIntent  = getIntent();
        bgColor = incomingIntent.getIntExtra("LAST_BG_COLOR", Color.WHITE);

        setTitle("Paramètres");

        TextView tvButtonName = findViewById(R.id.textView_button_name);
        final EditText etButtonName = findViewById(R.id.editText_button_name);
        Button buttonCancel = findViewById(R.id.button_cancel_add);
        Button buttonOk = findViewById(R.id.button_ok);
        TextView tvButtonBackground = findViewById(R.id.textView_bg_color);
        ColorPickerView colorPicker = findViewById(R.id.setting_colorPicker);
        final View preview = findViewById(R.id.view_preview);


        tvButtonName.setText("Nom (8 caractères max) : ");
        etButtonName.setHint("Nom du bouton");

        tvButtonBackground.setText("Couleur de fond :");
        colorPicker.setInitialColor(bgColor);

        colorPicker.subscribe(new ColorObserver() {
            @Override
            public void onColor(int color, boolean fromUser, boolean shouldPropagate) {
                preview.setBackgroundColor(color);
                bgColor = color;
            }
        });

        buttonCancel.setText("Cancel");
        buttonOk.setText("OK");

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leavingIntent.putExtra("BG_COLOR", bgColor);
                setResult(0, leavingIntent);
                finish();
            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonName = etButtonName.getText().toString();
                leavingIntent.putExtra("BG_COLOR", bgColor);
                if(buttonName.length() <= 8) {
                    leavingIntent.putExtra("BUTTON_NAME", buttonName);
                    setResult(1, leavingIntent);
                    finish();
                }
                else{
                    Toast.makeText(ButtonSettingActivity.this, "Nom de bouton trop long", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}

