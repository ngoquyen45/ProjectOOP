package com.viettel.dms.helper.layout;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.viettel.dms.R;


/**
 * Created by Thanh on 3/19/2015.
 */
public class CustomSearchView extends SearchView {

    SearchAutoComplete mSearchSrcTextView;

    public CustomSearchView(Context context) {
        super(context, null);
        setSearchIcons();
    }

    public CustomSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSearchIcons();
    }

    public CustomSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSearchIcons();
    }
    private void setSearchIcons() {
        int iconsTintColor = getContext().getResources().getColor(R.color.Black54);

        ImageView mag2Button = (ImageView) findViewById(R.id.search_mag_icon2);
        mag2Button.setColorFilter(iconsTintColor);

        ImageView closeButton = (ImageView) findViewById(R.id.search_close_btn);
        closeButton.setColorFilter(iconsTintColor);

        ImageView goButton = (ImageView) findViewById(R.id.search_go_btn);
        goButton.setColorFilter(iconsTintColor);

        ImageView voiceButton = (ImageView) findViewById(R.id.search_voice_btn);
        voiceButton.setColorFilter(iconsTintColor);

        this.mSearchSrcTextView = (SearchAutoComplete) findViewById(R.id.search_src_text);
        updateQueryHint(getQueryHint());


    }

    @Override
    public void setQueryHint(CharSequence hint) {
        super.setQueryHint(hint);
        updateQueryHint(hint);
    }

    private void updateQueryHint(CharSequence hint) {
        if (mSearchSrcTextView == null) {
            return;
        }
        if (hint != null) {
            mSearchSrcTextView.setHint(hint);
        } else {
            mSearchSrcTextView.setHint("");
        }
    }
}
