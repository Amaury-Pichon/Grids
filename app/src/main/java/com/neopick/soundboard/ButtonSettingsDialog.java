package com.neopick.soundboard;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.lang.reflect.Array;

import top.defaults.colorpicker.ColorObserver;
import top.defaults.colorpicker.ColorPickerView;

public class ButtonSettingsDialog extends DialogFragment {

    public interface SettingsDialogListener{
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    SettingsDialogListener listener;
    private int bgColor = Color.WHITE;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (SettingsDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString() + "must implement SettingDialogManager");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.activity_button_setting,null))
                .setTitle("Paramètres du bouton")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogPositiveClick(ButtonSettingsDialog.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogNegativeClick(ButtonSettingsDialog.this);
                    }
                });

//        TextView tvButtonName =
//        final EditText etButtonName = this.getView().findViewById(R.id.editText_button_name);
//        Button buttonCancel = getView().findViewById(R.id.button_cancel);
//        Button buttonOk = getView().findViewById(R.id.button_ok);
//        TextView tvButtonBackground = getView().findViewById(R.id.textView_bg_color);
//        ColorPickerView colorPicker = getView().findViewById(R.id.setting_colorPicker);
//        final View preview = getView().findViewById(R.id.view_preview);
//
//
//        tvButtonName.setText("Nom (8 caractères max) : ");
//        etButtonName.setHint("Nom du bouton");
//
//        tvButtonBackground.setText("Couleur de fond :");
//        colorPicker.setInitialColor(bgColor);
//
//        colorPicker.subscribe(new ColorObserver() {
//            @Override
//            public void onColor(int color, boolean fromUser, boolean shouldPropagate) {
//                preview.setBackgroundColor(color);
//                bgColor = color;
//            }
//        });
//
//        buttonCancel.setText("Cancel");
//        buttonOk.setText("OK");


        return builder.create();
    }
}
