package com.viettel.dmsplus.sdk;

import android.annotation.SuppressLint;
import android.util.Log;

import com.viettel.dmsplus.sdk.auth.OAuthSession;
import com.viettel.dmsplus.sdk.models.CalculatePromotionRequest;
import com.viettel.dmsplus.sdk.models.CategorySimpleResult;
import com.viettel.dmsplus.sdk.models.CloseVisitRequest;
import com.viettel.dmsplus.sdk.models.CustomerFeedbackResult;
import com.viettel.dmsplus.sdk.models.CustomerListResult;
import com.viettel.dmsplus.sdk.models.CustomerRegisterInfoResult;
import com.viettel.dmsplus.sdk.models.CustomerRegisterModel;
import com.viettel.dmsplus.sdk.models.CustomerSummary;
import com.viettel.dmsplus.sdk.models.CustomerVisitInfo;
import com.viettel.dmsplus.sdk.models.DashboardMonthlyInfo;
import com.viettel.dmsplus.sdk.models.EndVisitingRequest;
import com.viettel.dmsplus.sdk.models.ErrorInfo;
import com.viettel.dmsplus.sdk.models.ExchangeReturnCreateDto;
import com.viettel.dmsplus.sdk.models.ExchangeReturnDto;
import com.viettel.dmsplus.sdk.models.ExchangeReturnSimpleListResult;
import com.viettel.dmsplus.sdk.models.IdDto;
import com.viettel.dmsplus.sdk.models.Location;
import com.viettel.dmsplus.sdk.models.LocationHolder;
import com.viettel.dmsplus.sdk.models.OrderDetailResult;
import com.viettel.dmsplus.sdk.models.OrderPromotion;
import com.viettel.dmsplus.sdk.models.OrderSimpleListResult;
import com.viettel.dmsplus.sdk.models.PlaceOrderProductResult;
import com.viettel.dmsplus.sdk.models.PlaceOrderRequest;
import com.viettel.dmsplus.sdk.models.ProductAndQuantity;
import com.viettel.dmsplus.sdk.models.ProductListResult;
import com.viettel.dmsplus.sdk.models.PromotionListResult;
import com.viettel.dmsplus.sdk.models.RevenueByMonthResult;
import com.viettel.dmsplus.sdk.models.SalesMonthlySummaryResult;
import com.viettel.dmsplus.sdk.models.StringDto;
import com.viettel.dmsplus.sdk.models.SurveyListResult;
import com.viettel.dmsplus.sdk.models.VisitCheckCreateDto;
import com.viettel.dmsplus.sdk.models.VisitCheckDetail;
import com.viettel.dmsplus.sdk.models.VisitCheckList;
import com.viettel.dmsplus.sdk.network.Request;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @author thanh
 * @since 10/11/15
 */
public class MainEndpoint extends Endpoint {

    private static final String LOGTAG = "MainEndpoint";
    private static MainEndpoint DEFAULT_SESSION_ENDPOINT;

    /*return main function with OAuth*/
    public static MainEndpoint get() {
        if (DEFAULT_SESSION_ENDPOINT == null) {
            synchronized (MainEndpoint.class) {
                if (DEFAULT_SESSION_ENDPOINT == null) {
                    OAuthSession defaultSession = OAuthSession.getDefaultSession();
                    if (defaultSession == null) {
                        Log.e(LOGTAG, "No OAuthSession found");
                    } else {
                        DEFAULT_SESSION_ENDPOINT = new MainEndpoint(defaultSession);
                    }
                }
            }
        }
        return DEFAULT_SESSION_ENDPOINT;
    }

    public static void reset() {
        DEFAULT_SESSION_ENDPOINT = null;
    }

    /**
     * Constructs a Endpoint with the provided OAuthSession.
     *
     * @param session authenticated session to use with the Endpoint.
     */
    protected MainEndpoint(OAuthSession session) {
        super(session);
    }

    public String getImageURL(String imgId) {
        // Special url with no role in url
        return getApiBaseUrl() + "/image/" + imgId;
    }

    // add ?sizetype=standard to crop image
    public Request<IdDto> requestUploadImage(String photoPath) {
        String url = getApiBaseUrl() + "/image";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("file", new FileSystemResource(photoPath));

        return post(url, map, IdDto.class, headers);
    }

    public Request<DashboardMonthlyInfo> requestDashboardInfo() {
        String url = getUrlWithPath("dashboard");

        return get(url, DashboardMonthlyInfo.class);
    }

    public Request<RevenueByMonthResult> requestDashboardByCustomer() {
        String url = getUrlWithPath("dashboard/by-customer");

        return get(url, RevenueByMonthResult.class);
    }

