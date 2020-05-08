package com.viettel.backend.domain;

import java.math.BigDecimal;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.domain.embed.CustomerEmbed;
import com.viettel.backend.domain.embed.OrderDetail;
import com.viettel.backend.domain.embed.OrderProduct;
import com.viettel.backend.domain.embed.OrderPromotion;
import com.viettel.backend.domain.embed.OrderPromotionDetail;
import com.viettel.backend.domain.embed.OrderPromotionReward;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.engine.promotion.definition.I_Order;
import com.viettel.backend.util.entity.SimpleDate;

@Document(collection = "VisitAndOrder")
public class Order extends PO implements POApprovable, I_Order<ObjectId, OrderProduct, OrderDetail, OrderPromotion, OrderPromotionDetail, OrderPromotionReward> {

    private static final long serialVersionUID = -1522058115974076896L;

    public static final int DELIVERY_TYPE_IMMEDIATE = 0;
    public static final int DELIVERY_TYPE_IN_DAY = 1;
    public static final int DELIVERY_TYPE_ANOTHER_DAY = 2;

    public static final String COLUMNNAME_CUSTOMER_ID = "customer.id";
    public static final String COLUMNNAME_CUSTOMER_CODE = "customer.code";
    public static final String COLUMNNAME_CUSTOMER_NAME = "customer.name";
    public static final String COLUMNNAME_CUSTOMER_AREA_ID = "customer.area.id";

    public static final String COLUMNNAME_CREATED_BY_ID = "createdBy.id";

    public static final String COLUMNNAME_DISTRIBUTOR_ID = "distributor.id";
    public static final String COLUMNNAME_DISTRIBUTOR_CODE = "distributor.code";
    public static final String COLUMNNAME_DISTRIBUTOR_NAME = "distributor.name";

    public static final String COLUMNNAME_IS_VISIT = "isVisit";
    public static final String COLUMNNAME_IS_ORDER = "isOrder";

    public static final String COLUMNNAME_CODE = "code";

    public static final String COLUMNNAME_CREATED_TIME_VALUE = "startTime.value";
    public static final String COLUMNNAME_DELIVERY_TYPE = "deliveryType";
    public static final String COLUMNNAME_DELIVERY_DATE = "deliveryDate";
    
    public static final String COLUMNNAME_APPROVE_TIME_VALUE = "approveTime.value";
    public static final String COLUMNNAME_APPROVE_USER = "approveUser";

    public static final String COLUMNNAME_SUB_TOTAL = "subTotal";
    public static final String COLUMNNAME_PROMOTION_AMT = "promotionAmt";
    public static final String COLUMNNAME_DISCOUNT_AMT = "discountAmt";

    public static final String COLUMNNAME_GRAND_TOTAL = "grandTotal";
    
    public Order() {
        super();

        setVisit(false);
        setOrder(true);
    }

    private CategoryEmbed distributor;
    private CustomerEmbed customer;
    private UserEmbed createdBy;

    private SimpleDate startTime;
    private SimpleDate endTime;

    private boolean isVisit;
    private boolean isOrder;
    private String code;
    private int approveStatus;
    
    private SimpleDate approveTime;
    private UserEmbed approveUser;

    private int deliveryType;
    private SimpleDate deliveryTime;

    /** Thong tin khuyen mai khac */
    private String comment;

    /** Gia tri don hang truoc khuyen mai */
    private BigDecimal subTotal;

    /** Gia tri khuyen mai */
    private BigDecimal promotionAmt;

    /** Manual Discount Percentage (null if amount is fixed) */
    private BigDecimal discountPercentage;
    /** 
     * Manual Discount Amount
     * discountAmt = fixed or = (subTotal - promotionAmt) * discount
     */
    private BigDecimal discountAmt;

    /** Gia tri cuoi cung cua don hang = subTotal - promotionAmt - discountAmt */
    private BigDecimal grandTotal;
    
    private BigDecimal quantity;
    private BigDecimal productivity;

    private List<OrderDetail> details;
    private List<OrderPromotion> promotionResults;
    
    private boolean vanSales;
    
    public CategoryEmbed getDistributor() {
        return distributor;
    }

    public void setDistributor(CategoryEmbed distributor) {
        this.distributor = distributor;
    }

    public CustomerEmbed getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEmbed customer) {
        this.customer = customer;
    }

    public UserEmbed getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(UserEmbed createdBy) {
        this.createdBy = createdBy;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(int approveStatus) {
        this.approveStatus = approveStatus;
    }
    
    public SimpleDate getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(SimpleDate approveTime) {
        this.approveTime = approveTime;
    }

    public UserEmbed getApproveUser() {
        return approveUser;
    }

    public void setApproveUser(UserEmbed approveUser) {
        this.approveUser = approveUser;
    }

    public int getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(int deliveryType) {
        this.deliveryType = deliveryType;
    }

    public SimpleDate getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(SimpleDate deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getPromotionAmt() {
        return promotionAmt;
    }

    public void setPromotionAmt(BigDecimal promotionAmt) {
        this.promotionAmt = promotionAmt;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public BigDecimal getDiscountAmt() {
        return discountAmt;
    }

    public void setDiscountAmt(BigDecimal discountAmt) {
        this.discountAmt = discountAmt;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getProductivity() {
        return productivity;
    }
    
    public void setProductivity(BigDecimal productivity) {
        this.productivity = productivity;
    }

    public List<OrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetail> details) {
        this.details = details;
    }

    public List<OrderPromotion> getPromotionResults() {
        return promotionResults;
    }

    public void setPromotionResults(List<OrderPromotion> promotionResults) {
        this.promotionResults = promotionResults;
    }

    public SimpleDate getCreatedTime() {
        return startTime;
    }

    public void setCreatedTime(SimpleDate createdTime) {
        startTime = new SimpleDate(createdTime);
        endTime = new SimpleDate(createdTime);
    }

    // PROTECTED
    public boolean isVisit() {
        return isVisit;
    }

    protected void setVisit(boolean isVisit) {
        this.isVisit = isVisit;
    }

    public boolean isOrder() {
        return isOrder;
    }

    protected void setOrder(boolean isOrder) {
        this.isOrder = isOrder;
    }

    protected SimpleDate getStartTime() {
        return startTime;
    }

    protected void setStartTime(SimpleDate startTime) {
        this.startTime = startTime;
    }

    protected SimpleDate getEndTime() {
        return endTime;
    }

    protected void setEndTime(SimpleDate endTime) {
        this.endTime = endTime;
    }
    
    public boolean isVanSales() {
        return vanSales;
    }
    
    public void setVanSales(boolean vanSales) {
        this.vanSales = vanSales;
    }

}
