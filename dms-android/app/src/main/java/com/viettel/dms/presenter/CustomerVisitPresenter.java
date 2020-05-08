package com.viettel.dms.presenter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.viettel.dms.BuildConfig;
import com.viettel.dms.R;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.FileUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.location.LocationDistanceMatcherListener;
import com.viettel.dms.helper.location.LocationHelper;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.ui.activity.CloseVisitActivity;
import com.viettel.dms.ui.activity.ReviewActivity;
import com.viettel.dms.ui.activity.VisitExhibitionRatingActivity;
import com.viettel.dms.ui.activity.VisitFeedbackActivity;
import com.viettel.dms.ui.activity.VisitPlaceOrderActivity;
import com.viettel.dms.ui.activity.VisitPlaceOrderTBActivity;
import com.viettel.dms.ui.activity.VisitSurveyActivity;
import com.viettel.dms.ui.activity.VisitTakePhotoActivity;
import com.viettel.dms.ui.fragment.CustomerVisitFragment;
import com.viettel.dms.ui.iview.ICustomerVisitView;
import com.viettel.dmsplus.sdk.ErrorType;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.auth.OAuthSession;
import com.viettel.dmsplus.sdk.models.CustomerFeedbackModel;
import com.viettel.dmsplus.sdk.models.CustomerForVisit;
import com.viettel.dmsplus.sdk.models.CustomerVisitInfo;
import com.viettel.dmsplus.sdk.models.EndVisitingRequest;
import com.viettel.dmsplus.sdk.models.ExhibitionRatingDto;
import com.viettel.dmsplus.sdk.models.IdDto;
import com.viettel.dmsplus.sdk.models.OrderHolder;
import com.viettel.dmsplus.sdk.models.PlaceOrderRequest;
import com.viettel.dmsplus.sdk.models.SurveyAnswerDto;
import com.viettel.dmsplus.sdk.models.UserInfo;
import com.viettel.dmsplus.sdk.models.VisitHolder;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

import java.io.File;

/**
 * @author PHAMHUNG
 * @since 8/26/2015
 */
public class CustomerVisitPresenter extends BasePresenter {
    ICustomerVisitView iView;

    private Location mLocation = null;
    private Context context;

    // Visit info in case of customer already visited
    private CustomerVisitInfo mCustomerVisitInfo;

    // Visit info in case of visiting customer
    private VisitHolder mVisitHolder;

    private CustomerForVisit mCustomerForVisitDto;
    private String visitID;
    private OrderHolder mOrderHolder;
    private CustomerFeedbackModel[] mFeedback;
    private SurveyAnswerDto[] mSurveyAnswers;
    private ExhibitionRatingDto[] mRateSubmit;
    private String mPhotoPath;
    private String photoId;
    private boolean vanSale;

    private SdkAsyncTask<?> networkTask;

