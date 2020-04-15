package com.neopick.soundboard;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONParser {

    private FileBuilder fileBuilder;
    private ArrayList<ButtonModel> mButtons;

    public JSONParser(Context ctx, String fileName){
        fileBuilder = new FileBuilder(ctx, fileName);
        mButtons = new ArrayList<ButtonModel>();
    }

    public JSONParser(Context ctx){
        fileBuilder = new FileBuilder(ctx);
        mButtons = new ArrayList<ButtonModel>();
    }

    public ArrayList<ButtonModel> init(){

        String content = fileBuilder.read();
        try{
            JSONObject mainObject = new JSONObject(content);
            JSONArray grid = mainObject.getJSONArray("grid");
            for(Integer index = 0 ; index < grid.length(); index++){
                JSONObject buttonModel = grid.getJSONObject(index);
                JSONObject buttonParams = buttonModel.getJSONObject(index.toString());
                int type = buttonParams.getInt("type");
                if(type == JSONBuilder.TYPE_BUTTON_COUNT){
                    mButtons.add(new ButtonCount(buttonParams.getString("name"), buttonParams.getInt("color"), buttonParams.getInt("count")));
                    Log.e("JSONParser","Count du ButtonCount d'index " + index + " : " + buttonParams.getInt("count"));
                }
                else if(type == JSONBuilder.TYPE_BUTTON_SOUND){
                    mButtons.add(new ButtonSound(buttonParams.getString("name"), buttonParams.getInt("color"), buttonParams.getString("source")));
                    Log.e("JSONParser","Source du ButtonSound d'index " + index + " : " + buttonParams.getString("source"));
                }
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        return  mButtons;
    }
}