    public Request<OrderSimpleListResult> requestDashboardByCustomerDetail(String customerId) {
        String url = getUrlWithPath("dashboard/by-customer/detail?customerId={customerId}");

        return get(url, OrderSimpleListResult.class, customerId);
    }

    public Request<SalesMonthlySummaryResult> requestDashboardByDay() {
        String url = getUrlWithPath("dashboard/by-day");

        return get(url, SalesMonthlySummaryResult.class);
    }

    @SuppressLint("SimpleDateFormat")
    public Request<OrderSimpleListResult> requestDashboardByDayDetail(Date date) {
        String url = getUrlWithPath("dashboard/by-day/detail?date={2015-03-26}");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return get(url, OrderSimpleListResult.class, sdf.format(date));
    }

    public Request<CustomerSummary> requestCustomerInfo(String customerId) {
        String url = getUrlWithPath("customer/{id}/summary");

        return get(url, CustomerSummary.class, customerId);
    }

    public Request<PlaceOrderProductResult> requestPlaceOrderProductList(String customerId) {
        String url = getUrlWithPath("product/for-order?customerId={customerId}");

        return get(url, PlaceOrderProductResult.class, customerId);
    }

    public Request<CustomerListResult> requestCustomerListToday() {
        String url = getUrlWithPath("customer/for-visit?today=true");

        return get(url, CustomerListResult.class);
    }

    public Request<CustomerListResult> requestCustomerListNotScheduleToday() {
        String url = getUrlWithPath("customer/for-visit?today=false");

        return get(url, CustomerListResult.class);
    }

    public Request<CustomerListResult> requestCustomerListAll() {
        String url = getUrlWithPath("customer/for-visit");

        return get(url, CustomerListResult.class);
    }

    /*QUANG: CALCULATE PROMOTION*/
    public Request<OrderPromotion[]> requestCalculatePromotion(

            String customerId, ProductAndQuantity[] selected) {
            String url = getUrlWithPath("order/calculate");

        /*THE REQUEST JSON*/
        CalculatePromotionRequest body = new CalculatePromotionRequest();
        body.setCustomerId(customerId);
        body.setDetails(selected);

        /*post to url calculate*/
        return post(url, body, OrderPromotion[].class);
    }

    public Request<IdDto> requestSendUnplantOrder(
            String customerId, PlaceOrderRequest order) {
        String url = getUrlWithPath("order/unplanned");

        order.setCustomerId(customerId);

        return post(url, order, IdDto.class);
    }

    public Request<PromotionListResult> requestPromotionList() {
        String url = getUrlWithPath("promotion/available");

        return get(url, PromotionListResult.class);
    }

    public Request<IdDto> requestStartVisit(String customerId, Location location) {
        String url = getUrlWithPath("visit/start?customerId={customerId}");

        return post(url, new LocationHolder(location), IdDto.class, customerId);
    }

    public Request<CustomerVisitInfo> requestEndVisit(String visitId, EndVisitingRequest body) {
        String url = getUrlWithPath("visit/{visitId}/end");

        return put(url, body, CustomerVisitInfo.class, visitId);
    }

    public Request<IdDto> requestCloseVisit(String customerId, String photoId, Location location) {
        String url = getUrlWithPath("visit/close?customerId={customerId}");

        CloseVisitRequest body = new CloseVisitRequest();
        body.setLocation(location);
        body.setClosingPhotoId(photoId);

        return post(url, body, IdDto.class, customerId);
    }

    /**
     * Yêu cầu thông tin ghé thăm trong ngày hôm nay của người dùng đang đăng nhập với một khách
     * hàng có <b>id</b> được truyền vào. Trả về lỗi nếu khách hàng chưa được ghé thăm!
     */
    public Request<CustomerVisitInfo> requestVisitInfo(String customerId) {
        String url = getUrlWithPath("visit/today?customerId={customerId}");

        return get(url, CustomerVisitInfo.class, customerId);
    }

    /**
     * Yêu cầu danh sách các đơn hàng trong ngày hôm nay của khách hàng có <b>id</b> được truyền vào
     */
    public Request<OrderSimpleListResult> requestOrderTodayByCustomer(String customerId) {
        String url = getUrlWithPath("order/today?customerId={customerId}");

        return get(url, OrderSimpleListResult.class, customerId);
    }

    /**
     * Yêu cầu thông tin chi tiết đơn hàng có <b>id</b> được truyền vào
     */
    public Request<OrderDetailResult> requestOrderDetail(String orderId) {
        String url = getUrlWithPath("order/{orderId}");

        return get(url, OrderDetailResult.class, orderId);
    }