    public CustomerVisitPresenter(ICustomerVisitView i, Context ct, VisitHolder v, CustomerForVisit info) {
        iView = i;
        context = ct;
        mVisitHolder = v;
        mCustomerForVisitDto = info;

        if (mVisitHolder != null) {
            mCustomerForVisitDto = mVisitHolder.getCustomerInfo();
            visitID = mVisitHolder.getVisitId();
            mOrderHolder = mVisitHolder.getOrderHolder();
            mFeedback = mVisitHolder.getFeedback();
            mSurveyAnswers = mVisitHolder.getSurveyAnswers();
            mRateSubmit = mVisitHolder.getRateSubmit();
            mPhotoPath = mVisitHolder.getPhotoPath();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (networkTask != null) {
            networkTask.cancel(true);
            networkTask = null;
        }
    }

    /**
     * Locating my location and determine my location is matched to customer's location or not.
     * User must turn on GPS service to use function
     */
    public void requestLocationToContinue(final LocationCallback callback) {
        boolean okGPS = iView.checkEnableGPS();
        if (!okGPS) return;
        iView.showProgressLocating();

        LocationDistanceMatcherListener locationListener = new LocationDistanceMatcherListener() {
            @Override
            public void onLocationMatchSuccess(Location location) {
                iView.dismissProgress();
                mLocation = location;
                callback.onLocateSuccess();
            }

            @Override
            public void onLocationMatchFail(Location location) {
                iView.dismissProgress();
                mLocation = location;
                Dialog.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            callback.onContinueAnyway();
                        } else if (which == DialogInterface.BUTTON_NEUTRAL) {
                            requestLocationToContinue(callback);
                        } else if (which == DialogInterface.BUTTON_POSITIVE) {
                            mLocation = null;
                        }
                    }
                };
                AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(context);
                builder.setTitle(R.string.warning);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.confirm_cancel, clickListener);
                builder.setNegativeButton(R.string.confirm_continue, clickListener);
                builder.setNeutralButton(R.string.confirm_retry, clickListener);
                if (location == null) {
                    builder.setMessage(R.string.customer_visit_message_cannot_locate);
                } else {
                    builder.setMessage(R.string.customer_visit_message_location_not_accuracy);
                }
                Dialog alertDialog = builder.create();
                alertDialog.show();
            }

            @Override
            public void onTurnOffLocation() {
                iView.dismissProgress();
                iView.showWarningTurnOffGPSWhenLocating();

            }
        };
        UserInfo userInfo = OAuthSession.getDefaultSession().getUserInfo();

        if (BuildConfig.DEBUG) {
            float maxAllowedDistance = userInfo.getVisitDistanceKPI() != 0 ? (float) userInfo.getVisitDistanceKPI() * 1000 : HardCodeUtil.CustomerVisit.MAX_ALLOWED_DISTANCE_DEBUG;
            LocationHelper.matcherWithLocation(context, maxAllowedDistance, HardCodeUtil.CustomerVisit.MAX_ALLOWED_TIME_DEBUG,
                    mCustomerForVisitDto.getLocation().getLatitude(), mCustomerForVisitDto.getLocation().getLongitude(), locationListener);
        } else {
            float maxAllowedDistance = userInfo.getVisitDistanceKPI() != 0 ? (float) userInfo.getVisitDistanceKPI() * 1000 : HardCodeUtil.CustomerVisit.MAX_ALLOWED_DISTANCE;
            LocationHelper.matcherWithLocation(context, maxAllowedDistance, HardCodeUtil.CustomerVisit.MAX_ALLOWED_TIME,
                    mCustomerForVisitDto.getLocation().getLatitude(), mCustomerForVisitDto.getLocation().getLongitude(), locationListener);
        }

    }

    public void doClickStartVisit() {
        if (mLocation == null) {
            requestLocationToContinue(startVisitBehavior);
        } else {
            startVisit();
        }
    }

    public void doClickCloseVisit() {
        if (mLocation == null) {
            requestLocationToContinue(closeVisitBehavior);
        } else {
            startCloseVisit();
        }
    }

    public void startVisit() {
        iView.showProgressLoading();
        RequestCompleteCallback<IdDto> startVisitCallback = new RequestCompleteCallback<IdDto>() {
            @Override
            public void onSuccess(IdDto data) {
                visitID = data.getId();
                // Broadcasting to Customer list
                Intent intent = new Intent(HardCodeUtil.BroadcastReceiver.ACTION_CUSTOMER_VISIT_STATUS_CHANGED);
                intent.putExtra("customerId", mCustomerForVisitDto.getId());
                intent.putExtra("visitStatus", HardCodeUtil.CustomerVisitStatus.VISITING);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                mCustomerForVisitDto.setVisitStatus(HardCodeUtil.CustomerVisitStatus.VISITING);
                saveStateToFile();
                updateWhenVisitStatusChanged();
            }

            @Override
            public void onError(SdkException info) {
                NetworkErrorDialog.processError(context, info);
            }

            @Override
            public void onFinish(boolean canceled) {
                iView.dismissProgress();
                networkTask = null;
            }
        };
        networkTask = MainEndpoint
                .get()
                .requestStartVisit(mCustomerForVisitDto.getId(),
                        mLocation == null ? null : new com.viettel.dmsplus.sdk.models.Location(mLocation.getLatitude(), mLocation.getLongitude())
                )
                .executeAsync(startVisitCallback);
    }

    public void sendVisitImage() {
        networkTask = MainEndpoint.get().requestUploadImage(mPhotoPath).executeAsync(
                new RequestCompleteCallback<IdDto>() {

                    @Override
                    public void onSuccess(IdDto data) {
                        photoId = data.getId();
                        collectInfoThenEndVisit();
                    }

                    @Override
                    public void onError(SdkException info) {
                        iView.dismissProgress();
                        DialogUtils.showConfirmDialog(context,
                                R.string.notify,
                                R.string.customer_visit_message_send_visit_photo_fail,
                                R.string.confirm_yes,
                                R.string.confirm_no, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which != DialogInterface.BUTTON_POSITIVE) {
                                            return;
                                        }
                                        iView.showProgressEndVisit();
                                        collectInfoThenEndVisit();
                                    }
                                });
                    }

                    @Override
                    public void onFinish(boolean canceled) {
                        networkTask = null;
                    }
                }
        );
    }

    public void endVisit() {

        DialogUtils.showConfirmDialog(context,
                R.string.notify,
                R.string.customer_visit_message_end_visit_confirm,
                R.string.confirm_yes,
                R.string.confirm_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which != DialogInterface.BUTTON_POSITIVE) {
                            return;
                        }
                        if (mCustomerForVisitDto.getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITING) {
                            iView.showProgressEndVisit();
                            if (mPhotoPath != null) {
                                sendVisitImage();
                            } else {
                                collectInfoThenEndVisit();
                            }
                        }
                    }
                });
    }

    void collectInfoThenEndVisit() {
        final RequestCompleteCallback<CustomerVisitInfo> endVisitCallback = new RequestCompleteCallback<CustomerVisitInfo>() {
            @Override
            public void onSuccess(CustomerVisitInfo data) {
                mCustomerVisitInfo = data;
                removeStateFile();
                if (mPhotoPath != null) {
                    new File(mPhotoPath).delete();
                }
                iView.dismissProgress();
                iView.showMessageEndVisitSuccess();
                // Broadcasting to Customer list
                Intent intent = new Intent(HardCodeUtil.BroadcastReceiver.ACTION_CUSTOMER_VISIT_STATUS_CHANGED);
                intent.putExtra("customerId", mCustomerForVisitDto.getId());
                intent.putExtra("visitStatus", HardCodeUtil.CustomerVisitStatus.VISITED);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                mCustomerForVisitDto.setVisitStatus(HardCodeUtil.CustomerVisitStatus.VISITED);
                updateWhenVisitStatusChanged();
            }

            @Override
            public void onError(SdkException info) {
                iView.dismissProgress();
                if (info.getErrorType() == ErrorType.BUSINESS_ERROR) {
                    if (HardCodeUtil.CustomerVisit.ERROR_VISIT_WAS_ENDED.equals(info.getError().getMessage())) {
                        // Remove state file
                        removeStateFile();
                        // Broadcasting to Customer list
                        Intent intent = new Intent(HardCodeUtil.BroadcastReceiver.ACTION_CUSTOMER_VISIT_STATUS_CHANGED);
                        intent.putExtra("customerId", mCustomerForVisitDto.getId());
                        intent.putExtra("visitStatus", HardCodeUtil.CustomerVisitStatus.VISITED);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        mCustomerForVisitDto.setVisitStatus(HardCodeUtil.CustomerVisitStatus.VISITED);
                        iView.showMessageVisitHadEnded();
                        return;
                    }
                }
                NetworkErrorDialog.processError(context, info);
            }

            @Override
            public void onFinish(boolean canceled) {
                networkTask = null;
            }
        };
        if (mCustomerForVisitDto.getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITING) {
            EndVisitingRequest body = new EndVisitingRequest();
            if (mOrderHolder != null) {
                PlaceOrderRequest placeOrderRequest = VisitPlaceOrderActivity.buildPurchaseOrder(mOrderHolder);
                placeOrderRequest.setVanSales(isVanSale());
                body.setOrder(placeOrderRequest);
            }

            if (mFeedback != null && mFeedback.length > 0) {
                String[] feedbacks = new String[mFeedback.length];
                int i = 0;
                for (CustomerFeedbackModel item : mFeedback) {
                    feedbacks[i++] = item.getMessage();
                }
                body.setFeedbacks(feedbacks);
            }
            if (mRateSubmit != null && mRateSubmit.length > 0) {
                body.setExhibitionRatings(mRateSubmit);
            }

            if (mSurveyAnswers != null) {
                body.setSurveyAnswers(mSurveyAnswers);
            } else {
                body.setSurveyAnswers(new SurveyAnswerDto[0]);
            }
            if (!StringUtils.isNullOrEmpty(photoId)) {
                body.setPhoto(photoId);
            }

            networkTask = MainEndpoint.get().requestEndVisit(visitID, body).executeAsync(endVisitCallback);
        }
    }

    void startCloseVisit() {
        Intent i = new Intent(context, CloseVisitActivity.class);
        Bundle b = new Bundle();
        b.putParcelable(CloseVisitActivity.PARAM_CUSTOMER_INFO, mCustomerForVisitDto);
        com.viettel.dmsplus.sdk.models.Location loc = mLocation == null ? null : new com.viettel.dmsplus.sdk.models.Location(
                mLocation.getLatitude(), mLocation.getLongitude());
        b.putParcelable(CloseVisitActivity.PARAM_LOCATION, loc);
        i.putExtras(b);
        iView.startNewActivityForResult(i, CustomerVisitFragment.REQUEST_CLOSE_VISIT);
    }

    public void loadVisitInfo() {
        iView.showProgressLoading();
        RequestCompleteCallback<CustomerVisitInfo> loadVisitInfoCallback = new RequestCompleteCallback<CustomerVisitInfo>() {
            @Override
            public void onSuccess(CustomerVisitInfo data) {
                visitID = data.getId();
                mCustomerVisitInfo = data;
                updateWhenVisitStatusChanged();
            }

            @Override
            public void onError(SdkException info) {
                NetworkErrorDialog.processError(context, info);
            }

            @Override
            public void onFinish(boolean canceled) {
                iView.dismissProgress();
                networkTask = null;
            }
        };
        networkTask = MainEndpoint.get().requestVisitInfo(mCustomerForVisitDto.getId()).executeAsync(loadVisitInfoCallback);
    }

    public void saveStateToFile() {
        FileUtils.saveObject(context, HardCodeUtil.CustomerVisit.STATE_FILE_NAME,
                new VisitHolder(mCustomerForVisitDto, visitID, mOrderHolder, mFeedback, mSurveyAnswers, mRateSubmit, mPhotoPath, vanSale));
    }

    public void removeStateFile() {
        FileUtils.deleteFileObject(context, HardCodeUtil.CustomerVisit.STATE_FILE_NAME);
    }

    public void updateWhenVisitStatusChanged() {
        if (mCustomerForVisitDto.isPlanned()) {
            if (mCustomerForVisitDto.getVisitStatus() == HardCodeUtil.CustomerVisitStatus.NOT_VISITED) {
                iView.enableStartVisitLayout(true);
            } else {
                iView.enableStartVisitLayout(false);
                iView.updateActionVisitLayout();
            }
        }
        iView.updateMenu();
    }

    public void doClickTakeOrder(boolean isVanSale) {
        if (mCustomerForVisitDto.isPlanned()
                && mCustomerForVisitDto.getVisitStatus() == HardCodeUtil.CustomerVisitStatus.NOT_VISITED) {
            return;
        }
        if (mCustomerForVisitDto.isPlanned())
            if (mCustomerForVisitDto.getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITING) {
                if (context.getResources().getBoolean(R.bool.is_tablet)) {
                    Intent i = new Intent(context, VisitPlaceOrderTBActivity.class);
                    Bundle b = new Bundle();
                    b.putParcelable(VisitPlaceOrderTBActivity.PARAM_CUSTOMER_INFO, mCustomerForVisitDto);
                    b.putString(VisitPlaceOrderTBActivity.PARAM_VISIT_ID, visitID);
                    b.putParcelable(VisitPlaceOrderTBActivity.PARAM_ORDER_HOLDER, mOrderHolder);
                    b.putBoolean(VisitPlaceOrderTBActivity.PARAM_IS_VAN_SALE, isVanSale);
                    i.putExtras(b);
                    iView.startNewActivityForResult(i, CustomerVisitFragment.REQUEST_PLACE_ORDER);
                } else {
                    Intent i = new Intent(context, VisitPlaceOrderActivity.class);
                    Bundle b = new Bundle();
                    b.putParcelable(VisitPlaceOrderActivity.PARAM_CUSTOMER_INFO, mCustomerForVisitDto);
                    b.putString(VisitPlaceOrderActivity.PARAM_VISIT_ID, visitID);
                    b.putParcelable(VisitPlaceOrderActivity.PARAM_ORDER_HOLDER, mOrderHolder);
                    b.putBoolean(VisitPlaceOrderActivity.PARAM_IS_VAN_SALE, isVanSale);
                    i.putExtras(b);
                    iView.startNewActivityForResult(i, CustomerVisitFragment.REQUEST_PLACE_ORDER);
                }
                return;
            } else {
                if (mCustomerVisitInfo != null) {
                    if (!mCustomerVisitInfo.isHasOrder()) {
                        DialogUtils.showMessageDialog(context, R.string.notify, R.string.customer_visit_message_visit_have_no_order);
                    } else {
                        Intent i = new Intent(context, ReviewActivity.class);
                        Bundle b = new Bundle();
                        b.putString(ReviewActivity.PARAM_REVIEW_ORDER_ID, mCustomerVisitInfo.getId());
                        i.putExtras(b);
                        iView.startNewActivity(i);
                    }
                }
                return;
            }
    }

    public void doClickTakePhoto() {
        if (mCustomerForVisitDto.getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITED) {
            if (mCustomerVisitInfo == null
                    || (!mCustomerVisitInfo.isClosed()
                    && mCustomerVisitInfo.getPhoto() == null)) {
                DialogUtils.showMessageDialog(context, R.string.notify, R.string.customer_visit_message_visit_have_no_photo);
                return;
            }
        }

        Intent i = new Intent(context, VisitTakePhotoActivity.class);
        i.putExtra(VisitTakePhotoActivity.PARAM_URL, mPhotoPath);
        i.putExtra(VisitTakePhotoActivity.PARAM_VISITED_PHOTO_ID, mCustomerVisitInfo == null ? null : mCustomerVisitInfo.getPhoto());
        i.putExtra(VisitTakePhotoActivity.PARAM_IS_VISITED, mCustomerForVisitDto.getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITED);
        iView.startNewActivityForResult(i, CustomerVisitFragment.REQUEST_TAKE_PICTURE);
    }

    public void doClickFeedback() {
        if (mCustomerForVisitDto.isPlanned()) {
            if (mCustomerForVisitDto.getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITING) {
                Intent i = new Intent(context, VisitFeedbackActivity.class);
                Bundle b = new Bundle();
                b.putString(VisitFeedbackActivity.PARAM_CUSTOMER_INFO, mCustomerForVisitDto.getId());
                b.putString(VisitFeedbackActivity.PARAM_CUSTOMER_NAME, mCustomerForVisitDto.getName());
                // Trong cung lan visit nhung da tao feedback truoc do
                b.putParcelableArray(VisitFeedbackActivity.PARAM_FEEDBACK_VISITING, mFeedback);
                b.putBoolean(VisitFeedbackActivity.PARAM_VISITED, false);
                i.putExtras(b);
                iView.startNewActivityForResult(i, CustomerVisitFragment.REQUEST_FEEDBACK);
                return;
            } else if (mCustomerForVisitDto.getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITED) {
                if (mCustomerVisitInfo == null || mCustomerVisitInfo.getFeedbacks() == null || mCustomerVisitInfo.getFeedbacks().length == 0) {
                    DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            if (which == DialogInterface.BUTTON_NEGATIVE) {
                                return;
                            }
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                Intent i = new Intent(context, VisitFeedbackActivity.class);
                                Bundle b = new Bundle();
                                b.putString(VisitFeedbackActivity.PARAM_CUSTOMER_INFO, mCustomerForVisitDto.getId());
                                b.putString(VisitFeedbackActivity.PARAM_CUSTOMER_NAME, mCustomerForVisitDto.getName());
                                b.putBoolean(VisitFeedbackActivity.PARAM_VISITED, true);
                                i.putExtras(b);
                                iView.startNewActivity(i);
                            }
                        }
                    };
                    DialogUtils.showConfirmDialog(context, R.string.notify, R.string.customer_visit_message_visit_have_no_feedback, R.string.confirm_continue, R.string.confirm_cancel, clickListener);
                } else {
                    Intent i = new Intent(context, VisitFeedbackActivity.class);
                    Bundle b = new Bundle();
                    b.putString(VisitFeedbackActivity.PARAM_CUSTOMER_INFO, mCustomerForVisitDto.getId());
                    b.putString(VisitFeedbackActivity.PARAM_CUSTOMER_NAME, mCustomerForVisitDto.getName());
                    b.putBoolean(VisitFeedbackActivity.PARAM_VISITED, true);
                    i.putExtras(b);
                    iView.startNewActivity(i);
                }
            }
        } else {

        }
    }

    public void doClickSurvey() {
        if (mCustomerForVisitDto.isPlanned()) {
            if (mCustomerForVisitDto.getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITING) {
                Intent i = new Intent(context, VisitSurveyActivity.class);
                Bundle b = new Bundle();
                b.putString(VisitSurveyActivity.PARAM_VISIT_ID, visitID);
                b.putString(VisitSurveyActivity.PARAM_CUSTOMER_NAME, mCustomerForVisitDto.getName());
                b.putString(VisitSurveyActivity.PARAM_CUSTOMER_ID, mCustomerForVisitDto.getId());
                b.putParcelableArray(VisitSurveyActivity.PARAM_SURVEY_ANSWERS, mSurveyAnswers);
                i.putExtras(b);
                iView.startNewActivityForResult(i, CustomerVisitFragment.REQUEST_SURVEY);
            } else {
                if (mCustomerVisitInfo == null || mCustomerVisitInfo.getSurveyAnswers() == null || mCustomerVisitInfo.getSurveyAnswers().length == 0) {
                    DialogUtils.showMessageDialog(context, R.string.notify, R.string.customer_visit_message_visit_no_input_opponent_info);
                } else {
                    Intent i = new Intent(context, VisitSurveyActivity.class);
                    Bundle b = new Bundle();
                    b.putString(VisitSurveyActivity.PARAM_VISIT_ID, visitID);
                    b.putString(VisitSurveyActivity.PARAM_CUSTOMER_ID, mCustomerForVisitDto.getId());
                    b.putParcelableArray(VisitSurveyActivity.PARAM_SURVEY_QUESTION, mCustomerVisitInfo.getSurveys());
                    b.putParcelableArray(VisitSurveyActivity.PARAM_SURVEY_ANSWERS, mCustomerVisitInfo.getSurveyAnswers());
                    b.putBoolean(VisitSurveyActivity.PARAM_VISIT_ENDED, true);
                    i.putExtras(b);
                    iView.startNewActivityForResult(i, CustomerVisitFragment.REQUEST_SURVEY);
                }
            }
        }
    }

    public void doClickRatingExhition() {
        if (mCustomerForVisitDto.isPlanned()) {
            if (mCustomerForVisitDto.getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITING) {
                Intent i = new Intent(context, VisitExhibitionRatingActivity.class);
                Bundle b = new Bundle();
                b.putBoolean(VisitExhibitionRatingActivity.PARAM_VISIT, false);
                b.putString(VisitExhibitionRatingActivity.PARAM_CUSTOMER_INFO, mCustomerForVisitDto.getId());
                b.putParcelableArray(VisitExhibitionRatingActivity.PARAM_RATING_INFO, mRateSubmit);
                b.putString(VisitExhibitionRatingActivity.PARAM_VISIT_ID, visitID);
                i.putExtras(b);
                iView.startNewActivityForResult(i, CustomerVisitFragment.REQUEST_EXHIBITION);
            } else if (mCustomerForVisitDto.getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITED) {
                if (mCustomerVisitInfo == null || mCustomerVisitInfo.getExhibitionRatings() == null || mCustomerVisitInfo.getExhibitionRatings().length == 0) {
                    DialogUtils.showMessageDialog(context, R.string.notify, R.string.customer_visit_message_visit_no_rate_exhibition);
                } else {
                    Intent i = new Intent(context, VisitExhibitionRatingActivity.class);
                    Bundle b = new Bundle();
                    b.putBoolean(VisitExhibitionRatingActivity.PARAM_VISIT, true);
                    b.putString(VisitExhibitionRatingActivity.PARAM_CUSTOMER_INFO, mCustomerForVisitDto.getId());
                    b.putParcelableArray(VisitExhibitionRatingActivity.PARAM_VISITED_DTO, mCustomerVisitInfo.getExhibitions());
                    b.putParcelableArray(VisitExhibitionRatingActivity.PARAM_RATED_INFO, mCustomerVisitInfo.getExhibitionRatings());
                    b.putString(VisitExhibitionRatingActivity.PARAM_VISIT_ID, visitID);
                    i.putExtras(b);
                    iView.startNewActivity(i);
                }
            }
        }
    }

    private LocationCallback startVisitBehavior = new LocationCallback() {
        @Override
        public void onLocateSuccess() {
            startVisit();
        }

        @Override
        public void onContinueAnyway() {
            startVisit();
        }
    };

    private LocationCallback closeVisitBehavior = new LocationCallback() {
        @Override
        public void onLocateSuccess() {
            startCloseVisit();
        }

        @Override
        public void onContinueAnyway() {
            startCloseVisit();
        }
    };

    //region getter/setter
    public OrderHolder getOrderHolder() {
        return mOrderHolder;
    }

    public void setOrderHolder(OrderHolder mOrderHolder) {
        this.mOrderHolder = mOrderHolder;
    }

    public CustomerFeedbackModel[] getFeedback() {
        return mFeedback;
    }

    public void setFeedback(CustomerFeedbackModel[] mFeedback) {
        this.mFeedback = mFeedback;
    }

    public SurveyAnswerDto[] getSurveyAnswers() {
        return mSurveyAnswers;
    }

    public void setSurveyAnswers(SurveyAnswerDto[] mSurveyAnswers) {
        this.mSurveyAnswers = mSurveyAnswers;
    }

    public ExhibitionRatingDto[] getRateSubmit() {
        return mRateSubmit;
    }

    public void setRateSubmit(ExhibitionRatingDto[] mRateSubmit) {
        this.mRateSubmit = mRateSubmit;
    }

    public CustomerForVisit getCustomerForVisitDto() {
        return mCustomerForVisitDto;
    }

    public void setCustomerForVisitDto(CustomerForVisit mCustomerForVisitDto) {
        this.mCustomerForVisitDto = mCustomerForVisitDto;
    }

    public CustomerVisitInfo getCustomerVisitInfo() {
        return mCustomerVisitInfo;
    }

    public void setCustomerVisitInfo(CustomerVisitInfo mCustomerVisitInfo) {
        this.mCustomerVisitInfo = mCustomerVisitInfo;
    }

    public String getmPhotoPath() {
        return mPhotoPath;
    }

    public void setmPhotoPath(String mPhotoPath) {
        this.mPhotoPath = mPhotoPath;
    }

    public boolean isVanSale() {
        return vanSale;
    }

    public void setVanSale(boolean vanSale) {
        this.vanSale = vanSale;
    }

    //endregion
    public interface LocationCallback {
        void onLocateSuccess();

        void onContinueAnyway();
    }
}
