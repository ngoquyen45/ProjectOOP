package com.viettel.dms.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.viettel.dms.R;
import com.viettel.dms.helper.layout.ThemeUtils;
import com.viettel.dms.ui.fragment.BaseFragment;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String LOGTAG = BaseActivity.class.getName();

    @IdRes
    private int contentViewId = R.id.content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ThemeUtils.getColor(this, R.attr.colorPrimaryDark));
        }

        // Check kind of devices and request proper orientation
        if (getResources().getBoolean(R.bool.is_tablet)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Using this method to set ActionBar title with customized Typeface
     */
    public void setActionBarTitle(CharSequence title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (backStackCount() > 0) {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getCurrentFragment();
        if (fragment != null
                && fragment instanceof BaseFragment) {
            // If handled in fragment then prevent default action
            if (((BaseFragment) fragment).onBackPressed()) {
                return;
            }
        }
        super.onBackPressed();
    }

    public void closeSoftKey() {
        View v = getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public void popFragmentBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();

        if (count > 0) {
            fm.popBackStack(fm.getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public int backStackCount() {
        return getSupportFragmentManager().getBackStackEntryCount();
    }

    /**
     * Adjust title or icon if needed when switching fragment
     */
    public void adjustWhenFragmentChanged(BaseFragment fragment) {
        if (fragment.hasTitle()) {
            setActionBarTitle(fragment.getTitle());
        } else {
            setActionBarTitle(null);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.dialog_enter, R.anim.dialog_exit);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.dialog_enter, R.anim.dialog_exit);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    protected void replaceCurrentFragment(Fragment fragment,
                                          boolean addToBackStack, boolean slideToLeft) {
        closeSoftKey();
        try {
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            if (slideToLeft) {
                transaction.setCustomAnimations(
                        R.anim.anim_slide_right_to_left,
                        R.anim.anim_slide_right_to_left_out,
                        R.anim.anim_slide_left_to_right,
                        R.anim.anim_slide_left_to_right_out);
            }
            transaction.replace(R.id.content, fragment);
            if (addToBackStack) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
        } catch (Exception ex) {
            Log.e(LOGTAG, "Error while replace frament", ex);
        }
    }

    /**
     * Any call to replace content fragment must be add to back stack except
     * root fragments
     */
    public void replaceCurrentFragment(Fragment fragment) {
        replaceCurrentFragment(fragment, true, true);
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(
                R.id.content);
    }

    public void clearFragmentBackStack() {
        FragmentManager fm = this.getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    public void removeFragment(Fragment f) {
        if (f != null) {
            this.getSupportFragmentManager().beginTransaction().remove(f).commit();
        }
    }

    public int getContentViewId() {
        return contentViewId;
    }

    public void setContentViewId(int contentViewId) {
        this.contentViewId = contentViewId;
    }

    public int getScreenHeight() {
        return findViewById(R.id.content).getHeight();
    }
}
