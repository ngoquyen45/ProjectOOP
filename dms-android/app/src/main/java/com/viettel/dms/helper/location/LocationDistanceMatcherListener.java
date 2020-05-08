package com.viettel.dms.helper.location;

import android.location.Location;

/**
 * Created by Thanh on 3/10/2015.
 */
public interface LocationDistanceMatcherListener {

    void onLocationMatchSuccess(Location location);

    void onLocationMatchFail(Location location);

    void onTurnOffLocation();
}