    /**
     * Yêu cầu danh sách các cuộc khảo sát dành cho khách hàng có <b>id</b> được truyền vào
     */
    public Request<SurveyListResult> requestSurveyList(String customerId) {
        String url = getUrlWithPath("survey/available?customerId={customerId}");

        return get(url, SurveyListResult.class, customerId);
    }


    public Request<CategorySimpleResult> requestCustomerType() {
        String url = getUrlWithPath("customertype/all");

        return get(url, CategorySimpleResult.class);
    }

    public Request<CategorySimpleResult> requestListDistrict() {
        String url = getUrlWithPath("area/all");

        return get(url, CategorySimpleResult.class);
    }

    public Request<IdDto> postRegisterCustomer(CustomerRegisterModel customerRegisterModel) {
        String url = getUrlWithPath("customer/register");

        return post(url, customerRegisterModel, IdDto.class);
    }

    public Request<ErrorInfo> updateMobilePhone(String customerId, String mobile) {
        String url = getUrlWithPath("customer/{id}/mobile");

        return put(url, new StringDto(mobile), ErrorInfo.class, customerId);
    }

    public Request<ErrorInfo> updateHomePhone(String customerId, String phone) {
        String url = getUrlWithPath("customer/{id}/phone");

        return put(url, new StringDto(phone), ErrorInfo.class, customerId);
    }

    public Request<ErrorInfo> updateLocation(String customerId, double latitude, double longtitude) {
        String url = getUrlWithPath("customer/{id}/location");
        HashMap<String, Object> content = new HashMap<String, Object>();
        content.put("longitude", longtitude);
        content.put("latitude", latitude);

        return put(url, content, ErrorInfo.class, customerId);
    }

    public Request<CustomerFeedbackResult> requestFeedbackList(String customerId) {
        String url = getUrlWithPath("feedback?customerId={id}");

        return get(url, CustomerFeedbackResult.class, customerId);
    }

    public Request<OrderSimpleListResult> requestOrderToday() {
        String url = getUrlWithPath("order/today");

        return get(url, OrderSimpleListResult.class);
    }

    public Request<ExchangeReturnSimpleListResult> requestExchangeToday() {
        String url = getUrlWithPath("exchange-product/today");

        return get(url, ExchangeReturnSimpleListResult.class);
    }

    public Request<ExchangeReturnSimpleListResult> requestReturnToday() {
        String url = getUrlWithPath("return-product/today");

        return get(url, ExchangeReturnSimpleListResult.class);
    }

    public Request<IdDto> requestSendExchangeProduct(ExchangeReturnCreateDto dto) {
        String url = getUrlWithPath("exchange-product");
        return post(url, dto, IdDto.class);
    }

    public Request<IdDto> requestSendReturnProduct(ExchangeReturnCreateDto dto) {
        String url = getUrlWithPath("return-product");
        return post(url, dto, IdDto.class);
    }

    public Request<ExchangeReturnDto> requestExchangeDetail(String id) {
        String url = getUrlWithPath("exchange-product/{id}");
        return get(url, ExchangeReturnDto.class, id);
    }

    public Request<ExchangeReturnDto> requestReturnDetail(String id) {
        String url = getUrlWithPath("return-product/{id}");
        return get(url, ExchangeReturnDto.class, id);
    }

    public Request<CustomerRegisterInfoResult> requestCustomerRegisterList(int page, int size, String query) {
        String url = getUrlWithPath("customer/register?page={page}&size={size}&q={q}");

        return get(url, CustomerRegisterInfoResult.class, page, size, query);
    }

    public Request<ProductListResult> requestProductList(int page, int size, String query) {
        String url = getUrlWithPath("product?q={q}&page={page}&size={size}");

        return get(url, ProductListResult.class, query, page, size);
    }


    /***
     * STORE CHECKER
     ***/
    public Request<CategorySimpleResult> requestDeliveryMenList() {
        String url = getUrlWithPath("delivery-man/all");

        return get(url, CategorySimpleResult.class);
    }

    public Request<VisitCheckList> requestVisitCheckList() {
        String url = getUrlWithPath("visit-check");

        return get(url, VisitCheckList.class);
    }

    public Request<VisitCheckDetail> requestVisitCheckInfo(String visitCheckId) {
        String url = getUrlWithPath("visit-check/{visitCheckId}");

        return get(url, VisitCheckDetail.class, visitCheckId);
    }

    public Request<ErrorInfo> requestSendCheckResult(String visitCheckId, VisitCheckCreateDto dto) {
        String url = getUrlWithPath("visit-check/{visitCheckId}");

        return put(url, dto, ErrorInfo.class, visitCheckId);
    }

    private String getUrlWithPath(String path) {
        return String.format("%s/%s/%s", getApiBaseUrl(), mSession.getRole().getUrlPrefix(), path);
    }

}
