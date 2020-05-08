package com.viettel.dms.helper.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.viettel.dms.R;
import com.viettel.dms.helper.DateTimeUtils;
import com.viettel.dms.helper.layout.DividerItemDecoration;
import com.viettel.dms.helper.layout.HeaderRecyclerDividerItemDecorator;
import com.viettel.dms.helper.layout.ViewEmptyStateLayout;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.PromotionListItem;
import com.viettel.dmsplus.sdk.models.PromotionListResult;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

/**
 * @author ThanhNV60
 */
public class PromotionListDialog implements OnClickListener {

    protected Context context;

    private String visitId;

    private Dialog dialog;
    private ImageButton btnClose;

    private LayoutInflater layoutInflater;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ViewEmptyStateLayout viewEmptyStateLayout;
    private int viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL;
    private ListAdapter adapter;
    private PromotionListItem[] items;

    private SdkAsyncTask<?> refreshTask;

    @SuppressLint("InflateParams")
    public PromotionListDialog(Context context, String visitId) {
        this.context = context;
        this.visitId = visitId;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.dialog = new Dialog(this.context);
        this.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.dialog.setContentView(R.layout.dialog_promotion_list);
        this.dialog.setCancelable(false);

        initViews();

        onRefresh();
    }

    private void initViews() {
        btnClose = (ImageButton) dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);

        recyclerView = (RecyclerView) dialog.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        viewEmptyStateLayout = (ViewEmptyStateLayout) dialog.findViewById(R.id.view_State);

        adapter = new ListAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(
                new HeaderRecyclerDividerItemDecorator(
                        context, DividerItemDecoration.VERTICAL_LIST, false, false,
                        R.drawable.bg_dialog_promotion_list_divider));
        viewEmptyStateLayout.updateViewState(viewState);
    }

    public void onRefresh() {
        refreshTask = MainEndpoint.get().requestPromotionList().executeAsync(refreshCallback);
    }

    private RequestCompleteCallback<PromotionListResult> refreshCallback = new RequestCompleteCallback<PromotionListResult>() {
        @Override
        public void onSuccess(PromotionListResult info) {
            items = info.getList();
            rebindData();
        }

        @Override
        public void onError(SdkException info) {
            NetworkErrorDialog.processError(context, info);
        }

        @Override
        public void onFinish(boolean canceled) {
            refreshTask = null;
        }
    };

    private void rebindData() {
        if (items.length <= 0) {
            viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_EMPTY_PROMOTION);
        } else {
            viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == btnClose.getId()) {
            this.dismiss();
        }
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
        if (refreshTask != null) {
            refreshTask.cancel(true);
            refreshTask = null;
        }
    }

    private class ListAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.adapter_dialog_promotion, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ItemViewHolder vh = (ItemViewHolder) holder;
            PromotionListItem item = items[position];
            vh.tvPromotionName.setText(item.getName());

            String content = context.getResources().getString(R.string.promotion_available_dialog_time_content);
            content = content.replace("{0}", DateTimeUtils.formatDate(item.getStartDate()));
            content = content.replace("{1}", DateTimeUtils.formatDate(item.getEndDate()));
            vh.tvPromotionDate.setText(content);

            vh.tvApplyTo.setText(item.getApplyFor());
            vh.tvContent.setText(item.getDescription());
        }

        @Override
        public int getItemCount() {
            if (items != null) {
                return items.length;
            }
            return 0;
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvPromotionName;
        TextView tvPromotionDate;
        TextView tvApplyTo;
        TextView tvContent;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvPromotionName = (TextView) itemView.findViewById(R.id.tvPromotionName);
            tvPromotionDate = (TextView) itemView.findViewById(R.id.tvPromotionDate);
            tvApplyTo = (TextView) itemView.findViewById(R.id.tvApplyTo);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
        }
    }
}
