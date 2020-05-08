package com.viettel.dms.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.viettel.dms.R;
import com.viettel.dms.helper.layout.LayoutUtils;
import com.viettel.dms.ui.fragment.BaseFragment;
import com.viettel.dms.ui.fragment.VisitSurveyListFragment;
import com.viettel.dmsplus.sdk.models.SurveyAnswerDto;
import com.viettel.dmsplus.sdk.models.SurveyListResult;

public class VisitSurveyActivity extends BaseActivity {
    public static String PARAM_CUSTOMER_ID = "customerID";
    public static String PARAM_CUSTOMER_NAME = "customerNAME";
    public static String PARAM_VISIT_ID = "visitId";
    public static String PARAM_SURVEY_ANSWERS = "surveyAnswers";
    public static String PARAM_SURVEY_QUESTION = "surveyQuestion";
    public static String PARAM_VISIT_ENDED = "visitEnded";


    private Toolbar toolbar;

    private String visitId;
    private String customerId;
    private String customerName;
    private SurveyListResult.SurveyDto[] surveyDtos;
    private SurveyAnswerDto[] surveyAnswers;
    private boolean visitEnded;
    private int defaultToolbarPadding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_survey);
        Bundle args = this.getIntent().getExtras();
        if (args != null) {
            visitEnded = args.getBoolean(PARAM_VISIT_ENDED, false);
            visitId = args.getString(PARAM_VISIT_ID);
            customerId = args.getString(PARAM_CUSTOMER_ID);
            customerName = args.getString(PARAM_CUSTOMER_NAME);

            Parcelable[] parcelables = args.getParcelableArray(PARAM_SURVEY_ANSWERS);
            if (parcelables != null) {
                surveyAnswers = new SurveyAnswerDto[parcelables.length];
                int i = 0;
                for (Parcelable parcelable : parcelables) {
                    surveyAnswers[i++] = (SurveyAnswerDto) parcelable;
                }
            }

            Parcelable[] parcelableDTO = args.getParcelableArray(PARAM_SURVEY_QUESTION);
            if (parcelableDTO != null) {
                surveyDtos = new SurveyListResult.SurveyDto[parcelableDTO.length];
                int i = 0;
                for (Parcelable parcelable : parcelableDTO) {
                    surveyDtos[i++] = (SurveyListResult.SurveyDto) parcelable;
                }
            }
        }
        defaultToolbarPadding = getResources().getDimensionPixelSize(R.dimen.second_keyline);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        replaceCurrentFragment(VisitSurveyListFragment.newInstance(visitId, customerId,customerName, surveyAnswers, surveyDtos, visitEnded), false, false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return false;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void adjustWhenFragmentChanged(BaseFragment fragment) {
        super.adjustWhenFragmentChanged(fragment);

        if (fragment.getPaddingLeft() != null) {
            // Convert dip to px
            int paddingInPx = LayoutUtils.dipToPx(this, fragment.getPaddingLeft());
            toolbar.setContentInsetsRelative(paddingInPx, toolbar.getContentInsetEnd());
        } else {
            toolbar.setContentInsetsRelative(defaultToolbarPadding, toolbar.getContentInsetEnd());
        }
    }
    public void finishWithAnswers(SurveyAnswerDto[] surveyAnswers) {
        Intent returnIntent = new Intent();
        Bundle b = new Bundle();
        b.putParcelableArray(PARAM_SURVEY_ANSWERS, surveyAnswers);
        returnIntent.putExtras(b);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
