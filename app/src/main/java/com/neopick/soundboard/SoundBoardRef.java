package com.neopick.soundboard;

import android.app.Activity;

import java.lang.ref.WeakReference;

public class SoundBoardRef {
    public static WeakReference<SoundBoard> mSoundBoardActivityRef;
    public static void updateActivity(SoundBoard activity) {
        mSoundBoardActivityRef = new WeakReference<SoundBoard>(activity);
    }
}
