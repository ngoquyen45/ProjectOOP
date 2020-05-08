package com.viettel.dms.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.idunnololz.widgets.AnimatedExpandableListView;
import com.joanzapata.iconify.widget.IconTextView;
import com.viettel.dms.R;
import com.viettel.dms.helper.DateTimeUtils;
import com.viettel.dms.helper.layout.GeneralSwipeRefreshLayout;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.helper.layout.ViewEmptyStateLayout;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.presenter.PromotionListPresenter;
import com.viettel.dms.ui.iview.IPromotionListView;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.PromotionListItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author PHAMHUNG
 * @since 30/10/2015
 */
public class PromotionListTBFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, IPromotionListView {

    @Bind(R.id.swipe_refresh)
    GeneralSwipeRefreshLayout swipeRefresh;
    @Bind(R.id.rv_Promotion_List)
    AnimatedExpandableListView rvPromotionList;
    @Bind(R.id.view_State)
    ViewEmptyStateLayout viewEmptyStateLayout;

    private boolean loading = false;
    PromotionExpandableAdapter adapter;
    private LayoutInflater layoutInflater;

    private int viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL;
    private int previousGroup = -1;

    PromotionListPresenter presenter;

    public static PromotionListTBFragment newInstance() {
        PromotionListTBFragment fragment = new PromotionListTBFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PromotionListTBFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        presenter = new PromotionListPresenter(this);
        adapter = new PromotionExpandableAdapter();
        this.layoutInflater = getLayoutInflater(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setPaddingLeft(80f);
        View view = inflater.inflate(R.layout.fragment_promotion_list, container, false);
        ButterKnife.bind(this, view);
        setTitleResource(R.string.promotion_available_title);

        swipeRefresh.setOnChildScrollUpListener(new GeneralSwipeRefreshLayout.OnChildScrollUpListener() {
            @Override
            public boolean canChildScrollUp() {
                return ViewCompat.canScrollVertically(rvPromotionList, -1);
            }
        });
        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(loading);
                swipeRefresh.setOnRefreshListener(PromotionListTBFragment.this);
                checkIfHaveData();
            }
        });
        rvPromotionList.setAdapter(adapter);
        // In order to show animations, we need to use a custom click handler
        // for our ExpandableListView.
        rvPromotionList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (rvPromotionList.isGroupExpanded(groupPosition)) {
                    rvPromotionList.collapseGroupWithAnimation(groupPosition);
                    previousGroup = -1;
                } else {
                    rvPromotionList.expandGroupWithAnimation(groupPosition);
                    if (previousGroup != -1 && previousGroup != groupPosition) {
                        rvPromotionList.collapseGroupWithAnimation(previousGroup);
                    }
                    previousGroup = groupPosition;
                }
                return true;
            }

        });
        viewEmptyStateLayout.updateViewState(viewState);
        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ButterKnife.unbind(this);
    }

    private void checkIfHaveData() {
        if (!loading) {
            if (presenter.getDataNotFilter() == null) {
                loading = true;
                swipeRefresh.setRefreshing(true);
                onRefresh();
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }


    @Override
    public void onRefresh() {
        presenter.requestPromotionList();
        adapter.notifyDataSetChanged();
    }


    @Override
    public void getPromotionSuccess() {
        adapter.refreshData();
        adapter.notifyDataSetChanged();
    }


    @Override
    public void getPromotionError(SdkException info) {
        NetworkErrorDialog.processError(context, info);
        viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NETWORK_ERROR);
    }

    @Override
    public void getPromotionFinish() {
        loading = false;
        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    class PromotionExpandableAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
        List<PromotionListItem> visibleData = new ArrayList<>();

        public void refreshData() {
            this.visibleData.clear();
            if (presenter == null || presenter.getDataNotFilter().isEmpty()){
                viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_EMPTY_PROMOTION);
            }else{
                viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL);
                this.visibleData.addAll(presenter.getDataNotFilter());
                notifyDataSetChanged();
            }
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder vh;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.adapter_promotion_child_tb, parent, false);
                vh = new ChildHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ChildHolder) convertView.getTag();
            }
            PromotionListItem item = visibleData.get(groupPosition);
            String time = context.getResources().getString(R.string.promotion_available_dialog_time_content);
            String tempStartTime = DateTimeUtils.formatDate(item.getStartDate());
            String tempEndTime = DateTimeUtils.formatDate(item.getEndDate());
            time = time.replace("{0}", tempStartTime);
            time = time.replace("{1}", tempEndTime);
            SetTextUtils.setText(vh.tvTime, time);
            SetTextUtils.setText(vh.tvApplyTo, item.getApplyFor());
            SetTextUtils.setText(vh.tvContent, item.getDescription());
            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            if (visibleData == null) return 0;
            if (visibleData.get(groupPosition) != null) return 1;// We have only 1 child
            return 0;
        }

        @Override
        public int getGroupCount() {
            if (visibleData == null) return 0;
            return visibleData.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return visibleData.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return visibleData.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return groupPosition * childPosition + childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupHolder vh;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.adapter_promotion_group_tb, parent, false);
                vh = new GroupHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (GroupHolder) convertView.getTag();
            }
            PromotionListItem item = visibleData.get(groupPosition);
            updateView(vh.border, vh.itvIndicator, isExpanded);
            SetTextUtils.setText(vh.tvName, item.getName());
            SetTextUtils.setText(vh.tvStart, DateTimeUtils.formatDate(item.getStartDate()));
            SetTextUtils.setText(vh.tvEnd, DateTimeUtils.formatDate(item.getEndDate()));
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        public void updateView(View border, IconTextView itv, boolean isExpand) {
            if (border == null) return;
            if (isExpand) {
                itv.setText("{md-keyboard-arrow-up 24dp @color/Black26}");
                border.setVisibility(View.INVISIBLE);
            } else {
                border.setVisibility(View.VISIBLE);
                itv.setText("{md-keyboard-arrow-down 24dp @color/Black26}");
            }
        }
    }

    public static class ChildHolder {
        @Bind(R.id.tv_Time)
        TextView tvTime;
        @Bind(R.id.tv_Apply_To)
        TextView tvApplyTo;
        @Bind(R.id.tv_Content)
        TextView tvContent;

        public ChildHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

    public static class GroupHolder {
        @Bind(R.id.tv_Promotion_Name)
        TextView tvName;
        @Bind(R.id.tv_Promotion_Start)
        TextView tvStart;
        @Bind(R.id.tv_Promotion_End)
        TextView tvEnd;
        @Bind(R.id.border)
        View border;
        @Bind(R.id.rl_Icon)
        RelativeLayout rlIcon;
        @Nullable
        @Bind(R.id.itv_Indicator)
        IconTextView itvIndicator;

        public GroupHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }

    }
}
