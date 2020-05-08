package com.viettel.backend.dto.report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.LinkedList;

import org.springframework.util.Assert;

import com.viettel.backend.domain.Product;
import com.viettel.backend.domain.ProductCategory;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.domain.embed.OrderProduct;
import com.viettel.backend.dto.category.ProductSimpleDto;
import com.viettel.backend.dto.category.UserDto;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.dto.common.I_ProductPhotoFactory;
import com.viettel.backend.dto.common.ProgressDto;

public class PerformanceDto extends UserDto {

    private static final long serialVersionUID = -2017957272413725675L;

    private int nbDay;
    private ProgressDto revenue;
    private ProgressDto quantity;
    private ProgressDto productivity;
    private ProgressDto nbOrder;
    private ProgressDto newCustomer;
    private ProgressDto nbVisit;
    private int nbVisitErrorPosition;
    private int nbVisitErrorDuration;
    private int nbVisitHasOrder;

    private int totalSKU;
    private BigDecimal skuByOrderPlan;
    private BigDecimal quantityByOrderPlan;
    private BigDecimal revenueByOrderPlan;

    private Collection<SalesResultDailyDto> salesResultsDaily;
    private Collection<ProductCategorySalesQuantityDto> productCategoriesSalesQuantity;

    public PerformanceDto(User user) {
        super(user);

        this.nbDay = 0;
        this.revenue = new ProgressDto();
        this.quantity = new ProgressDto();
        this.productivity = new ProgressDto();
        this.nbOrder = new ProgressDto();
        this.newCustomer = new ProgressDto();
        this.nbVisit = new ProgressDto();
        this.nbVisitErrorPosition = 0;
        this.nbVisitErrorDuration = 0;
        this.nbVisitHasOrder = 0;

        this.totalSKU = 0;
        this.skuByOrderPlan = BigDecimal.ZERO;
        this.quantityByOrderPlan = BigDecimal.ZERO;
        this.revenueByOrderPlan = BigDecimal.ZERO;
    }

    public int getNbDay() {
        return nbDay;
    }

    public void setNbDay(int nbDay) {
        this.nbDay = nbDay;
    }

    public ProgressDto getRevenue() {
        return revenue;
    }

    public ProgressDto getQuantity() {
        return quantity;
    }

    public ProgressDto getProductivity() {
        return productivity;
    }

    public ProgressDto getNbOrder() {
        return nbOrder;
    }

    public ProgressDto getNewCustomer() {
        return newCustomer;
    }

    public ProgressDto getNbVisit() {
        return nbVisit;
    }

    public void incrementTotalSku(int totalSKU) {
        this.totalSKU += totalSKU;
    }

    public void setSkuByOrderPlan(BigDecimal skuByOrderPlan) {
        this.skuByOrderPlan = skuByOrderPlan;
    }

    public void setQuantityByOrderPlan(BigDecimal quantityByOrderPlan) {
        this.quantityByOrderPlan = quantityByOrderPlan;
    }

    public void setRevenueByOrderPlan(BigDecimal revenueByOrderPlan) {
        this.revenueByOrderPlan = revenueByOrderPlan;
    }

    public int getNbVisitErrorPosition() {
        return nbVisitErrorPosition;
    }

    public void setNbVisitErrorPosition(int nbVisitErrorPosition) {
        this.nbVisitErrorPosition = nbVisitErrorPosition;
    }

    public int getNbVisitErrorDuration() {
        return nbVisitErrorDuration;
    }

    public void setNbVisitErrorDuration(int nbVisitErrorDuration) {
        this.nbVisitErrorDuration = nbVisitErrorDuration;
    }

    public int getNbVisitHasOrder() {
        return nbVisitHasOrder;
    }

    public void setNbVisitHasOrder(int nbVisitHasOrder) {
        this.nbVisitHasOrder = nbVisitHasOrder;
    }

