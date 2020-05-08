package com.viettel.dms.helper;

import android.content.Context;

import com.viettel.dms.R;
import com.viettel.dms.helper.layout.ChooseThemeAdapter;
import com.viettel.dms.helper.layout.ThemeUtils;
import com.viettel.dms.ui.fragment.NavigationDrawerFragment;

/**
 * @author PHAMHUNG
 * @since 8/13/2015
 */
public class HardCodeUtil {
    public static final int SPLASH_DISPLAY_LENGTH = 1000;
    public static final int NETWORK_TIME_OUT = 20000;
    public static final int IMAGE_QUALITY = 60;

    public interface DeliveryType {
        int IMMEDIATELY = 0;
        int SAME_DAY = 1;
        int ANOTHER_DAY = 2;
    }


    public interface NavigationDrawer {
        int DRAWER_HEADER = 0;
        int DRAWER_SUBHEADER = 1;
        int DRAWER_ITEM = 2;

        int DRAWER_ITEM_RESULT_SALE = 1;
        int DRAWER_ITEM_VISIT = 2;
        int DRAWER_ITEM_TAKE_ORDER = 3;
        int DRAWER_ITEM_RETURN = 4;
        int DRAWER_ITEM_EXCHANGE = 5;
        int DRAWER_ITEM_PROMOTION_PROGRAM = 6;
        int DRAWER_ITEM_LIST_PRODUCT = 7;
        int DRAWER_ITEM_REGISTER_CUSTOMER = 8;
        int DRAWER_ITEM_CHANGE_PASS = 9;
        int DRAWER_ITEM_THEME = 10;
        int DRAWER_ITEM_EXIT = 11;

        NavigationDrawerFragment.DrawerItem[] drawerMenu = new NavigationDrawerFragment.DrawerItem[]{
                new NavigationDrawerFragment.DrawerItem("", 0, DRAWER_HEADER, 0),
                new NavigationDrawerFragment.DrawerItem("{md-dashboard 24dp}", R.string.navigation_drawer_item_result_sale, DRAWER_ITEM, DRAWER_ITEM_RESULT_SALE),
                new NavigationDrawerFragment.DrawerItem("{md-group 24dp}", R.string.navigation_drawer_item_visit, DRAWER_ITEM, DRAWER_ITEM_VISIT),
                new NavigationDrawerFragment.DrawerItem("{md-assignment 24dp}", R.string.navigation_drawer_item_take_order, DRAWER_ITEM, DRAWER_ITEM_TAKE_ORDER),
                new NavigationDrawerFragment.DrawerItem("{md-find-replace 24dp}", R.string.navigation_drawer_item_exchange, DRAWER_ITEM, DRAWER_ITEM_EXCHANGE),
                new NavigationDrawerFragment.DrawerItem("{md-local-shipping 24dp}", R.string.navigation_drawer_item_return, DRAWER_ITEM, DRAWER_ITEM_RETURN),
                new NavigationDrawerFragment.DrawerItem("", R.string.navigation_drawer_header_support_sale, DRAWER_SUBHEADER, 0),
                new NavigationDrawerFragment.DrawerItem("{md-local-offer 24dp }", R.string.navigation_drawer_item_promotion_program, DRAWER_ITEM, DRAWER_ITEM_PROMOTION_PROGRAM),
                new NavigationDrawerFragment.DrawerItem("{md-widgets 24dp}", R.string.navigation_drawer_item_list_product, DRAWER_ITEM, DRAWER_ITEM_LIST_PRODUCT),
                new NavigationDrawerFragment.DrawerItem("{md-person-add 24dp}", R.string.navigation_drawer_item_new_customer, DRAWER_ITEM, DRAWER_ITEM_REGISTER_CUSTOMER),
                new NavigationDrawerFragment.DrawerItem("", R.string.navigation_drawer_header_system, DRAWER_SUBHEADER, 0),
                new NavigationDrawerFragment.DrawerItem("{md-lock-outline 24dp}", R.string.navigation_drawer_item_change_pass, DRAWER_ITEM, DRAWER_ITEM_CHANGE_PASS),
                new NavigationDrawerFragment.DrawerItem("{md-settings 24dp}", R.string.navigation_drawer_item_settings, DRAWER_ITEM, DRAWER_ITEM_THEME),
                new NavigationDrawerFragment.DrawerItem("{md-exit-to-app 24dp}", R.string.navigation_drawer_item_exit, DRAWER_ITEM, DRAWER_ITEM_EXIT),
        };
    }

    public interface Location {
        double defaultLast = 21.029151466759075;
        double defaultLong = 105.8417272567749;
        float defaultZoomLevel = 14.0f;
        float defaultZoomLevelLocated = 20.0f;
    }

    private static int firstLetter[] = {
            R.color.first_letter_0, R.color.first_letter_1, R.color.first_letter_2,
            R.color.first_letter_3, R.color.first_letter_4, R.color.first_letter_5,
            R.color.first_letter_6, R.color.first_letter_7, R.color.first_letter_8,
            R.color.first_letter_9, R.color.first_letter_A, R.color.first_letter_B,
            R.color.first_letter_C, R.color.first_letter_D, R.color.first_letter_E,
            R.color.first_letter_F, R.color.first_letter_G, R.color.first_letter_H,
            R.color.first_letter_I, R.color.first_letter_J, R.color.first_letter_K,
            R.color.first_letter_L, R.color.first_letter_M, R.color.first_letter_N,
            R.color.first_letter_O, R.color.first_letter_P, R.color.first_letter_Q,
            R.color.first_letter_R, R.color.first_letter_S, R.color.first_letter_T,
            R.color.first_letter_U, R.color.first_letter_V, R.color.first_letter_W,
            R.color.first_letter_X, R.color.first_letter_Y, R.color.first_letter_Z};

