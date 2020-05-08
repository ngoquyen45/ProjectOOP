package com.viettel.dms.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.viettel.dms.R;
import com.viettel.dms.helper.DateTimeUtils;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.layout.GeneralSwipeRefreshLayout;
import com.viettel.dms.helper.layout.LayoutUtils;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.helper.layout.ViewEmptyStateLayout;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.presenter.VisitFeedbackPresenter;
import com.viettel.dms.ui.iview.IVisitFeedbackView;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CustomerFeedbackModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class
        VisitFeedbackActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, IVisitFeedbackView {
    public static String PARAM_CUSTOMER_INFO = "PARAM_CUSTOMER_INFO";
    public static String PARAM_CUSTOMER_NAME = "PARAM_CUSTOMER_NAME";
    public static String PARAM_FEEDBACK_VISITING = "PARAM_VISITING_FEEDBACK";
    public static String PARAM_VISITED = "PARAM_VISITED";

    String customerId;
    String customerName;
    private boolean visited;

    @Bind(R.id.app_bar)
    Toolbar mToolbar;
    @Nullable
    @Bind(R.id.tv_Sub_Title)
    TextView tvSubTitle;
    @Bind(R.id.swipe_refresh)
    GeneralSwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.rv_FeedBack)
    RecyclerView rvFeedBack;
    @Bind(R.id.edt_Message)
    EditText edtMessage;
    @Bind(R.id.itv_Send)
    IconTextView itvSend;
    @Bind(R.id.ll_Input_Form)
    View llInputForm;
    @Bind(R.id.view_State)
    ViewEmptyStateLayout viewEmptyStateLayout;

    MyFeedbackAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private LayoutInflater layoutInflater;
    MenuItem itemDone;
    private boolean firstComment = true;
    private boolean HAVE_NEW_COMMENT_FLAG = false;
    private boolean loading = false;
    private int viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL;

    protected ArrayList<CustomerFeedbackModel> mFeedbackList;
    protected ArrayList<CustomerFeedbackModel> mPendingFeedback;
    private Handler mHandler = new Handler();

    private VisitFeedbackPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_feedback);
        setUp();
        Bundle args = getIntent().getExtras();
        if (args != null) {
            customerId = args.getString(PARAM_CUSTOMER_INFO);
            customerName = args.getString(PARAM_CUSTOMER_NAME);
            visited = args.getBoolean(PARAM_VISITED);
            if (!visited) {
                Parcelable[] parcelables = args.getParcelableArray(PARAM_FEEDBACK_VISITING);
                if (parcelables != null && parcelables.length > 0) {
                    firstComment = false;
                    for (Parcelable item : parcelables) {
                        if (item instanceof CustomerFeedbackModel) {
                            mPendingFeedback.add((CustomerFeedbackModel) item);
                        }
                    }
                }
            }
        }
        presenter = new VisitFeedbackPresenter(this);
        ButterKnife.bind(this);
        initView();
    }

    void setUp() {
        layoutInflater = LayoutInflater.from(this);
        layoutManager = new LinearLayoutManager(this);
        mFeedbackList = new ArrayList<>();
        mPendingFeedback = new ArrayList<>();
        mAdapter = new MyFeedbackAdapter();
    }

    void initView() {
        setTitle(R.string.visit_feedback_title);
        setSupportActionBar(mToolbar);
        if (visited) llInputForm.setVisibility(View.GONE);
        SetTextUtils.setText(tvSubTitle, customerName);
        mToolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        mToolbar.setNavigationOnClickListener(this);
        if (getResources().getBoolean(R.bool.is_tablet)) {
            int paddingInPx = LayoutUtils.dipToPx(this, 80);
            mToolbar.setContentInsetsRelative(paddingInPx, mToolbar.getContentInsetEnd());
        }
        rvFeedBack.setLayoutManager(layoutManager);
        rvFeedBack.setVerticalScrollBarEnabled(true);
        rvFeedBack.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnChildScrollUpListener(new GeneralSwipeRefreshLayout.OnChildScrollUpListener() {
            @Override
            public boolean canChildScrollUp() {
                closeSoftKey();
                return ViewCompat.canScrollVertically(rvFeedBack, -1);
            }
        });
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(loading);
                mSwipeRefreshLayout.setOnRefreshListener(VisitFeedbackActivity.this);
                checkIfHaveData();
            }
        });
        viewEmptyStateLayout.updateViewState(viewState);
    }

    private void checkIfHaveData() {
        if (!loading) {
            if (mFeedbackList.size() == 0) {
                loading = true;
                mSwipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        }
    }

    private void archiveFeedbackData() {
        Intent it = new Intent();
        Bundle b = new Bundle();
        CustomerFeedbackModel[] temp = new CustomerFeedbackModel[mPendingFeedback.size()];
        temp = mPendingFeedback.toArray(temp);
        b.putParcelableArray(VisitFeedbackActivity.PARAM_FEEDBACK_VISITING, temp);
        it.putExtras(b);
        VisitFeedbackActivity.this.setResult(Activity.RESULT_OK, it);
        VisitFeedbackActivity.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!visited) {
            getMenuInflater().inflate(R.menu.mb_menu_action_done, menu);
            itemDone = menu.findItem(R.id.action_done);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            archiveFeedbackData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    void refreshAdapter() {
        Collections.sort(mFeedbackList);
        mAdapter.preProcess();
        if (mFeedbackList.size() > 0) {
            rvFeedBack.scrollToPosition(mFeedbackList.size() - 1);
            viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL);
        } else {
            viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_EMPTY_FEEDBACK);
        }
    }

    @OnClick(R.id.itv_Send)
    void sendMessage() {
        if (!StringUtils.isNullOrEmpty(edtMessage.getText().toString())) {
            Calendar now = Calendar.getInstance();
            Date nowDate = now.getTime();
            final CustomerFeedbackModel newMessage = new CustomerFeedbackModel();
            newMessage.setMessage(edtMessage.getText().toString());
            newMessage.setCreatedTime(nowDate);
            if (firstComment) {
                newMessage.setLastCommentOfDate(true);
                firstComment = false;
            } else newMessage.setLastCommentOfDate(false);
            newMessage.setPosition(true);
            edtMessage.setText("");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    addNewMessage(newMessage);
                }
            });
        }
    }

    public void addNewMessage(CustomerFeedbackModel newMessage) {
        mFeedbackList.add(newMessage);
        mPendingFeedback.add(newMessage);
        HAVE_NEW_COMMENT_FLAG = true;
        mAdapter.preProcess();
        mAdapter.notifyDataSetChanged();
        viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL);
        if (mFeedbackList.size() > 0)
            rvFeedBack.scrollToPosition(mFeedbackList.size() - 1);
    }

    @Override
    public void onClick(View view) {
        if (HAVE_NEW_COMMENT_FLAG) {
            DialogUtils.showConfirmDialog(this, R.string.notify, R.string.visit_feedback_confirm_message, R.string.visit_feedback_confirm_ok, R.string.visit_feedback_confirm_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        archiveFeedbackData();
                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                        VisitFeedbackActivity.this.setResult(Activity.RESULT_CANCELED);
                        VisitFeedbackActivity.this.finish();
                    }
                }
            });
        } else {
            VisitFeedbackActivity.this.setResult(Activity.RESULT_CANCELED);
            VisitFeedbackActivity.this.finish();
        }
    }

    @Override
    public void onRefresh() {
        presenter.processRequestFeedback(customerId);
    }

    @Override
    public void getFeedbackSuccess() {
        mFeedbackList.clear();
        mFeedbackList.addAll(presenter.getLstData());

        if (mPendingFeedback.size() > 0) {
            mFeedbackList.addAll(mPendingFeedback);
        }
        refreshAdapter();
    }

    @Override
    public void getFeedbackError(SdkException info) {
        NetworkErrorDialog.processError(VisitFeedbackActivity.this, info);
        viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NETWORK_ERROR);
    }

    @Override
    public void getFeedbackFinish() {
        loading = false;
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    class MyFeedbackAdapter extends RecyclerView.Adapter<FeedBackViewHolder> {

        private boolean LEFT = false, RIGHT = true;

        public MyFeedbackAdapter() {

        }

        public void preProcess() {
            Date currentDate;
            boolean currentPosition;
            int count = 0, size;

            if (mFeedbackList != null && mFeedbackList.size() > 0) {
                size = mFeedbackList.size();
                count++;
                currentDate = mFeedbackList.get(0).getCreatedTime();
                mFeedbackList.get(0).setLastCommentOfDate(true);
                mFeedbackList.get(0).setFirstCommentOfDate(true);
                if (size > 1) {
                    for (int i = 1; i < size; i++) {
                        if (DateTimeUtils.compare(mFeedbackList.get(i).getCreatedTime(), currentDate) != 0) {
                            currentDate = mFeedbackList.get(i).getCreatedTime();
                            mFeedbackList.get(i).setLastCommentOfDate(true);
                            mFeedbackList.get(i).setFirstCommentOfDate(true);
                            count++;
                        } else {
                            mFeedbackList.get(i - 1).setLastCommentOfDate(false);
                            mFeedbackList.get(i).setLastCommentOfDate(true);
                            mFeedbackList.get(i).setFirstCommentOfDate(false);
                        }
                    }
                }
                if (DateTimeUtils.isToday(mFeedbackList.get(size - 1).getCreatedTime())) {
                    if (count % 2 == 0) {
                        currentPosition = LEFT;
                    } else {
                        currentPosition = RIGHT;
                    }
                } else {
                    if (count % 2 == 0) {
                        currentPosition = RIGHT;
                    } else {
                        currentPosition = LEFT;
                    }
                }

                mFeedbackList.get(0).setPosition(currentPosition);
                if (size > 1) {
                    for (int i = 1; i < size; i++) {
                        if (mFeedbackList.get(i).isFirstCommentOfDate()) {
                            currentPosition = !currentPosition;
                        }
                        mFeedbackList.get(i).setPosition(currentPosition);
                    }
                }
            }
            notifyDataSetChanged();
        }

        private LinearLayout.LayoutParams setLayoutParamsForParent(LinearLayout.LayoutParams params, boolean position) {
            params.gravity = position ? Gravity.RIGHT : Gravity.LEFT;
            if (position) {
                params.rightMargin = (int) getResources().getDimension(R.dimen.margin_standard);
            } else {
                params.leftMargin = (int) getResources().getDimension(R.dimen.margin_standard);
            }
            return params;
        }

        @Override
        public FeedBackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.adapter_customer_feedback_item, parent, false);
            return new FeedBackViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FeedBackViewHolder holder, int position) {
            CustomerFeedbackModel item = mFeedbackList.get(position);
            holder.itemView.setTag(position);

            if (item.isLastCommentOfDate()) {
                String tempDate = DateTimeUtils.formatDateAndTime(item.getCreatedTime());
                holder.txtDate.setText(tempDate);
                holder.txtDate.setVisibility(View.VISIBLE);
            } else {
                holder.txtDate.setVisibility(View.GONE);
            }
            holder.txtMessage.setText(item.getMessage());

            LinearLayout.LayoutParams parentLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (item.isFirstCommentOfDate()) {
                parentLayout.setMargins(0, 8, 0, 0);
            } else parentLayout.setMargins(0, 4, 0, 0);
            holder.llGrandParent.setLayoutParams(parentLayout);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params = setLayoutParamsForParent(params, item.isPosition());
            holder.llParentLayout.setLayoutParams(params);

            if (item.isPosition() == LEFT) {
                holder.llParentLayout.setBackgroundResource(R.drawable.bg_visit_feedback_left);
            } else {
                holder.llParentLayout.setBackgroundResource(R.drawable.bg_visit_feedback_right);
            }
        }

        @Override
        public int getItemCount() {
            return mFeedbackList.size();
        }

    }

    static class FeedBackViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llParentLayout, llGrandParent;
        TextView txtMessage, txtDate;

        public FeedBackViewHolder(View itemView) {
            super(itemView);
            llGrandParent = (LinearLayout) itemView.findViewById(R.id.id_grandparent);
            llParentLayout = (LinearLayout) itemView.findViewById(R.id.id_llparent);
            txtMessage = (TextView) itemView.findViewById(R.id.id_message);
            txtDate = (TextView) itemView.findViewById(R.id.id_date);
        }
    }
}
