package com.viettel.dms.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.viettel.dms.R;
import com.viettel.dms.helper.DateTimeUtils;
import com.viettel.dms.helper.NumberFormatUtils;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.layout.GeneralSwipeRefreshLayout;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.helper.layout.ViewEmptyStateLayout;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.ExchangeReturnDto;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewExchangeReturnFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String PARAM_ID = "PARAM_ID";
    private static final String PARAM_NAME = "PARAM_NAME";
    private static final String PARAM_IS_EXCHANGE = "PARAM_IS_EXCHANGE";
    String exchangeReturnId;
    String customerName;
    boolean isExchange = false;

    private int viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL;

    @Bind(R.id.swipe_refresh)
    GeneralSwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Nullable
    @Bind(R.id.tv_Sub_Title)
    TextView tvSubTitle;
    @Nullable
    @Bind(R.id.tv_Customer_Info)
    TextView tvCustomerInfo;
    @Bind(R.id.view_State)
    ViewEmptyStateLayout viewEmptyStateLayout;

    private LayoutInflater layoutInflater;
    private RecyclerView.LayoutManager layoutManager;
    ListAdapter adapter;
    ExchangeReturnDto data;
    private boolean loading = false;
    private SdkAsyncTask<?> refreshTask;

    public static ReviewExchangeReturnFragment newInstance(String id, String name, boolean isExchange) {
        ReviewExchangeReturnFragment fragment = new ReviewExchangeReturnFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_ID, id);
        args.putString(PARAM_NAME, name);
        args.putBoolean(PARAM_IS_EXCHANGE, isExchange);
        fragment.setArguments(args);
        return fragment;
    }

    public ReviewExchangeReturnFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle data = getArguments();
            exchangeReturnId = data.getString(PARAM_ID);
            customerName = data.getString(PARAM_NAME);
            isExchange = data.getBoolean(PARAM_IS_EXCHANGE);
        }
        adapter = new ListAdapter();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review_place_order, container, false);
        ButterKnife.bind(this, view);
        setTitleResource(isExchange ? R.string.exchange_return_title_exchange : R.string.exchange_return_title_return);
        SetTextUtils.setText(tvSubTitle, customerName);
        setPaddingLeft(getResources().getBoolean(R.bool.is_tablet) ? 104f : 72f);

        layoutInflater = getLayoutInflater(savedInstanceState);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnChildScrollUpListener(new GeneralSwipeRefreshLayout.OnChildScrollUpListener() {
            @Override
            public boolean canChildScrollUp() {
                ((BaseActivity) getActivity()).closeSoftKey();
                return ViewCompat.canScrollVertically(recyclerView, -1);
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(loading);
                swipeRefreshLayout.setOnRefreshListener(ReviewExchangeReturnFragment.this);
                checkIfHaveData();
            }
        });
        viewEmptyStateLayout.updateViewState(viewState);
        return view;
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }

    private void checkIfHaveData() {
        if (!loading) {
            if (data == null) {
                loading = true;
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            } else {
                rebindData();
            }
        }
    }

    private void rebindData() {
        if (data == null) {
            return;
        }
        SetTextUtils.setText(tvCustomerInfo, DateTimeUtils.formatDateAndTime(data.getCreatedTime()));
        adapter.beforeDatasetChanged();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        if (isExchange) {
            refreshTask = MainEndpoint
                    .get()
                    .requestExchangeDetail(exchangeReturnId)
                    .executeAsync(mCallback);
        } else {
            refreshTask = MainEndpoint
                    .get()
                    .requestReturnDetail(exchangeReturnId)
                    .executeAsync(mCallback);
        }
    }

    RequestCompleteCallback<ExchangeReturnDto> mCallback = new RequestCompleteCallback<ExchangeReturnDto>() {
        @Override
        public void onSuccess(final ExchangeReturnDto response) {
            data = response;
            viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL);
            rebindData();
        }

        @Override
        public void onError(SdkException info) {
            viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NETWORK_ERROR);
            NetworkErrorDialog.processError(context, info);
        }

        @Override
        public void onFinish(boolean canceled) {
            refreshTask = null;
            loading = false;
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setEnabled(false);
                }
            });
        }
    };

    private class ListAdapter extends RecyclerView.Adapter {
        int TYPE_HEADER = 0;
        int TYPE_ITEM = 1;
        int TYPE_TOTAL = 2;
        int productsSize;

        private void beforeDatasetChanged() {
            if (data == null) {
                productsSize = 0;
            } else {
                productsSize = data.getDetails() == null ? 0 : data.getDetails().length;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                View view = layoutInflater.inflate(R.layout.adapter_review_exchange_return_header_item, parent, false);
                HeaderViewHolder viewHolder = new HeaderViewHolder(view);
                return viewHolder;
            }
            if (viewType == TYPE_ITEM) {
                View view = layoutInflater.inflate(R.layout.adapter_review_exchange_return_item, parent, false);
                ItemViewHolder viewHolder = new ItemViewHolder(view);
                return viewHolder;
            }
            if (viewType == TYPE_TOTAL) {
                View view = layoutInflater.inflate(R.layout.adapter_review_exchange_return_total, parent, false);
                TotalViewHolder viewHolder = new TotalViewHolder(view);
                return viewHolder;
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position == 0) {
                return;
            }
            if (position < productsSize + 1) {
                ItemViewHolder vh = (ItemViewHolder) holder;
                ExchangeReturnDto.ExchangeReturnDetailDto info = data.getDetails()[position - 1];
                vh.imgProductImage.setImageDrawable(null);
                if (info.getProduct().getPhoto() != null) {
                    Picasso
                            .with(context)
                            .load(MainEndpoint.get().getImageURL(info.getProduct().getPhoto()))
                            .noFade()
                            .placeholder(R.drawable.img_logo_default)
                            .into(vh.imgProductImage);
                }
                vh.tvProductName.setText(info.getProduct().getName());
                SetTextUtils.setText(vh.tvProductCode, info.getProduct().getCode());
                SetTextUtils.setText(vh.tvProductUOM, info.getProduct().getUom().getName());
                vh.tvQuantity.setText(
                        NumberFormatUtils.formatNumber(info.getQuantity())
                );
                return;
            }
            if (position == productsSize + 1) {
                TotalViewHolder vh = (TotalViewHolder) holder;
                vh.tvTotal.setText(NumberFormatUtils.formatNumber(data.getQuantity()));
            }
        }

        @Override
        public int getItemCount() {
            if (data == null) {
                return 0;
            }
            return 1 + productsSize + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) return TYPE_HEADER;
            if (position < productsSize + 1) return TYPE_ITEM;
            if (position == productsSize + 1) return TYPE_TOTAL;
            return TYPE_ITEM;
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img_Product_Image)
        CircleImageView imgProductImage;
        @Bind(R.id.tv_Product_Name)
        TextView tvProductName;
        @Nullable
        @Bind(R.id.tv_Product_Code)
        TextView tvProductCode;
        @Nullable
        @Bind(R.id.tv_Product_UOM)
        TextView tvProductUOM;
        @Bind(R.id.tv_Quantity)
        TextView tvQuantity;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class TotalViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_Total)
        TextView tvTotal;

        public TotalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
