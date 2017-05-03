package gr16.android.heavensgps.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ChrisPCBeast on 03-05-2017.
 * Why create a sub-class of application? Because it can make developing the SQLlite database easier.
 * Remember to specify in the AndroidManifest that this class is the application class.
 */

public final class HGPSApplication extends Application
{
    private static HGPSApplication  instance;
    public HGPSApplication()
    {
        instance = this;
    }
    public static Context getContext()
    {
        return instance;
    }

    /**
     * Takes an instance of the activity you want to switch to, and the instance of the activity you are swithcing from.
     * @param newIntent
     * @param oldIntent
     */
    public static void activityIntentSwitch(Activity newIntent, Activity oldIntent) // Instead of duplicating same three lines everywhere, just call this.
    {
        Intent mainIntent = new Intent(getContext(), newIntent.getClass());
        oldIntent.startActivity(mainIntent);
        oldIntent.finish();
    }

    public static void activityIntentSwitch(Activity newIntent, Activity oldIntent, int anim1, int anim2)
    {
        Intent mainIntent = new Intent(getContext(), newIntent.getClass());
        mainIntent.setFlags(mainIntent.FLAG_ACTIVITY_NO_ANIMATION);
        oldIntent.startActivity(mainIntent);
        oldIntent.overridePendingTransition(anim1, anim2);
        oldIntent.finish();
    }
}
