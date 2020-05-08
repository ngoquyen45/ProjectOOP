package android.support.v7.app;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Thanh on 2/24/2015.
 * Put in same package to access protected variable
 */
public class HackedDrawerArrowDrawableToggle extends android.support.v7.app.ActionBarDrawerToggle.DrawerArrowDrawableToggle {

    public HackedDrawerArrowDrawableToggle(Activity activity, Context themedContext) {
        super(activity, themedContext);
    }

}
