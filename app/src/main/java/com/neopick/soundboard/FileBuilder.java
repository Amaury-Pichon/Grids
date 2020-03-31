package com.neopick.soundboard;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileBuilder {

    private static final String JSON_BUTTONS = "json-buttons.txt";

    private File file;
//    private FileReader reader;
//    private FileWriter writer;
//    private BufferedReader bufferedReader;
//    private BufferedWriter bufferedWriter;
//    private String response;
    private Context mContext;

    private OutputStream fOut;
    private InputStream fIn;

    public FileBuilder(Context ctx, String pFileName){
        file = new File(ctx.getExternalFilesDir(null),pFileName);
        Log.v("PATH", ctx.getFilesDir().getPath());
        mContext = ctx;
    }


    public FileBuilder(Context ctx){
        file = new File(ctx.getExternalFilesDir(null), JSON_BUTTONS);
        Log.v("PATH", ctx.getFilesDir().getPath());
//        reader = null;
//        writer = null;
//        bufferedReader = null;
//        bufferedWriter = null;
//        response = null;
        mContext = ctx;
    }

    public void create(){
        if(!file.exists()){
            try{
                file.createNewFile();
                fOut = new FileOutputStream(file);
                fOut.write("{}".getBytes());
                fOut.close();

//                writer = new FileWriter(file.getAbsoluteFile());
//                bufferedWriter = new BufferedWriter(writer);
//                bufferedWriter.write("{}");
//                bufferedWriter.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public String read(){
        StringBuffer output = new StringBuffer();
        String line = "";
        try{
            fIn = new FileInputStream(file);
            // reader = new FileReader(file.getAbsolutePath());
            //bufferedReader = new BufferedReader(reader);
//            String line = "";
            int c;
            while ((c = fIn.read()) != -1) {

                line = line + Character.toString((char) c);
            }
//            while ((line = bufferedReader.readLine()) != null){
//            output.append(line + "\n");
//            response = output.toString();
//            bufferedReader.close();
            fIn.close();
        }catch(IOException e){
            e.printStackTrace();
        }
//        return response;
        return line;
    }

    public void write(String input){
        try{
            fOut = new FileOutputStream(file);
            //writer = new FileWriter(file.getAbsoluteFile());
            //bufferedWriter = new BufferedWriter(writer);
            fOut.write(input.getBytes());
            fOut.flush();
            fOut.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void delete(){
        file.delete();
    }

    public void scan(){
        MediaScannerConnection.scanFile(mContext,
                new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }
}
