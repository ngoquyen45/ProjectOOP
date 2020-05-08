package android.support.v7.app;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

/**
 * Created by Thanh on 2/24/2015.
 * Put in same package to access protected variable
 */
public class HackedActionBarDrawerToggle extends ActionBarDrawerToggle {

    public HackedActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
    }

    public HackedActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
    }

    public HackedActionBarDrawerToggle(Activity activity, Toolbar toolbar, DrawerLayout drawerLayout, HackedDrawerArrowDrawableToggle slider, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, toolbar, drawerLayout, slider, openDrawerContentDescRes, closeDrawerContentDescRes);
    }

}
