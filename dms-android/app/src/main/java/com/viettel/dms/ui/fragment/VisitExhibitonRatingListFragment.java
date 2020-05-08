package com.viettel.dms.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dms.ui.activity.VisitExhibitionRatingActivity;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.ExhibitionDto;
import com.viettel.dmsplus.sdk.models.ExhibitionRatingDto;
import com.viettel.dmsplus.sdk.models.ExhibitionResult;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VisitExhibitonRatingListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String PARAM_VISIT_ID = "PARAM_VISIT_ID";
    private static final String PARAM_CUSTOMER_INFO = "PARAM_CUSTOMER_INFO";
    private static final String PARAM_SUBMIT_CHANGE = "PARAM_SUBMIT_CHANGE";
    private static final String PARAM_DTO = "PARAM_DTO";
    private static final String PARAM_VISITED = "PARAM_VISITED";

    boolean visited;
    String visitID;
    String customerId;
    ExhibitionRatingDto[] previousChange;
    ExhibitionDto[] dtos;

    LayoutInflater layoutInflater;
    RecyclerView.LayoutManager layoutManager;
    @Bind(R.id.rv_Exhibition_List) RecyclerView mRecyclerView;
    @Bind(R.id.swipe_refresh) GeneralSwipeRefreshLayout swipeRefreshLayout;
    ArrayList<ExhibitionDto> mExhibitionList;

    ListAdapter adapter;
    private SdkAsyncTask<?> refreshTask;
    private boolean loading = false;

    public static VisitExhibitonRatingListFragment newInstance(boolean visited, String customerID, ExhibitionRatingDto[] changeRate, ExhibitionDto[] exhibitionDtos, String visitId) {
        VisitExhibitonRatingListFragment fragment = new VisitExhibitonRatingListFragment();
        Bundle args = new Bundle();
        args.putBoolean(PARAM_VISITED, visited);
        args.putString(PARAM_CUSTOMER_INFO, customerID);
        args.putParcelableArray(PARAM_SUBMIT_CHANGE, changeRate);
        args.putParcelableArray(PARAM_DTO, exhibitionDtos);
        args.putString(PARAM_VISIT_ID, visitId);
        fragment.setArguments(args);
        return fragment;
    }

    public VisitExhibitonRatingListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            visited = getArguments().getBoolean(PARAM_VISITED);
            visitID = getArguments().getString(PARAM_VISIT_ID);
            customerId = getArguments().getString(PARAM_CUSTOMER_INFO);
            if (visited) {
                Parcelable[] dtoParcel = getArguments().getParcelableArray(PARAM_DTO);
                if (dtoParcel != null) {
                    dtos = new ExhibitionDto[dtoParcel.length];
                    for (int i = 0; i < dtoParcel.length; i++) {
                        dtos[i] = (ExhibitionDto) dtoParcel[i];
                    }
                }
            }

            Parcelable[] parcelables = getArguments().getParcelableArray(PARAM_SUBMIT_CHANGE);
            if (parcelables != null) {
                previousChange = new ExhibitionRatingDto[parcelables.length];
                for (int i = 0; i < parcelables.length; i++) {
                    previousChange[i] = (ExhibitionRatingDto) parcelables[i];
                }
            }
        }
        adapter = new ListAdapter();
        layoutInflater = getLayoutInflater(savedInstanceState);
        setHasOptionsMenu(true);
        LocalBroadcastManager.getInstance(context).registerReceiver(ratingAddReceiver,
                new IntentFilter(HardCodeUtil.BroadcastReceiver.ACTION_CUSTOMER_VISIT_RATING_ADDED));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visit_exhibiton_rating_list, container, false);
        ButterKnife.bind(this, view);
        this.setTitleResource(R.string.visit_survey_list_title);
        ActionBar actionBar = ((BaseActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }
        layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(
                new HeaderRecyclerDividerItemDecorator(
                        context, DividerItemDecoration.VERTICAL_LIST, false, false,
                        R.drawable.divider_list_0));

        swipeRefreshLayout.setOnChildScrollUpListener(new GeneralSwipeRefreshLayout.OnChildScrollUpListener() {
            @Override
            public boolean canChildScrollUp() {
                return ViewCompat.canScrollVertically(mRecyclerView, -1);
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(loading);
                swipeRefreshLayout.setOnRefreshListener(VisitExhibitonRatingListFragment.this);
                checkIfHaveData();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(ratingAddReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public boolean onBackPressed() {
        if (visited) {
            getActivity().finish();
        } else {
            VisitExhibitionRatingActivity activity = (VisitExhibitionRatingActivity) getActivity();
            activity.finishWithChangeRating(previousChange);
        }
        return true;
    }

    BroadcastReceiver ratingAddReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ExhibitionRatingDto submit = intent.getParcelableExtra("rate");
            if (submit != null) {
                for (ExhibitionDto info : mExhibitionList) {
                    if (info.getId().equalsIgnoreCase(submit.getExhibitionId())) {
                        info.setRated(true);
                        break;
                    }
                }
                if (previousChange != null) {
                    boolean update = false;
                    for (ExhibitionRatingDto s : previousChange) {
                        if (s.getExhibitionId().equalsIgnoreCase(submit.getExhibitionId())) {
                            s.setItems(submit.getItems());
                            update = true;
                            break;
                        }
                    }
                    if (!update) {
                        List<ExhibitionRatingDto> temp = new ArrayList<>(Arrays.asList(previousChange));
                        temp.add(submit);
                        previousChange = new ExhibitionRatingDto[temp.size()];
                        int i = 0;
                        for (ExhibitionRatingDto rs : temp) {
                            previousChange[i++] = rs;
                        }
                    }
                } else {
                    previousChange = new ExhibitionRatingDto[1];
                    previousChange[0] = submit;
                }
            }
            adapter.notifyDataSetChanged();
        }
    };

    void checkIfHaveData() {
        if (!loading) {
            if (mExhibitionList == null) {
                loading = true;
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            } else {
                rebindData();
            }
        }
    }

    void rebindData() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        if (visited) {
            if (mExhibitionList == null) {
                mExhibitionList = dtos != null ? new ArrayList<>(Arrays.asList(dtos)) : new ArrayList<ExhibitionDto>();
            }
            adapter.refreshData(mExhibitionList);
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    loading = false;
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            //refreshTask = MainEndpoint.get().requestExhibitionList(customerId).executeAsync(mCallback);;
        }
    }

    RequestCompleteCallback<ExhibitionResult> mCallback = new RequestCompleteCallback<ExhibitionResult>() {

        @Override
        public void onSuccess(ExhibitionResult data) {
            if (data.getList() != null) {
                mExhibitionList = new ArrayList<>(Arrays.asList(data.getList()));
            } else {
                mExhibitionList = new ArrayList<>();
            }
            // only show exhibition evaluated
            if (visited) {
                if (previousChange != null && previousChange.length > 0) {
                    List<ExhibitionDto> evaluateExhibition = new ArrayList<>();
                    for (ExhibitionRatingDto eRD : previousChange) {
                        for (ExhibitionDto eD : mExhibitionList) {
                            if (eRD.getExhibitionId().equalsIgnoreCase(eD.getId())) {
                                evaluateExhibition.add(eD);
                                break;
                            }
                        }
                    }
                    mExhibitionList.clear();
                    mExhibitionList.addAll(evaluateExhibition);
                } else {
                    mExhibitionList.clear();
                }

            }
            adapter.refreshData(mExhibitionList);
        }

        @Override
        public void onError(SdkException info) {
            NetworkErrorDialog.processError(context, info);
        }

        @Override
        public void onFinish(boolean canceled) {
            super.onFinish(canceled);
            refreshTask = null;
            loading = false;
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    };


    class ListAdapter extends RecyclerView.Adapter<ExhibitionViewHolder> {
        List<ExhibitionDto> visibleData = new ArrayList<>();

        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition = (int) view.getTag();
                if (itemPosition < 0 || itemPosition >= visibleData.size()) {
                    return;
                }
                ExhibitionDto item = visibleData.get(itemPosition);
                ExhibitionRatingDto s = null;
                if (previousChange != null) {
                    for (ExhibitionRatingDto change : previousChange) {
                        if (change.getExhibitionId().equalsIgnoreCase(item.getId())) {
                            s = change;
                            break;
                        }
                    }
                }
                replaceCurrentFragment(VisitExhibitionRatingEditFragment.newInstance(item, s, visited));
            }
        };

        @Override
        public ExhibitionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.adapter_visit_exhibition_rating_list, parent, false);
            view.setOnClickListener(mOnClickListener);
            return new ExhibitionViewHolder(view);
        }


        @Override
        public void onBindViewHolder(ExhibitionViewHolder holder, int position) {
            holder.itemView.setTag(position);
            ExhibitionDto item = visibleData.get(position);

            holder.tvExhibitionName.setText(item.getName());
            holder.tvExhibitionTime.setText(
                    StringUtils.joinWithDashes(context,
                            DateTimeUtils.formatDate(item.getStartDate()),
                            DateTimeUtils.formatDate(item.getEndDate())
                    )
            );
            if (visited) {
                holder.itvExhibitionStatus.setText(R.string.visit_exhibition_rating_list_item_complete);
            } else {
                if (item.isRated()) {
                    holder.itvExhibitionStatus.setText(R.string.visit_exhibition_rating_list_item_complete);
                } else {
                    holder.itvExhibitionStatus.setText(R.string.visit_exhibition_rating_list_item_pending);
                }
            }
        }

        @Override
        public int getItemCount() {
            return visibleData.size();
        }

        private void refreshData(List<ExhibitionDto> data) {
            if (!visited) {
                if (previousChange != null) {
                    for (ExhibitionRatingDto sm : previousChange) {
                        for (ExhibitionDto info : data) {
                            if (info.getId().equalsIgnoreCase(sm.getExhibitionId())) {
                                info.setRated(true);
                                break;
                            }
                        }
                    }
                }
            }
            visibleData.clear();
            visibleData.addAll(data);
            notifyDataSetChanged();
        }
    }

    class ExhibitionViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_Exhibition_Name) TextView tvExhibitionName;
        @Bind(R.id.tv_Exhibition_Time) TextView tvExhibitionTime;
        @Bind(R.id.itv_Exhibition_Status) IconTextView itvExhibitionStatus;

        public ExhibitionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
