package com.viettel.dms.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.viettel.dms.R;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.FileUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.NumberFormatUtils;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.helper.location.LocationHelper;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.presenter.CustomerVisitPresenter;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dms.ui.activity.CloseVisitActivity;
import com.viettel.dms.ui.activity.CustomerInfoActivity;
import com.viettel.dms.ui.activity.VisitExhibitionRatingActivity;
import com.viettel.dms.ui.activity.VisitFeedbackActivity;
import com.viettel.dms.ui.activity.VisitPlaceOrderActivity;
import com.viettel.dms.ui.activity.VisitPlaceOrderTBActivity;
import com.viettel.dms.ui.activity.VisitSurveyActivity;
import com.viettel.dms.ui.activity.VisitTakePhotoActivity;
import com.viettel.dms.ui.iview.ICustomerVisitView;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.auth.OAuthSession;
import com.viettel.dmsplus.sdk.models.CustomerFeedbackModel;
import com.viettel.dmsplus.sdk.models.CustomerForVisit;
import com.viettel.dmsplus.sdk.models.ExhibitionRatingDto;
import com.viettel.dmsplus.sdk.models.OrderHolder;
import com.viettel.dmsplus.sdk.models.SurveyAnswerDto;
import com.viettel.dmsplus.sdk.models.UserInfo;
import com.viettel.dmsplus.sdk.models.VisitHolder;
import com.viettel.dmsplus.sdk.models.VisitSimpleInfoDto;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomerVisitFragment extends BaseFragment implements ICustomerVisitView {

    @Bind(R.id.tv_Customer_Name)
    TextView tvCustomerName;
    @Bind(R.id.tv_Customer_Address)
    TextView tvCustomerAddress;
    @Bind(R.id.tv_Customer_Mobile)
    TextView tvCustomerMobile;

    @Bind(R.id.layoutActions)
    View layoutActions;
    @Bind(R.id.layoutStart)
    View layoutStart;
    //For Tablet
    @Nullable
    @Bind(R.id.layout_Address)
    ViewGroup viewAddress;
    @Nullable
    @Bind(R.id.layout_Mobile)
    ViewGroup viewMobile;
    @Nullable
    @Bind(R.id.layout_Phone_Home)
    ViewGroup viewPhoneHome;
    @Nullable
    @Bind(R.id.layout_Email)
    ViewGroup viewEmail;
    @Nullable
    @Bind(R.id.layout_Representative)
    ViewGroup viewRepresentative;
    @Nullable
    @Bind(R.id.tv_Customer_Phone_Home)
    TextView tvCustomerPhoneHome;
    @Nullable
    @Bind(R.id.tv_Customer_Email)
    TextView tvEmail;
    @Nullable
    @Bind(R.id.tv_Representative)
    TextView tvRepresentative;

    ActionsViewHolder actionsViewHolder;
    StartViewHolder startViewHolder;
    MenuItem finishVisit;

    Dialog mProgressDialog;
    CustomerVisitPresenter presenter;

    private CustomerForVisit mCustomerForVisitDto;
    /* Get info from file state*/
    private VisitHolder mVisitHolder;
    private boolean CLOSE_VISIT = false;

    public static final int REQUEST_CLOSE_VISIT = 10002;
    public static final int REQUEST_PLACE_ORDER = 10003;
    public static final int REQUEST_FEEDBACK = 10004;
    public static final int REQUEST_SURVEY = 10005;
    public static final int REQUEST_EXHIBITION = 10006;
    public static final int REQUEST_TAKE_PICTURE = 10007;

    private static String PARAM_CUSTOMER_INFO = "PARAM_CUSTOMER_INFO";
    private static String PARAM_VISIT_HOLDER = "PARAM_VISIT_HOLDER";
    public static String EXTRA_ID = "EXTRA_ID";

    /**
     * Start new visiting session
     */
    public static CustomerVisitFragment newInstance(CustomerForVisit info) {
        CustomerVisitFragment fragment = new CustomerVisitFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_CUSTOMER_INFO, info);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Continue visiting with @visiHolder information
     */
    public static CustomerVisitFragment newInstance(VisitHolder visitHolder) {
        CustomerVisitFragment fragment = new CustomerVisitFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_VISIT_HOLDER, visitHolder);
        fragment.setArguments(args);
        return fragment;
    }

    public CustomerVisitFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCustomerForVisitDto = getArguments().getParcelable(PARAM_CUSTOMER_INFO);
            if (mCustomerForVisitDto == null) {
                mVisitHolder = (VisitHolder) getArguments().getSerializable(PARAM_VISIT_HOLDER);
                mCustomerForVisitDto = mVisitHolder != null ? mVisitHolder.getCustomerInfo() : null;
            }
        }
        presenter = new CustomerVisitPresenter(this, context, mVisitHolder, mCustomerForVisitDto);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_visit, container, false);

        ButterKnife.bind(this, view);
        SetTextUtils.setText(tvCustomerName, mCustomerForVisitDto.getName());
        SetTextUtils.setText(tvCustomerAddress, mCustomerForVisitDto.getAddress());
        SetTextUtils.setText(tvCustomerMobile, mCustomerForVisitDto.getMobile());
        // For Tablet
        SetTextUtils.setText(tvCustomerAddress, viewAddress, mCustomerForVisitDto.getAddress());
        SetTextUtils.setText(tvCustomerMobile, viewMobile, mCustomerForVisitDto.getMobile());
        SetTextUtils.setText(tvCustomerPhoneHome, viewPhoneHome, mCustomerForVisitDto.getPhone());
        SetTextUtils.setText(tvEmail, viewEmail, mCustomerForVisitDto.getEmail());
        SetTextUtils.setText(tvRepresentative, viewRepresentative, mCustomerForVisitDto.getContact());

        actionsViewHolder = new ActionsViewHolder(layoutActions);
        startViewHolder = new StartViewHolder(layoutStart);
        ActionBar actionBar = ((BaseActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        view.post(new Runnable() {
            @Override
            public void run() {
                if (mCustomerForVisitDto.isPlanned()) {
                    if (mCustomerForVisitDto.getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITING) {
                        presenter.startVisit();
                    } else if (mCustomerForVisitDto.getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITED
                            && presenter.getCustomerVisitInfo() == null) {
                        presenter.loadVisitInfo();
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mb_menu_customer_visit, menu);
        finishVisit = menu.findItem(R.id.action_finish_visiting);
        updateMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        if (item.getItemId() == R.id.action_finish_visiting) {
            if (presenter.getCustomerForVisitDto().getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITING) {
                presenter.endVisit();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onBackPressed() {
        if (presenter.getCustomerForVisitDto().isPlanned()
                && presenter.getCustomerForVisitDto().getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITING) {
            DialogUtils.showMessageDialog(context, R.string.notify, R.string.customer_visit_message_cannot_close_when_visiting);
            return true;
        }
        return super.onBackPressed();
    }

    public void updateMenu() {
        if (finishVisit == null) return;
        if (presenter.getCustomerForVisitDto().getVisitStatus() == HardCodeUtil.CustomerVisitStatus.NOT_VISITED || presenter.getCustomerForVisitDto().getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITED) {
            finishVisit.setVisible(false);
        }
        if (presenter.getCustomerForVisitDto().getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITING) {
            finishVisit.setVisible(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PLACE_ORDER && resultCode == Activity.RESULT_OK) {
            OrderHolder dOrderHolder = data.getParcelableExtra(getResources().getBoolean(R.bool.is_tablet) ? VisitPlaceOrderTBActivity.PARAM_ORDER_HOLDER : VisitPlaceOrderActivity.PARAM_ORDER_HOLDER);
            boolean isVanSale = data.getBooleanExtra(getResources().getBoolean(R.bool.is_tablet) ? VisitPlaceOrderTBActivity.PARAM_IS_VAN_SALE : VisitPlaceOrderActivity.PARAM_IS_VAN_SALE, false);
            presenter.setOrderHolder(dOrderHolder);
            presenter.setVanSale(isVanSale);
            actionsViewHolder.updateTextAndIcon();
            presenter.saveStateToFile();
        }
        if (requestCode == REQUEST_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            String photoPath = data.getStringExtra(VisitTakePhotoActivity.PARAM_URL);
            presenter.setmPhotoPath(photoPath);
            actionsViewHolder.updateTextAndIcon();
            presenter.saveStateToFile();
        }
        if (requestCode == REQUEST_FEEDBACK && resultCode == Activity.RESULT_OK) {
            CustomerFeedbackModel[] dFeedback = null;
            Parcelable[] parcelables = data.getParcelableArrayExtra(VisitFeedbackActivity.PARAM_FEEDBACK_VISITING);
            if (parcelables != null) {
                dFeedback = new CustomerFeedbackModel[parcelables.length];
                int i = 0;
                for (Parcelable parcelable : parcelables) {
                    dFeedback[i++] = (CustomerFeedbackModel) parcelable;
                }
            }
            presenter.setFeedback(dFeedback);
            actionsViewHolder.updateTextAndIcon();
            presenter.saveStateToFile();
        }
        if (requestCode == REQUEST_SURVEY && resultCode == Activity.RESULT_OK) {
            SurveyAnswerDto[] dSurveyAnswers = null;
            Parcelable[] parcelables = data.getParcelableArrayExtra(VisitSurveyActivity.PARAM_SURVEY_ANSWERS);

            if (parcelables != null) {
                dSurveyAnswers = new SurveyAnswerDto[parcelables.length];
                int i = 0;
                for (Parcelable parcelable : parcelables) {
                    dSurveyAnswers[i++] = (SurveyAnswerDto) parcelable;
                }
            }
            presenter.setSurveyAnswers(dSurveyAnswers);
            actionsViewHolder.updateTextAndIcon();
            presenter.saveStateToFile();
        }
        if (requestCode == REQUEST_EXHIBITION && resultCode == Activity.RESULT_OK) {
            ExhibitionRatingDto[] dSubmitRates = null;
            Parcelable[] parcelables = data.getParcelableArrayExtra(VisitExhibitionRatingActivity.PARAM_RATED_INFO);

            if (parcelables != null) {
                dSubmitRates = new ExhibitionRatingDto[parcelables.length];
                int i = 0;
                for (Parcelable parcelable : parcelables) {
                    dSubmitRates[i++] = (ExhibitionRatingDto) parcelable;
                }
            }
            presenter.setRateSubmit(dSubmitRates);
            presenter.saveStateToFile();
        }
        if (requestCode == REQUEST_CLOSE_VISIT && resultCode == Activity.RESULT_OK) {
            CLOSE_VISIT = true;
            String closeVisitPhoto = data.getStringExtra(CloseVisitActivity.PARAM_PHOTO_ID);
            presenter.getCustomerForVisitDto().setVisitStatus(HardCodeUtil.CustomerVisitStatus.VISITED);
            presenter.getCustomerForVisitDto().setVisitInfo(new VisitSimpleInfoDto());
            presenter.getCustomerForVisitDto().getVisitInfo().setClosingPhoto(closeVisitPhoto);
            presenter.getCustomerForVisitDto().getVisitInfo().setClosed(true);

            presenter.updateWhenVisitStatusChanged();
            FileUtils.deleteFileObject(context, HardCodeUtil.CustomerVisit.STATE_FILE_NAME);

            // Broadcasting to Customer list
            Intent intent = new Intent(HardCodeUtil.BroadcastReceiver.ACTION_CUSTOMER_VISIT_STATUS_CHANGED);
            intent.putExtra("customerId", presenter.getCustomerForVisitDto().getId());
            intent.putExtra("visitStatus", HardCodeUtil.CustomerVisitStatus.VISITED);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            DialogUtils.showMessageDialog(context, R.string.notify, R.string.customer_visit_message_visit_closed);
        }
    }


    @OnClick(R.id.btn_View_More)
    void doOnClickViewMore() {
        Intent intent = new Intent(context, CustomerInfoActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_ID, presenter.getCustomerForVisitDto().getId());
        startActivity(intent);
    }

    @Override
    public void showProgressLocating() {
        mProgressDialog = DialogUtils.showProgressDialog(context, null, StringUtils.getString(context, R.string.customer_visit_message_locating), true);
    }

    @Override
    public void showProgressLoading() {
        mProgressDialog = DialogUtils.showProgressDialog(context,
                StringUtils.getStringOrNull(context, R.string.message_please_wait),
                StringUtils.getStringOrNull(context, R.string.message_loading_data), true);
    }

    @Override
    public void showDialogResultLocating() {

    }

    @Override
    public void showProgressEndVisit() {
        mProgressDialog = DialogUtils.showProgressDialog(context,
                StringUtils.getString(context, R.string.customer_visit_message_end_visit_title),
                StringUtils.getString(context, R.string.customer_visit_message_end_visit_message_sending), true);
    }

    @Override
    public void showMessageEndVisitSuccess() {
        DialogUtils.showMessageDialog(context,
                R.string.customer_visit_message_end_visit_title,
                R.string.customer_visit_message_end_visit_message_success);
    }

    @Override
    public void showMessageVisitHadEnded() {
        DialogUtils.showMessageDialog(context, R.string.notify, R.string.customer_visit_message_visit_already_ended, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                presenter.loadVisitInfo();
            }
        });
    }

    @Override
    public void showWarningTurnOffGPSWhenLocating() {
        DialogUtils.showMessageDialog(context, R.string.warning, R.string.customer_visit_location_turn_off_manually);
    }

    @Override
    public void showNetworkTaskError(SdkException info) {
        NetworkErrorDialog.processError(context, info);
    }

    @Override
    public void enableStartVisitLayout(boolean b) {
        if (b) layoutStart.setVisibility(View.VISIBLE);
        else layoutStart.setVisibility(View.GONE);
    }

    @Override
    public void updateActionVisitLayout() {
        if (presenter.getCustomerForVisitDto().isPlanned()) {
            if (presenter.getCustomerForVisitDto().getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITED) {
                if (CLOSE_VISIT || (presenter.getCustomerVisitInfo() != null && presenter.getCustomerVisitInfo().isClosed())) {
                    SetTextUtils.setText(actionsViewHolder.tvOrderPrice, getString(R.string.customer_visit_actions_no_order));
                    SetTextUtils.setText(actionsViewHolder.tvTakePhoto, getString(R.string.customer_visit_actions_view_close_visit_photo));
                    actionsViewHolder.tvSurveyNumber.setText(null);
                    actionsViewHolder.tvFeedbackNumber.setText(null);

                    return;
                }
                if (presenter.getCustomerVisitInfo() != null && !presenter.getCustomerVisitInfo().isClosed() && !TextUtils.isEmpty(presenter.getCustomerVisitInfo().getId())) {
                    int status = presenter.getCustomerVisitInfo().getApproveStatus();
                    if (presenter.getCustomerVisitInfo().isHasOrder()) {
                        if (status == HardCodeUtil.OrderStatus.ACCEPTED) {
                            actionsViewHolder.tvOrderPrice.setText(NumberFormatUtils.formatNumber(presenter.getCustomerVisitInfo().getGrandTotal()));
                        } else {
                            actionsViewHolder.tvOrderPrice.setText(HardCodeUtil.getOrderStatus(context, status));
                        }
                    } else {
                        actionsViewHolder.tvOrderPrice.setText(R.string.customer_visit_actions_no_order);
                    }

                    if (presenter.getCustomerVisitInfo().getFeedbacks() != null) {
                        int numFeedback = presenter.getCustomerVisitInfo().getFeedbacks().length;
                        SetTextUtils.setText(actionsViewHolder.tvFeedbackNumber, numFeedback);
                    }

                    if (presenter.getCustomerVisitInfo().getSurveyAnswers() != null) {
                        int numSurvey = presenter.getCustomerVisitInfo().getSurveyAnswers().length;
                        SetTextUtils.setText(actionsViewHolder.tvSurveyNumber, numSurvey);
                    }
                    if (presenter.getCustomerVisitInfo().getPhoto() != null && !presenter.getCustomerVisitInfo().isClosed()) {
                        SetTextUtils.setText(actionsViewHolder.tvTakePhoto, getString(R.string.customer_visit_actions_view_photo));
                    } else {
                        SetTextUtils.setText(actionsViewHolder.tvTakePhoto, getString(R.string.customer_visit_actions_no_photo));
                    }
                }
            } else {
                actionsViewHolder.updateTextAndIcon();
            }

        }
    }


    @Override
    public void startNewActivityForResult(Intent i, int key) {
        startActivityForResult(i, key);
    }

    @Override
    public void startNewActivity(Intent i) {
        startActivity(i);
    }

    @Override
    public void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    public boolean checkEnableGPS() {
        if (!LocationHelper.isGpsProviderEnabled(context)) {
            LocationHelper.showAlertNoLocationService(context);
            return false;
        }
        if (!LocationHelper.isHaveRequiredPermission(context)) {
            LocationHelper.requestPermission(getActivity());
            return false;
        }
        return true;
    }


    class StartViewHolder {
        @Bind(R.id.ll_Close_Visit)
        View layoutCloseVisit;
        @Bind(R.id.ll_Start_Visit)
        View layoutStartVisit;

        public StartViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.ll_Start_Visit)
        void doClickStartVisit() {
            presenter.doClickStartVisit();
        }

        @OnClick(R.id.ll_Close_Visit)
        void doClickCloseVisit() {
            presenter.doClickCloseVisit();
        }

    }

    class ActionsViewHolder {
        @Bind(R.id.tv_Order_Price)
        TextView tvOrderPrice;
        @Bind(R.id.tv_Feedback_Number)
        TextView tvFeedbackNumber;
        @Bind(R.id.tv_Survey_Number)
        TextView tvSurveyNumber;
        @Bind(R.id.tv_take_photo)
        TextView tvTakePhoto;
        @Bind(R.id.img_Action_Place_Order)
        ImageView imgActionPlaceOrder;
        @Bind(R.id.img_Action_Take_Photo)
        ImageView imgActionTakePhoto;
        @Bind(R.id.img_Action_Feedback)
        ImageView imgActionFeedback;
        @Bind(R.id.img_Action_Survey)
        ImageView imgActionSurvey;
        @Bind(R.id.img_Action_Exhibition)
        ImageView imgActionExhibition;
        @Bind(R.id.cv_Purchase)
        CardView cvPurchase;


        private ActionsViewHolder(View view) {
            ButterKnife.bind(this, view);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                cvPurchase.setPreventCornerOverlap(false);
            }
            Picasso.with(context).load(R.drawable.bg_visit_action_place_order).into(imgActionPlaceOrder);
            Picasso.with(context).load(R.drawable.bg_visit_action_feedback).into(imgActionFeedback);
            Picasso.with(context).load(R.drawable.bg_visit_action_survey).into(imgActionSurvey);
            Picasso.with(context).load(R.drawable.bg_visit_action_take_photo).into(imgActionTakePhoto);
            Picasso.with(context).load(R.drawable.bg_visit_action_exhibition).into(imgActionExhibition);

        }

        @OnClick(R.id.rl_purchase)
        void doClickTakeOrder() {
            UserInfo userInfo = OAuthSession.getDefaultSession() != null ? OAuthSession.getDefaultSession().getUserInfo() : null;
            if (userInfo != null && userInfo.isVanSales() && presenter.getCustomerForVisitDto().getVisitStatus() != HardCodeUtil.CustomerVisitStatus.VISITED) {
                DialogUtils.showSingleChoiceDialog(context, R.string.place_order_orders_choose_sale_type, R.array.sale_type, R.string.confirm_yes, R.string.confirm_cancel, presenter.isVanSale() ? 1 : 0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        presenter.doClickTakeOrder(i == 1);
                        return false;
                    }
                });
            } else {
                presenter.doClickTakeOrder(false);
            }

        }

        @OnClick(R.id.rl_take_photo)
        void doClickTakePhoto() {
            if (CLOSE_VISIT) {
                Intent i = new Intent(context, VisitTakePhotoActivity.class);
                i.putExtra(VisitTakePhotoActivity.PARAM_VISITED_PHOTO_ID, presenter.getCustomerForVisitDto().getVisitInfo().getClosingPhoto());
                i.putExtra(VisitTakePhotoActivity.PARAM_IS_VISITED, true);
                startNewActivityForResult(i, CustomerVisitFragment.REQUEST_TAKE_PICTURE);
            } else {
                presenter.doClickTakePhoto();
            }
        }

        @OnClick(R.id.rl_Feedback)
        void doClickFeedBack() {
            presenter.doClickFeedback();
        }

        @OnClick(R.id.rl_Survey)
        void doClickSurvey() {
            presenter.doClickSurvey();
        }

        @OnClick(R.id.ll_Exhibition)
        void doClickRatingExhibition() {
            presenter.doClickRatingExhition();
        }

        public void updateTextAndIcon() {
            if (presenter.getOrderHolder() != null) {
                SetTextUtils.setText(tvOrderPrice, NumberFormatUtils.formatNumber(presenter.getOrderHolder().getTotalAmount()));
            }
            if (StringUtils.isNullOrEmpty(presenter.getmPhotoPath())) {
                tvTakePhoto.setText(R.string.customer_visit_actions_take_photo);
            } else {
                tvTakePhoto.setText(R.string.customer_visit_actions_view_photo);
            }
        }
    }

}
