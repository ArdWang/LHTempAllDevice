package com.wc.lhtempalldevice.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rnd on 2017/5/9.
 */

public class ActivityCollector {
    /**
     * 其它页面的activity
     */
    public static List<Activity> activities = new ArrayList<Activity>();
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 主界面的activity
     */
    public static List<Activity> mainactivit = new ArrayList<>();

    public static void addMainActivity(Activity activity){mainactivit.add(activity);}

    public static void removeMainActivity(Activity activity){
        mainactivit.remove(activity);
    }

    public static  void finisMainAll(){
        for(Activity activity:mainactivit){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
