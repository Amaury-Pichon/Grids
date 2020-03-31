package com.neopick.soundboard;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONBuilder {
    private static int TYPE_BUTTON_COUNT = 1;
    private static int TYPE_BUTTON_SOUND = 2;

    private FileBuilder fileBuilder;
    private ArrayList<ButtonModel> mButtons;

    public JSONBuilder(Context ctx, ArrayList<ButtonModel> pButtons){
        fileBuilder = new FileBuilder(ctx);
        mButtons = pButtons;
    }

    public JSONBuilder(Context ctx, String pName, ArrayList<ButtonModel> pButtons){
        fileBuilder = new FileBuilder(ctx, pName);
        mButtons = pButtons;
    }
    public void init(){
        create();
        String content = fileBuilder.read();
        try{
            JSONObject main = new JSONObject(content);
            boolean isGridExisting = main.has("grid");
            if(isGridExisting){
                deleteFile();
                create();
            }
            JSONArray grid = new JSONArray();
            for(ButtonModel button : mButtons){
                JSONObject buttonModel = new JSONObject();
                JSONObject buttonParams = new JSONObject();
                if(button instanceof ButtonCount){
                    buttonParams.put("type",TYPE_BUTTON_COUNT);
                    buttonParams.put("count", ((ButtonCount)button).getCount());
                }
                else if(button instanceof ButtonSound){
                    buttonParams.put("type", TYPE_BUTTON_SOUND);
                    buttonParams.put("source", ((ButtonSound)button).getSource());
                }
                buttonParams.put("name", button.getName());
                buttonParams.put("color", button.getColor());
                buttonModel.put(button.getPosition().toString(), buttonParams);
                grid.put(buttonModel);
            }
            main.put("grid", grid);
            fileBuilder.write(main.toString());
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void create(){
        fileBuilder.create();
    }

    public void deleteFile(){
        fileBuilder.delete();
    }

}