    public static int getResourceIdColor(char c) {
        try {
            if (c < 48) return firstLetter[0];
            else if (c < 58) return firstLetter[c - 48];
            else if (c < 65) return firstLetter[1];
            else if (c < 91) return firstLetter[c - 65];
            else if (c < 97) return firstLetter[2];
            else if (c < 123) return firstLetter[c - 97];
            else return firstLetter[3];
        } catch (Exception ex) {
            return firstLetter[4];
        }
    }

    public interface CustomerVisit {
        float MAX_ALLOWED_DISTANCE = 300;
        long MAX_ALLOWED_TIME = 60 * 1000;
        float MAX_ALLOWED_DISTANCE_DEBUG = 300;
        long MAX_ALLOWED_TIME_DEBUG = 5 * 1000;
        String STATE_FILE_NAME = "VisitingCustomer";
        String ERROR_VISIT_WAS_ENDED = "visit.was.ended";
    }

    public interface BroadcastReceiver {
        String ACTION_CUSTOMER_VISIT_STATUS_CHANGED = "com.viettel.dms.ACTION_CUSTOMER_VISIT_STATUS_CHANGED";
        String ACTION_CUSTOMER_VISIT_SURVEY_ADDED = "com.viettel.dms.ACTION_CUSTOMER_VISIT_SURVEY_ADDED";
        String ACTION_UNPLANT_ORDER_ADDED = "com.viettel.dms.ACTION_UNPLANT_ORDER_ADDED";
        String ACTION_UPDATE_MOBILE = "com.viette.dms.ACTION_UPDATE_MOBILE";
        String ACTION_UPDATE_LOCATION = "com.viette.dms.ACTION_UPDATE_LOCATION";
        String ACTION_CUSTOMER_VISIT_RATING_ADDED = "com.viettel.dms.ACTION_CUSTOMER_VISIT_RATING_ADDED";
        String ACTION_CUSTOMER_NEW_CUSTOMER_ADD = "com.viettel.dms.ACTION_CUSTOMER_NEW_CUSTOMER_ADD";
        String ACTION_STORE_CHECKER_CHECK = "com.viettel.dms.ACTION_STORE_CHECKER_CHECK";
        String ACTION_EXCHANGE_RETURN_PRODUCT = "com.viettel.dms.ACTION_EXCHANGE_RETURN_PRODUCT";
    }

    public interface OrderStatus {
        int WAITING = 0;
        int ACCEPTED = 1;
        int REJECTED = 2;
    }

    public interface CustomerVisitStatus {
        int VISITING = 0;
        int NOT_VISITED = 1;
        int VISITED = 2;
    }

    public interface RegisterCustomer {
        int PENDING = 0;
        int ACCEPT = 1;
        int REJECT = 2;
    }

    public interface SurveyStatus {
        int NOT_COMPLETED = 0;
        int COMPLETED = 1;
        int COMPLETING = 2;
    }

    public static String getVisitStatus(Context ctx, int status) {
        int resId = 0;
        switch (status) {
            case CustomerVisitStatus.VISITING:
                resId = R.string.status_visit_visiting;
                break;
            case CustomerVisitStatus.NOT_VISITED:
                resId = R.string.status_visit_not_visited;
                break;
            case CustomerVisitStatus.VISITED:
                resId = R.string.status_visit_visited;
                break;
        }
        if (resId == 0) {
            return "";
        }
        return ctx.getString(resId);
    }

    public static String getOrderStatus(Context ctx, int status) {
        int resId = 0;
        switch (status) {
            case OrderStatus.WAITING:
                resId = R.string.status_order_waiting;
                break;
            case OrderStatus.ACCEPTED:
                resId = R.string.status_order_accepted;
                break;
            case OrderStatus.REJECTED:
                resId = R.string.status_order_rejected;
                break;
        }
        if (resId == 0) {
            return "";
        }
        return ctx.getString(resId);
    }

    public static String getSurveyStatus(Context ctx, int status) {
        int resId = 0;
        switch (status) {
            case SurveyStatus.NOT_COMPLETED:
                resId = R.string.status_survey_not_completed;
                break;
            case SurveyStatus.COMPLETED:
                resId = R.string.status_survey_completed;
                break;
            case SurveyStatus.COMPLETING:
                resId = R.string.status_survey_completing;
                break;
        }
        if (resId == 0) {
            return "";
        }
        return ctx.getString(resId);
    }

    public static ChooseThemeAdapter.ThemeItem[] THEMES_APP = new ChooseThemeAdapter.ThemeItem[]{
            new ChooseThemeAdapter.ThemeItem(ThemeUtils.THEME_DEFAULT, "Indigo/ Pink", R.color.colorPrimary1, R.color.colorSecondary1),
            new ChooseThemeAdapter.ThemeItem(ThemeUtils.THEME_2, "Deep Purple/ Cyan", R.color.colorPrimary2, R.color.colorSecondary2),
            new ChooseThemeAdapter.ThemeItem(ThemeUtils.THEME_3, "Purple/ Green", R.color.colorPrimary3, R.color.colorSecondary3),
            new ChooseThemeAdapter.ThemeItem(ThemeUtils.THEME_4, "Blue Grey/ Red", R.color.colorPrimary4, R.color.colorSecondary4),
            new ChooseThemeAdapter.ThemeItem(ThemeUtils.THEME_5, "Red/ Light Blue", R.color.colorPrimary5, R.color.colorSecondary5),
    };

}
