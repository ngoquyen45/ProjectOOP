package com.viettel.dms.presenter;

import android.util.Log;

import com.viettel.dms.helper.NumberFormatUtils;
import com.viettel.dms.ui.iview.IPlaceOrderProductListView;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CustomerForVisit;
import com.viettel.dmsplus.sdk.models.OrderHolder;
import com.viettel.dmsplus.sdk.models.OrderPromotion;
import com.viettel.dmsplus.sdk.models.PlaceOrderProduct;
import com.viettel.dmsplus.sdk.models.PlaceOrderProductResult;
import com.viettel.dmsplus.sdk.models.ProductAndQuantity;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author PHAMHUNG
 * @since 8/27/2015.
 */
public class PlaceOrderProductListPresenter extends BasePresenter {
    List<PlaceOrderProduct> productList = new ArrayList<>();
    List<PlaceOrderProduct> selectedList = new ArrayList<>();
    private OrderPromotion[] orderPromotions;
    BigDecimal total = BigDecimal.ZERO;
    boolean isSetWithOrderHolder = false;

    private PlaceOrderProductResult productsResult = null;

    OrderHolder mOrderHolder;
    CustomerForVisit mCustomerForVisitDto;

    private Map<String, Integer> mapProductSelected = new LinkedHashMap<>();


    SdkAsyncTask<?> refreshTask;
    IPlaceOrderProductListView iView;

    public PlaceOrderProductListPresenter(IPlaceOrderProductListView i, OrderHolder oh, CustomerForVisit c) {
        iView = i;
        mOrderHolder = oh;
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

    public void rebindData() {
        productList.clear();
        selectedList.clear();
        total = BigDecimal.ZERO;
        if (productsResult != null && productsResult.getItems() != null) {
            Arrays.sort(productsResult.getItems());

            if (mOrderHolder != null && !isSetWithOrderHolder && mOrderHolder.productsSelected != null) {
                isSetWithOrderHolder = true;

                Map<String, Integer> mapSelected = new HashMap<>();
                for (PlaceOrderProduct product : mOrderHolder.productsSelected) {
                    mapSelected.put(product.getId(), product.getQuantity());
                }
                for (int i = 0; i < productsResult.getItems().length; i++) {
                    PlaceOrderProduct product = productsResult.getItems()[i];
                    if (mapSelected.containsKey(product.getId())) {
                        int quantity = mapSelected.get(product.getId());
                        product.setQuantity(quantity);
                        selectedList.add(product);
                        mapProductSelected.put(product.getId(), quantity);
                        total = total.add(product.getPrice().multiply(new BigDecimal(quantity)));
                    } else {
                        productList.add(product);
                    }
                }
            } else {
                for (int i = 0; i < productsResult.getItems().length; i++) {
                    PlaceOrderProduct product = productsResult.getItems()[i];
                    if (mapProductSelected.containsKey(product.getId())) {
                        int quantity = mapProductSelected.get(product.getId());
                        product.setQuantity(quantity);
                        selectedList.add(product);
                        total = total.add(product.getPrice().multiply(new BigDecimal(quantity)));
                    } else {
                        productList.add(product);
                    }
                }
            }
        }
        iView.updateProductList();
        iView.updateSelectedList();
        iView.updateTotalCost(NumberFormatUtils.formatNumber(total));
        // productListAdapter.setFilter(queryString);
    }

    public void checkIfHavingPromotion() {

        /*successful request*/
        RequestCompleteCallback<OrderPromotion[]> refreshCallback =

                new RequestCompleteCallback<OrderPromotion[]>() {

                    @Override
                    /*callback function*/
                    public void onSuccess(OrderPromotion[] info) {

                        orderPromotions = info;

                        if (orderPromotions == null) {
                            orderPromotions = new OrderPromotion[0];
                        }

                        iView.getDataSuccess();
                    }

                    @Override
                    public void onError(SdkException info) {
                        iView.showErrorInfo(info);
                        Log.d("Loi:", "bi loi");
                        System.out.print(info);
                    }

                    @Override
            /*loading dialog*/
                    public void onFinish(boolean canceled) {
                        refreshTask = null;
                        iView.dismissLoadingDialog();
                    }
                };

        /*MAIN FUNCTION*/

        /*get product*/
        ProductAndQuantity[] productAndQuantities = new ProductAndQuantity[selectedList.size()];
        int i = 0;

        /*add product into list*/
        for (PlaceOrderProduct product : selectedList) {
            productAndQuantities[i++] = new ProductAndQuantity(product.getId(), product.getQuantity());
        }

        /*make request*/
       refreshTask = MainEndpoint
                .get()
                .requestCalculatePromotion(mCustomerForVisitDto.getId(), productAndQuantities)
                .executeAsync(refreshCallback);

        /*dont request the promotion*/

        orderPromotions = new OrderPromotion[0];

        iView.getDataSuccess();

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

    public OrderHolder getmOrderHolder() {
        return mOrderHolder;
    }

    public OrderPromotion[] getOrderPromotions() {
        return orderPromotions;
    }

    public void setOrderPromotions(OrderPromotion[] orderPromotions) {
        this.orderPromotions = orderPromotions;
    }
}
