package com.viettel.dms.helper.layout;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by PHAMHUNG on 4/24/2015.
 * follow : http://stackoverflow.com/questions/14123243/google-maps-android-api-v2-interactive-infowindow-like-in-original-android-go/15040761#15040761
 */

public abstract class OnInfoWindowElemTouchListener implements View.OnTouchListener {
    private final View view;
    private final Handler handler = new Handler();

    private Marker marker;
    private boolean pressed = false;

    public OnInfoWindowElemTouchListener(View view){
        this.view = view;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    @Override
    public boolean onTouch(View vv, MotionEvent event) {
        if (0 <= event.getX() && event.getX() <= view.getWidth() &&
                0 <= event.getY() && event.getY() <= view.getHeight()) {

            switch (event.getActionMasked()) {

                case MotionEvent.ACTION_DOWN:
                    Log.e("TATA", "DOWN");
                    startPress();
                    break;

                // We need to delay releasing of the view a little so it shows the pressed state on the screen
                case MotionEvent.ACTION_UP:
                    Log.e("TATA", "UP");
                    handler.postDelayed(confirmClickRunnable, 150);
                    break;

                case MotionEvent.ACTION_CANCEL:
                    Log.e("TATA", "CANCEL");
                    endPress();
                    break;
                default:
                    break;
            }
        } else {
            // If the touch goes outside of the view's area
            // (like when moving finger out of the pressed button)
            // just release the press
            endPress();
        }
        return false;
    }

    private void startPress() {
        if (!pressed) {
            pressed = true;
            handler.removeCallbacks(confirmClickRunnable);
            // view.setBackground(bgDrawablePressed);
//            if (marker != null)
//                marker.showInfoWindow();
        }
    }

    private boolean endPress() {
        if (pressed) {
            this.pressed = false;
            handler.removeCallbacks(confirmClickRunnable);
            // view.setBackground(bgDrawableNormal);
//            if (marker != null)
//                marker.showInfoWindow();
            return true;
        } else
            return false;
    }

    private final Runnable confirmClickRunnable = new Runnable() {
        public void run() {
            if (endPress()) {
                onClickConfirmed(view, marker);
            }
        }
    };

    /**
     * This is called after a successful click
     */
    protected abstract void onClickConfirmed(View v, Marker marker);
}
