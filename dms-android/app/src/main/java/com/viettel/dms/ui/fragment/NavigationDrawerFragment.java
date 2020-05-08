package com.viettel.dms.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.HackedActionBarDrawerToggle;
import android.support.v7.app.HackedDrawerArrowDrawableToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.viettel.dms.R;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.helper.layout.ThemeUtils;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dms.ui.activity.MainActivity;
import com.viettel.dmsplus.sdk.auth.OAuthSession;
import com.viettel.dmsplus.sdk.models.UserInfo;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NavigationDrawerFragment extends Fragment implements FragmentManager.OnBackStackChangedListener {

    /**
     * Per the design guidelines, you should show the drawer on launch until the
     * user manually expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    @Bind(R.id.rv_navigation_drawer)
    RecyclerView rvDrawer;

    private Context context;

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;

    private DrawerItem mCurrentSelectedPosition = HardCodeUtil.NavigationDrawer.drawerMenu[1];
    private boolean mSelectFirstTime = true;

    private NavDrawerAdapter mNavDrawerAdapter;
    private LayoutInflater inflater;
    private LinearLayoutManager mLayoutManager;

    private boolean mUserLearnedDrawer;

    int backgroundColorFocus, backgroundColor, textItemColorFocus, textItemColor;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        this.inflater = getActivity().getLayoutInflater();
        this.mLayoutManager = new LinearLayoutManager(context);
        this.mNavDrawerAdapter = new NavDrawerAdapter();

        // Read in the flag indicating whether or not the user has demonstrated
        // awareness of the drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        this.textItemColor = ContextCompat.getColor(context, R.color.Black54);
        this.textItemColorFocus = ThemeUtils.getColor(context,R.attr.colorPrimary);
        this.backgroundColor = ContextCompat.getColor(context, R.color.Grey98);
        this.backgroundColorFocus =ContextCompat.getColor(context, R.color.Grey89);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        ButterKnife.bind(this, view);

        rvDrawer.setLayoutManager(mLayoutManager);
        rvDrawer.setAdapter(mNavDrawerAdapter);

        selectItem(mCurrentSelectedPosition);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ButterKnife.unbind(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of
        // actions in the action bar.
        setHasOptionsMenu(true);
    }

    /**
     * Users of this fragment must call this method to set up the navigation
     * drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout, HackedDrawerArrowDrawableToggle arrow) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new StackSupportActionBarDrawerToggle(getActivity(), null, mDrawerLayout, arrow,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActivity().getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallbacks = (NavigationDrawerCallbacks) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    "Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {
        mDrawerToggle.syncState();
    }

    private ActionBar getActionBar() {
        if(getActivity() == null) return  null;
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null
                && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void toogle() {
        if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    /**
     * Callbacks interface that all activities using this fragment must
     * implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(DrawerItem item);
    }

    private void selectItem(DrawerItem item) {
        if (mSelectFirstTime || !mCurrentSelectedPosition.equals(item)) {
            mSelectFirstTime = false;
            mCurrentSelectedPosition = item;
            if (rvDrawer != null) {
                mNavDrawerAdapter.notifyDataSetChanged();
                rvDrawer.setAdapter(mNavDrawerAdapter);
            }

            if (mCallbacks != null) {
                mCallbacks.onNavigationDrawerItemSelected(mCurrentSelectedPosition);
            }
        }

        if (isDrawerOpen()) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    public class StackSupportActionBarDrawerToggle extends HackedActionBarDrawerToggle {

        private final MainActivity mainActivity;
        private final HackedDrawerArrowDrawableToggle slider;

        private final int mOpenDrawerContentDescRes;
        private final int mCloseDrawerContentDescRes;

        public StackSupportActionBarDrawerToggle(Activity activity, Toolbar toolbar,
                                                 DrawerLayout drawerLayout, HackedDrawerArrowDrawableToggle slider,
                                                 int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, toolbar, drawerLayout, slider, openDrawerContentDescRes,
                    closeDrawerContentDescRes);
            this.mainActivity = (MainActivity) activity;
            this.slider = slider;
            this.mOpenDrawerContentDescRes = openDrawerContentDescRes;
            this.mCloseDrawerContentDescRes = closeDrawerContentDescRes;
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
            if (!isAdded()) {
                return;
            }

            // calls onPrepareOptionsMenu()
            getActivity().supportInvalidateOptionsMenu();
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            if (!isAdded()) {
                return;
            }

            if (!mUserLearnedDrawer) {
                // The user manually opened the drawer; store this flag to
                // prevent auto-showing the navigation drawer automatically in the future.
                mUserLearnedDrawer = true;
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
            }

            // calls onPrepareOptionsMenu()
            getActivity().supportInvalidateOptionsMenu();
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == android.R.id.home) {
                if (mainActivity.backStackCount() == 0) {
                    toogle();
                    ((BaseActivity) getActivity()).closeSoftKey();
                    return true;
                }
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void syncState() {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START) || mainActivity.backStackCount() > 0) {
                slider.setPosition(1);
            } else {
                slider.setPosition(0);
            }
            if (isDrawerIndicatorEnabled()) {
                setActionBarUpIndicator(slider,
                        mDrawerLayout.isDrawerOpen(GravityCompat.START) ?
                                mCloseDrawerContentDescRes : mOpenDrawerContentDescRes);
            }
        }

        public void setActionBarUpIndicator(Drawable drawable, int contentDescRes) {
            if(getActionBar() !=null) {
                getActionBar().setHomeAsUpIndicator(drawable);
                getActionBar().setHomeActionContentDescription(contentDescRes);
            }
        }
    }

    public static class DrawerItem {
        private String resIconStr;
        private int resTextId;
        private int type;
        private int index;

        public DrawerItem(String resIdStr, int resTextId, int type, int index) {
            this.resIconStr = resIdStr;
            this.resTextId = resTextId;
            this.type = type;
            this.index = index;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DrawerItem that = (DrawerItem) o;

            if (resTextId != that.resTextId) {
                return false;
            }
            if (type != that.type) {
                return false;
            }
            if (index != that.index) {
                return false;
            }
            return !(resIconStr != null ? !resIconStr.equals(that.resIconStr) : that.resIconStr != null);

        }

        @Override
        public int hashCode() {
            int result = resIconStr != null ? resIconStr.hashCode() : 0;
            result = 31 * result + resTextId;
            result = 31 * result + type;
            result = 31 * result + index;
            return result;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    // 3 types of Nav Item
    class NavDrawerHeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imgLogo)
        ImageView imgLogo;
        @Bind(R.id.tvClientName)
        TextView tvClientName;
        @Bind(R.id.tvFullname)
        TextView tvFullname;

        public NavDrawerHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class NavDrawerSubHeaderViewHoder extends RecyclerView.ViewHolder {
        @Bind(R.id.tvSubHeader)
        TextView tvSubHeader;

        public NavDrawerSubHeaderViewHoder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class NavDrawerItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.itvItem)
        IconTextView itvIcon;
        @Bind(R.id.tvItem)
        TextView tvItem;

        public NavDrawerItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // Adapter
    class NavDrawerAdapter extends RecyclerView.Adapter {
        DrawerItem[] menu = HardCodeUtil.NavigationDrawer.drawerMenu;
        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object o = view.getTag();
                if (o instanceof DrawerItem) {
                    selectItem((DrawerItem) o);
                }
            }
        };

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case HardCodeUtil.NavigationDrawer.DRAWER_HEADER: {
                    View view = inflater.inflate(R.layout.adapter_nav_drawer_header, parent, false);
                    return new NavDrawerHeaderViewHolder(view);
                }
                case HardCodeUtil.NavigationDrawer.DRAWER_SUBHEADER: {
                    View view = inflater.inflate(R.layout.adapter_nav_drawer_subheader, parent, false);
                    return new NavDrawerSubHeaderViewHoder(view);
                }
                case HardCodeUtil.NavigationDrawer.DRAWER_ITEM: {
                    View view = inflater.inflate(R.layout.adapter_nav_drawer_item, parent, false);
                    return new NavDrawerItemViewHolder(view);
                }
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int viewType = holder.getItemViewType();
            DrawerItem item = menu[position];
            if (viewType == HardCodeUtil.NavigationDrawer.DRAWER_HEADER) {
                NavDrawerHeaderViewHolder holderTo = (NavDrawerHeaderViewHolder) holder;
                Bitmap avatarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_logo_default);
                RoundedBitmapDrawable avatar = RoundedBitmapDrawableFactory.create(getResources(), avatarBitmap);
                avatar.setCornerRadius(Math.max(avatarBitmap.getWidth(), avatarBitmap.getHeight()) / 2);
                avatar.setFilterBitmap(true);
                avatar.setAntiAlias(true);
                holderTo.imgLogo.setImageDrawable(avatar);
                UserInfo userInfo = OAuthSession.getDefaultSession() != null ? OAuthSession.getDefaultSession().getUserInfo() : null;
                if (userInfo != null) {
                    SetTextUtils.setText(holderTo.tvFullname,userInfo.getFullname());
                    SetTextUtils.setText(holderTo.tvClientName, userInfo.getClientName());
                }
            }
            if (viewType == HardCodeUtil.NavigationDrawer.DRAWER_SUBHEADER) {
                NavDrawerSubHeaderViewHoder viewHoder = (NavDrawerSubHeaderViewHoder) holder;
                viewHoder.tvSubHeader.setText(item.resTextId);
            }
            if (viewType == HardCodeUtil.NavigationDrawer.DRAWER_ITEM) {
                NavDrawerItemViewHolder viewHolder = (NavDrawerItemViewHolder) holder;
                viewHolder.itvIcon.setText(item.resIconStr);
                viewHolder.tvItem.setText(item.resTextId);
                viewHolder.itemView.setTag(item);
                viewHolder.itemView.setOnClickListener(onClickListener);
                if (mCurrentSelectedPosition == item) {
                    viewHolder.tvItem.setTextColor(textItemColorFocus);
                    viewHolder.itvIcon.setTextColor(textItemColorFocus);
                    viewHolder.itemView.setBackgroundColor(backgroundColorFocus);
                } else {
                    viewHolder.tvItem.setTextColor(textItemColor);
                    viewHolder.itvIcon.setTextColor(textItemColor);
                    viewHolder.itemView.setBackgroundColor(backgroundColor);
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            return menu[position].type;
        }

        @Override
        public int getItemCount() {
            return menu.length;
        }
    }
}
