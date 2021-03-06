package com.garethevans.church.opensongtablet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

class MenuHandlers {

    public interface MyInterface {
        void callIntent(String what, Intent i);
        void openMyDrawers(String what);
        void openFragment();
        void prepareOptionMenu();
        void doMoveInSet();
    }

    public static MyInterface mListener;

    static void actOnClicks(Context c, int menuitem) {
        mListener = (MyInterface) c;
        FullscreenActivity.setMoveDirection = "";

        switch (menuitem) {

            case R.id.action_search:
                // Open/close the song drawer
                if (mListener!=null) {
                    mListener.openMyDrawers("song_toggle");
                }
                break;

            case R.id.action_settings:
                // Open/close the option drawer
                if (mListener!=null) {
                    mListener.openMyDrawers("option_toggle");
                }
                break;

            case R.id.action_fullsearch:
                // Full search window
                FullscreenActivity.whattodo = "fullsearch";
                mListener.openFragment();
                break;

            case R.id.set_add:
                if ((FullscreenActivity.isSong || FullscreenActivity.isPDF) && !FullscreenActivity.whichSongFolder.startsWith("..")) {
                    if (FullscreenActivity.whichSongFolder.equals(FullscreenActivity.mainfoldername)) {
                        FullscreenActivity.whatsongforsetwork = "$**_" + FullscreenActivity.songfilename + "_**$";
                    } else {
                        FullscreenActivity.whatsongforsetwork = "$**_" + FullscreenActivity.whichSongFolder + "/"
                                + FullscreenActivity.songfilename + "_**$";
                    }
                    // Allow the song to be added, even if it is already there
                    FullscreenActivity.mySet = FullscreenActivity.mySet + FullscreenActivity.whatsongforsetwork;
                    // Tell the user that the song has been added.
                    FullscreenActivity.myToastMessage = "\"" + FullscreenActivity.songfilename + "\" "
                            + c.getResources().getString(R.string.addedtoset);
                    ShowToast.showToast(c);
                    // Vibrate to indicate something has happened
                    DoVibrate.vibrate(c,50);

                    // Save the set and other preferences
                    Preferences.savePreferences();
                    if (mListener!=null) {
                        mListener.prepareOptionMenu();
                    }
                }
                break;
        }
    }

    static void forceOverFlow(Context c, ActionBar ab, Menu menu) {
        try {
            ViewConfiguration config = ViewConfiguration.get(c);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }

        if (ab != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    @SuppressLint("PrivateApi") Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (NoSuchMethodException e) {
                    Log.e("menu", "onMenuOpened", e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}