    public ProgressDto getSkuByOrder() {
        if (nbOrder.getActual().compareTo(BigDecimal.ZERO) > 0) {
            return new ProgressDto(skuByOrderPlan, new BigDecimal(totalSKU).divide(nbOrder.getActual(), 2, RoundingMode.HALF_UP));
        } else {
            return new ProgressDto(skuByOrderPlan, BigDecimal.ZERO);
        }
    }

    public ProgressDto getRevenueByOrder() {
        if (nbOrder.getActual().compareTo(BigDecimal.ZERO) > 0) {
            return new ProgressDto(revenueByOrderPlan, this.revenue.getActual().divide(nbOrder.getActual(), 2, RoundingMode.HALF_UP));
        } else {
            return new ProgressDto(revenueByOrderPlan, BigDecimal.ZERO);
        }
    }

    public ProgressDto getQuantityByOrder() {
        if (nbOrder.getActual().compareTo(BigDecimal.ZERO) > 0) {
            return new ProgressDto(quantityByOrderPlan, this.quantity.getActual().divide(nbOrder.getActual(), 2, RoundingMode.HALF_UP));
        } else {
            return new ProgressDto(quantityByOrderPlan, BigDecimal.ZERO);
        }
    }

    public Collection<SalesResultDailyDto> getSalesResultsDaily() {
        return salesResultsDaily;
    }

    public void setSalesResultsDaily(Collection<SalesResultDailyDto> salesResultsDaily) {
        this.salesResultsDaily = salesResultsDaily;
    }

    public Collection<ProductCategorySalesQuantityDto> getProductCategoriesSalesQuantity() {
        return productCategoriesSalesQuantity;
    }

    public void setProductCategoriesSalesQuantity(
            Collection<ProductCategorySalesQuantityDto> productCategoriesSalesQuantity) {
        this.productCategoriesSalesQuantity = productCategoriesSalesQuantity;
    }

    public static class ProductCategorySalesQuantityDto extends CategorySimpleDto {

        private static final long serialVersionUID = -2912225008471926691L;

        private BigDecimal quantity;
        private Collection<ProductSalesQuantityDto> productsSalesQuantity;

        public ProductCategorySalesQuantityDto(ProductCategory category) {
            super(category);

            this.productsSalesQuantity = new LinkedList<>();
            this.quantity = BigDecimal.ZERO;
        }
        
        public ProductCategorySalesQuantityDto(CategoryEmbed category) {
            super(category);

            this.productsSalesQuantity = new LinkedList<>();
            this.quantity = BigDecimal.ZERO;
        }

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void incrementQuantity(BigDecimal quantity) {
            Assert.notNull(quantity);

            if (this.quantity != null) {
                this.quantity = this.quantity.add(quantity);
            } else {
                this.quantity = quantity;
            }
        }

        public Collection<ProductSalesQuantityDto> getProductsSalesQuantity() {
            return productsSalesQuantity;
        }

        public void addProductsSalesQuantity(ProductSalesQuantityDto productSalesQuantity) {
            this.productsSalesQuantity.add(productSalesQuantity);
        }

    }

    public static class ProductSalesQuantityDto extends ProductSimpleDto {

        private static final long serialVersionUID = -9168082537833676182L;

        private BigDecimal quantity;

        public ProductSalesQuantityDto(Product product, I_ProductPhotoFactory productPhotoFactory) {
            super(product, productPhotoFactory, null);

            this.quantity = BigDecimal.ZERO;
        }

        public ProductSalesQuantityDto(OrderProduct product, I_ProductPhotoFactory productPhotoFactory) {
            super(product, productPhotoFactory);

            this.quantity = BigDecimal.ZERO;
        }

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void incrementQuantity(BigDecimal quantity) {
            Assert.notNull(quantity);

            if (this.quantity != null) {
                this.quantity = this.quantity.add(quantity);
            } else {
                this.quantity = quantity;
            }
        }

    }

}
