package com.viettel.dms.helper.layout;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.viettel.dms.R;

/**
 * Created by PHAMHUNG on 2/1/2016.
 */
public class ChooseThemeAdapter extends BaseAdapter {
    private Context context;
    ThemeItem[] data;

    public ChooseThemeAdapter(Context ctx, ThemeItem[] dt) {
        this.context = ctx;
        data = dt;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public ThemeItem getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            rowView = View.inflate(context, R.layout.adapter_choose_theme, null);

            ThemeViewHolder viewHolder = new ThemeViewHolder();
            viewHolder.tvThemeName = (TextView) rowView.findViewById(R.id.tv_Theme_Name);
            viewHolder.tvColorPrimary = (TextView) rowView.findViewById(R.id.tv_Color_Primary);
            viewHolder.tvColorSecondary = (TextView) rowView.findViewById(R.id.tv_Color_Secondary);
            rowView.setTag(viewHolder);
        }
        ThemeViewHolder vh = (ThemeViewHolder) rowView.getTag();
        ThemeItem item = data[position];
        SetTextUtils.setText(vh.tvThemeName, item.name);
        vh.tvColorPrimary.setBackgroundColor(ContextCompat.getColor(context, item.colorPrimary));
        vh.tvColorSecondary.setBackgroundColor(ContextCompat.getColor(context, item.colorSecondary));

        return rowView;
    }

    class ThemeViewHolder {
        TextView tvThemeName;
        TextView tvColorPrimary;
        TextView tvColorSecondary;

        public ThemeViewHolder() {

        }
    }

    public static class ThemeItem {
        int theme;
        String name;
        int colorPrimary;
        int colorSecondary;

        public ThemeItem(int theme, String name, @ColorRes int colorPrimary, @ColorRes int colorSecondary) {
            this.theme = theme;
            this.name = name;
            this.colorPrimary = colorPrimary;
            this.colorSecondary = colorSecondary;
        }

        public int getTheme() {
            return theme;
        }

        public void setTheme(int theme) {
            this.theme = theme;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getColorPrimary() {
            return colorPrimary;
        }

        public void setColorPrimary(int colorPrimary) {
            this.colorPrimary = colorPrimary;
        }

        public int getColorSecondary() {
            return colorSecondary;
        }

        public void setColorSecondary(int colorSecondary) {
            this.colorSecondary = colorSecondary;
        }
    }
}
