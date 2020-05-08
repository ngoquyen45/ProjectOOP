package com.viettel.dms.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.NumberFormatUtils;
import com.viettel.dms.ui.iview.IExchangeReturnChooseProductView;
import com.viettel.dms.ui.iview.IPlaceOrderProductListView;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.auth.OAuthSession;
import com.viettel.dmsplus.sdk.models.CustomerForVisit;
import com.viettel.dmsplus.sdk.models.ExchangeReturnCreateDto;
import com.viettel.dmsplus.sdk.models.ExchangeReturnSimpleDto;
import com.viettel.dmsplus.sdk.models.IdDto;
import com.viettel.dmsplus.sdk.models.OrderHolder;
import com.viettel.dmsplus.sdk.models.OrderPromotion;
import com.viettel.dmsplus.sdk.models.PlaceOrderProduct;
import com.viettel.dmsplus.sdk.models.PlaceOrderProductResult;
import com.viettel.dmsplus.sdk.models.ProductAndQuantity;
import com.viettel.dmsplus.sdk.models.UserInfo;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PHAMHUNG on 2/2/2016.
 */
public class ExchangeReturnChooseProductPresenter {
    List<PlaceOrderProduct> productList = new ArrayList<>();
    List<PlaceOrderProduct> selectedList = new ArrayList<>();
    BigDecimal total = BigDecimal.ZERO;
    ExchangeReturnCreateDto dto = null;

    private PlaceOrderProductResult productsResult = null;

    CustomerForVisit mCustomerForVisitDto;

    private Map<String, Integer> mapProductSelected = new LinkedHashMap<>();

    SdkAsyncTask<?> refreshTask;
    IExchangeReturnChooseProductView iView;

    public ExchangeReturnChooseProductPresenter(IExchangeReturnChooseProductView i, CustomerForVisit c) {
        iView = i;
        mCustomerForVisitDto = c;
    }

    public void requestProductList() {

        RequestCompleteCallback<PlaceOrderProductResult> refreshCallback = new RequestCompleteCallback<PlaceOrderProductResult>() {
            @Override
            public void onSuccess(PlaceOrderProductResult info) {
                productsResult = info;
                rebindData();
            }

            @Override
            public void onError(SdkException info) {
                iView.showErrorInfo(info);
            }

            @Override
            public void onFinish(boolean canceled) {
                refreshTask = null;
                iView.finishTask();
            }
        };
        refreshTask = MainEndpoint
                .get()
                .requestPlaceOrderProductList(mCustomerForVisitDto.getId())
                .executeAsync(refreshCallback);
    }

    RequestCompleteCallback<IdDto> callback = new RequestCompleteCallback<IdDto>() {
        @Override
        public void onSuccess(IdDto response) {
            iView.broadcastExchangeReturnProduct(response.getId(),mCustomerForVisitDto.getName(),dto.getTotal());
        }

        @Override
        public void onError(SdkException ex) {
            iView.showErrorInfo(ex);
        }

        @Override
        public void onFinish(boolean canceled) {
            iView.dismissLoadingDialog();
        }
    };


    public void sendExchangeReturnProduct(boolean isExchange) {
        dto = new ExchangeReturnCreateDto();
        String salesmanId = OAuthSession.getDefaultSession() != null ? OAuthSession.getDefaultSession().getUserInfo().getId() : null;
        dto.setSalesmanId(salesmanId);
        dto.setCustomerId(mCustomerForVisitDto.getId());
        int size = selectedList.size();
        int total = 0;
        ExchangeReturnCreateDto.ExchangeReturnDetailCreateDto[] details = new ExchangeReturnCreateDto.ExchangeReturnDetailCreateDto[size];
        for (int i = 0; i < size; i++) {
            ExchangeReturnCreateDto.ExchangeReturnDetailCreateDto item = new ExchangeReturnCreateDto.ExchangeReturnDetailCreateDto();
            item.setProductId(selectedList.get(i).getId());
            item.setQuantity(new BigDecimal(selectedList.get(i).getQuantity()));
            total = total + selectedList.get(i).getQuantity();
            details[i] = item;
        }
        dto.setDetails(details);
        dto.setTotal(total);
        if (isExchange) {
            refreshTask = MainEndpoint
                    .get()
                    .requestSendExchangeProduct(dto)
                    .executeAsync(callback);
        } else {
            refreshTask = MainEndpoint
                    .get()
                    .requestSendReturnProduct(dto)
                    .executeAsync(callback);
        }
    }

    public void rebindData() {
        productList.clear();
        selectedList.clear();
        total = BigDecimal.ZERO;
        if (productsResult != null && productsResult.getItems() != null) {
            Arrays.sort(productsResult.getItems());
            for (int i = 0; i < productsResult.getItems().length; i++) {
                PlaceOrderProduct product = productsResult.getItems()[i];
                if (mapProductSelected.containsKey(product.getId())) {
                    int quantity = mapProductSelected.get(product.getId());
                    product.setQuantity(quantity);
                    selectedList.add(product);
                    total = total.add(new BigDecimal(quantity));
                } else {
                    productList.add(product);
                }

            }
        }
        iView.updateProductList();
        iView.updateSelectedList();
        iView.updateTotalCost(NumberFormatUtils.formatNumber(total));
    }

    public PlaceOrderProductResult getProductsResult() {
        return productsResult;
    }

    public void addSelectedProduct(String idProduct, int value) {
        mapProductSelected.put(idProduct, value);
        rebindData();
    }

    public List<PlaceOrderProduct> getProductList() {
        return productList;
    }

    public void setProductList(List<PlaceOrderProduct> productList) {
        this.productList = productList;
    }

    public List<PlaceOrderProduct> getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(List<PlaceOrderProduct> selectedList) {
        this.selectedList = selectedList;
    }


    public void removeSelectedItem(int position) {
        PlaceOrderProduct product = selectedList.remove(position);
        mapProductSelected.remove(product.getId());
        rebindData();
    }

    public void updateSelectedItemQuantity(BigDecimal value, int position) {
        PlaceOrderProduct product = selectedList.get(position);
        if (value.compareTo(new BigDecimal(product.getQuantity())) == 0) {
            return;
        }
        mapProductSelected.put(product.getId(), value.intValue());
        rebindData();
    }
}
