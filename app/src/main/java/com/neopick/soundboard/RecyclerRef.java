package com.neopick.soundboard;

import java.lang.ref.WeakReference;

public class RecyclerRef {

    public static WeakReference<RecyclerActivity> mRecyclerActivityRef;
    public static void updateActivity(RecyclerActivity activity) {
        mRecyclerActivityRef = new WeakReference<RecyclerActivity>(activity);
    }
}
