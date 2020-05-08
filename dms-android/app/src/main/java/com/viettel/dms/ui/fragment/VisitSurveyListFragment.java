package com.viettel.dms.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.viettel.dms.R;
import com.viettel.dms.helper.DateTimeUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.layout.DividerItemDecoration;
import com.viettel.dms.helper.layout.GeneralSwipeRefreshLayout;
import com.viettel.dms.helper.layout.HeaderRecyclerDividerItemDecorator;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.helper.layout.ThemeUtils;
import com.viettel.dms.helper.layout.ViewEmptyStateLayout;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.presenter.VisitSurveyPresenter;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dms.ui.activity.VisitSurveyActivity;
import com.viettel.dms.ui.iview.IVisitSurveyView;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.SurveyAnswerDto;
import com.viettel.dmsplus.sdk.models.SurveyListResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VisitSurveyListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, IVisitSurveyView {

    public static String PARAM_CUSTOMER_ID = "PARAM_CUSTOMER_ID";
    public static String PARAM_CUSTOMER_NAME = "PARAM_CUSTOMER_NAME";
    public static String PARAM_VISIT_ID = "PARAM_VISIT_ID";
    public static String PARAM_VISIT_ENDED = "PARAM_VISIT_ENDED";
    public static String PARAM_SURVEY_QUESTION = "PARAM_SURVEY_QUESTION";
    public static String PARAM_SURVEY_ANSWERS = "PARAM_SURVEY_ANSWERS";

    @Bind(R.id.swipe_refresh)
    GeneralSwipeRefreshLayout swipeRefreshLayout;

    private LayoutInflater layoutInflater;
    private RecyclerView.LayoutManager layoutManager;
    @Bind(R.id.rv_Survey_List)
    RecyclerView rvSurveyList;
    @Nullable
    @Bind(R.id.tv_Sub_Title)
    TextView tvSubTitle;
    @Bind(R.id.view_State)
    ViewEmptyStateLayout viewEmptyStateLayout;
    private int viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL;

    private String customerId;
    private String customerName;
    private String mVisitId;
    private boolean visitEnded;
    private Map<String, SurveyAnswerDto> mapSurveyAnswers;
    int colorPending,colorComplete,colorRefuse;

    private List<SurveyListResult.SurveyDto> surveyListItems;
    private ListAdapter adapter;

    private boolean loading = false;
    private VisitSurveyPresenter presenter;

    public static VisitSurveyListFragment newInstance(String visitId, String customerId, String name, SurveyAnswerDto[] surveyAnswers, SurveyListResult.SurveyDto[] surveyDtos, boolean visitEnded) {
        VisitSurveyListFragment fragment = new VisitSurveyListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_CUSTOMER_ID, customerId);
        bundle.putString(PARAM_CUSTOMER_NAME, name);
        bundle.putString(PARAM_VISIT_ID, visitId);
        bundle.putBoolean(PARAM_VISIT_ENDED, visitEnded);
        bundle.putParcelableArray(PARAM_SURVEY_ANSWERS, surveyAnswers);
        bundle.putParcelableArray(PARAM_SURVEY_QUESTION, surveyDtos);
        fragment.setArguments(bundle);
        return fragment;
    }

    public VisitSurveyListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.layoutInflater = getLayoutInflater(savedInstanceState);
        this.mapSurveyAnswers = new HashMap<>();
        if (getArguments() != null) {
            this.customerId = getArguments().getString(PARAM_CUSTOMER_ID);
            this.customerName = getArguments().getString(PARAM_CUSTOMER_NAME);
            this.mVisitId = getArguments().getString(PARAM_VISIT_ID);
            this.visitEnded = getArguments().getBoolean(PARAM_VISIT_ENDED, false);
        }
        if (visitEnded) {
            surveyListItems = new ArrayList<>();
            Parcelable[] parcelDto = getArguments().getParcelableArray(PARAM_SURVEY_QUESTION);
            if (parcelDto != null) {
                for (Parcelable parcelable : parcelDto) {
                    SurveyListResult.SurveyDto surveyDto = (SurveyListResult.SurveyDto) parcelable;
                    surveyListItems.add(surveyDto);
                }
            }
        }
        if (getArguments() != null) {
            Parcelable[] parcelables = getArguments().getParcelableArray(PARAM_SURVEY_ANSWERS);
            if (parcelables != null) {
                for (Parcelable parcelable : parcelables) {
                    SurveyAnswerDto surveyAnswerDto = (SurveyAnswerDto) parcelable;
                    this.mapSurveyAnswers.put(surveyAnswerDto.getSurveyId(), surveyAnswerDto);
                }
            }
        }
        this.adapter = new ListAdapter();
        this.presenter = new VisitSurveyPresenter(this);
        colorComplete = ThemeUtils.getColor(context,R.attr.colorPrimary);
        colorPending = ThemeUtils.getColor(context,R.attr.colorSecondary);
        LocalBroadcastManager.getInstance(context).registerReceiver(answerAddedReceiver,
                new IntentFilter(HardCodeUtil.BroadcastReceiver.ACTION_CUSTOMER_VISIT_SURVEY_ADDED));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visit_survey_list, container, false);
        ButterKnife.bind(this, view);
        if (getResources().getBoolean(R.bool.is_tablet)) setPaddingLeft(80f);
        this.setTitleResource(R.string.visit_survey_list_title);
        SetTextUtils.setText(tvSubTitle, customerName);
        ActionBar actionBar = ((BaseActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }
        setHasOptionsMenu(true);
        rvSurveyList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        rvSurveyList.setLayoutManager(layoutManager);
        rvSurveyList.addItemDecoration(
                new HeaderRecyclerDividerItemDecorator(
                        context, DividerItemDecoration.VERTICAL_LIST, false, false, getResources().getBoolean(R.bool.is_tablet) ?
                        R.drawable.divider_list_80 : R.drawable.divider_list_0));
        rvSurveyList.setAdapter(adapter);

        swipeRefreshLayout.setOnChildScrollUpListener(new GeneralSwipeRefreshLayout.OnChildScrollUpListener() {
            @Override
            public boolean canChildScrollUp() {
                return ViewCompat.canScrollVertically(rvSurveyList, -1);
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(loading);
                swipeRefreshLayout.setOnRefreshListener(VisitSurveyListFragment.this);
                checkIfHaveData();
            }
        });
        viewEmptyStateLayout.updateViewState(viewState);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ButterKnife.unbind(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    BroadcastReceiver answerAddedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SurveyAnswerDto surveyAnswer = intent.getParcelableExtra("surveyAnswer");
            String visitId = intent.getStringExtra("visitId");

            if (!TextUtils.equals(visitId, mVisitId)) {
                return;
            }

            mapSurveyAnswers.put(surveyAnswer.getSurveyId(), surveyAnswer);
        }
    };

    @Override
    public boolean onBackPressed() {
        if (visitEnded) {
            getActivity().finish();
        } else {
            VisitSurveyActivity surveyActivity = (VisitSurveyActivity) getActivity();
            Collection<SurveyAnswerDto> collections = mapSurveyAnswers.values();
            SurveyAnswerDto[] surveyAnswers = new SurveyAnswerDto[collections.size()];
            surveyAnswers = collections.toArray(surveyAnswers);
            surveyActivity.finishWithAnswers(surveyAnswers);
        }
        return true;
    }

    @Override
    public void onRefresh() {
        if (!visitEnded) {
            presenter.requestSurveyList(customerId);
        } else {
            rebindData();
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }


    private void checkIfHaveData() {
        if (!loading) {
            if (surveyListItems == null) {
                loading = true;
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            } else {
                rebindData();
            }
        }
    }

    private void rebindData() {
        if (surveyListItems.size() <= 0) {
            viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_EMPTY_FEEDBACK);
        } else {
            viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void getSurveySuccess() {
        if (presenter.getmData() != null && presenter.getmData().getList() != null) {
            surveyListItems = new ArrayList<>(Arrays.asList(presenter.getmData().getList()));
        } else {
            surveyListItems = new ArrayList<>();
        }
        rebindData();
    }

    @Override
    public void getSurveyError(SdkException info) {
        NetworkErrorDialog.processError(context, info);
        viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NETWORK_ERROR);
    }

    @Override
    public void getSurveyFinish() {
        loading = false;
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private class ListAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition = (int) view.getTag();
                if (itemPosition < 0 || itemPosition >= surveyListItems.size()) {
                    return;
                }
                SurveyListResult.SurveyDto item = surveyListItems.get(itemPosition);
                replaceCurrentFragment(VisitSurveyEditFragment.newInstance(mVisitId, customerName,
                        item, mapSurveyAnswers.get(item.getId()), visitEnded));
            }

        };

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.adapter_visit_survey_list, parent, false);
            view.setOnClickListener(mOnClickListener);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder vh, int position) {
            vh.itemView.setTag(position);
            SurveyListResult.SurveyDto item = surveyListItems.get(position);
            vh.tvSurveyName.setText(item.getName());
            vh.tvSurveyTime.setText(
                    StringUtils.joinWithDashes(context,
                            DateTimeUtils.formatDate(item.getStartDate()),
                            DateTimeUtils.formatDate(item.getEndDate())
                    )
            );
            SetTextUtils.setText(vh.tvQuestionCount, new Integer(item.getQuestions().length).toString());
            if (visitEnded) {
                SetTextUtils.setText(vh.itvSurveyStatus, getString(R.string.visit_survey_list_item_complete));
                SetTextUtils.setTextColor(vh.itvSurveyStatus,colorComplete);
                SetTextUtils.setText(vh.tvStatus, HardCodeUtil.getSurveyStatus(context, HardCodeUtil.SurveyStatus.COMPLETED));
            } else {
                if (mapSurveyAnswers.containsKey(item.getId())) {
                    SetTextUtils.setText(vh.itvSurveyStatus, getString(R.string.visit_survey_list_item_complete));
                    SetTextUtils.setTextColor(vh.itvSurveyStatus, colorComplete);
                    SetTextUtils.setText(vh.tvStatus, HardCodeUtil.getSurveyStatus(context, HardCodeUtil.SurveyStatus.COMPLETING));
                } else {
                    SetTextUtils.setText(vh.itvSurveyStatus, getString(R.string.visit_survey_list_item_pending));
                    SetTextUtils.setTextColor(vh.itvSurveyStatus,colorPending);
                    SetTextUtils.setText(vh.tvStatus, HardCodeUtil.getSurveyStatus(context, HardCodeUtil.SurveyStatus.NOT_COMPLETED),colorPending, null, null);
                }
            }
        }

        @Override
        public int getItemCount() {
            if (surveyListItems != null) {
                return surveyListItems.size();
            }
            return 0;
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_Survey_Name)
        TextView tvSurveyName;
        @Bind(R.id.tv_Survey_Time)
        TextView tvSurveyTime;
        @Nullable
        @Bind(R.id.itv_Survey_Status)
        IconTextView itvSurveyStatus;
        @Nullable
        @Bind(R.id.tv_Question_Count)
        TextView tvQuestionCount;
        @Nullable
        @Bind(R.id.tvStatus)
        TextView tvStatus;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
