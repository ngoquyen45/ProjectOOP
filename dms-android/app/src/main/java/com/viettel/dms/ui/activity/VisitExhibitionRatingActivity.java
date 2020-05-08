package com.viettel.dms.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.viettel.dms.R;
import com.viettel.dms.ui.fragment.VisitExhibitonRatingListFragment;
import com.viettel.dmsplus.sdk.models.ExhibitionDto;
import com.viettel.dmsplus.sdk.models.ExhibitionRatingDto;

public class VisitExhibitionRatingActivity extends BaseActivity {
    boolean visited;
    String visitId;
    String  customerId;
    ExhibitionRatingDto[] changeRate;
    ExhibitionDto[] exhibitionDtos;

    private Toolbar toolbar;

    public static String PARAM_CUSTOMER_INFO = "customerInfo";
    public static String PARAM_VISIT = "visited";
    public static String PARAM_RATED_INFO = "ratedInfo";
    public static String PARAM_RATING_INFO = "ratingInfo";
    public static String PARAM_VISIT_ID = "visitID";
    public static String PARAM_VISITED_DTO = "PARAM_VISITED_DTO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        customerId = args.getString(PARAM_CUSTOMER_INFO);
        visited = args.getBoolean(PARAM_VISIT);
        visitId = args.getString(PARAM_VISIT_ID);

        if (visited) {
            Parcelable[] parcelables = args.getParcelableArray(PARAM_RATED_INFO);
            if (parcelables != null) {
                changeRate = new ExhibitionRatingDto[parcelables.length];
                int i = 0;
                for (Parcelable rs : parcelables) {
                    changeRate[i++] = (ExhibitionRatingDto) rs;
                }
            }

            Parcelable[] dtos = args.getParcelableArray(PARAM_VISITED_DTO);
            if (dtos != null) {
                exhibitionDtos = new ExhibitionDto[dtos.length];
                int j = 0;
                for (Parcelable dto : dtos) {
                    exhibitionDtos[j++] = (ExhibitionDto) dto;
                }
            }
        } else {
            Parcelable[] parcelables = args.getParcelableArray(PARAM_RATING_INFO);
            if (parcelables != null) {
                changeRate = new ExhibitionRatingDto[parcelables.length];
                int i = 0;
                for (Parcelable rs : parcelables) {
                    changeRate[i++] = (ExhibitionRatingDto) rs;
                }
            }
        }

        setContentView(R.layout.activity_visit_exhibition_rating);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        replaceCurrentFragment(VisitExhibitonRatingListFragment.newInstance(visited, customerId, changeRate, exhibitionDtos, visitId), false, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    public void finishWithChangeRating(ExhibitionRatingDto[] rates) {
        Intent returnIntent = new Intent();
        Bundle b = new Bundle();
        b.putParcelableArray(PARAM_RATED_INFO, rates);
        returnIntent.putExtras(b);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
