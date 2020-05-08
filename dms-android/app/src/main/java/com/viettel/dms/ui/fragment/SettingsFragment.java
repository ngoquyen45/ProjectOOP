package com.viettel.dms.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.viettel.dms.DMSApplication;
import com.viettel.dms.R;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.helper.share.DMSSharePreference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsFragment extends BaseFragment {
    @Bind(R.id.tvLanguage)
    TextView tvLanguage;
    @Bind(R.id.tvMap)
    TextView tvMap;
    String defaultLanguage = "en";
    int defaultMap = 0;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        setTitleResource(R.string.navigation_drawer_item_settings);
        defaultLanguage = DMSSharePreference.get().getDefaultLanguage();
        defaultMap = DMSSharePreference.get().getDedaultMapType();
        String[] arrayCode = getResources().getStringArray(R.array.settings_change_language_array_code);
        String[] arrayLanguage = getResources().getStringArray(R.array.settings_change_language_array);
        for (int i = 0; i < arrayCode.length; i++) {
            if (defaultLanguage.equalsIgnoreCase(arrayCode[i])) {
                tvLanguage.setText(arrayLanguage[i]);
                break;
            }
        }
        String[] arrayMap = getResources().getStringArray(R.array.settings_change_map_type);
        tvMap.setText(arrayMap[defaultMap]);
        return view;
    }

    @OnClick(R.id.ll_Change_Theme)
    void changeTheme() {
        DialogUtils.showChooseThemeDialog(context, R.string.settings_change_themes).show();
    }

    @OnClick(R.id.ll_Change_Language)
    void changeLanguage() {
        DialogUtils.showChangeSettingDialog(context, R.string.settings_change_language, R.array.settings_change_language_array, new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                defaultLanguage = DMSSharePreference.get().getDefaultLanguage();
                String[] arrayCode = getResources().getStringArray(R.array.settings_change_language_array_code);
                if (!defaultLanguage.equalsIgnoreCase(arrayCode[i])) {
                    DMSSharePreference.get().putDefaultLanguage(arrayCode[i]);
                    ((DMSApplication) getActivity().getApplication()).changeLang(arrayCode[i]);
                    Activity activity = getActivity();
                    activity.finish();
                    activity.startActivity(new Intent(activity, activity.getClass()));
                }
            }
        });
    }

    @OnClick(R.id.ll_Change_Map)
    void changeMapType() {
        DialogUtils.showChangeSettingDialog(context, R.string.settings_change_map, R.array.settings_change_map_type, new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                DMSSharePreference.get().putDedaultMapType(i);
                String[] arrayName = getResources().getStringArray(R.array.settings_change_map_type);
                SetTextUtils.setText(tvMap, arrayName[i]);
            }
        });
    }

}
