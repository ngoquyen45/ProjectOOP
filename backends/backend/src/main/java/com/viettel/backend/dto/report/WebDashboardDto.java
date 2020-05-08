package com.viettel.backend.dto.report;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.util.Assert;

import com.viettel.backend.dto.common.ProgressDto;
import com.viettel.backend.util.entity.SimpleDate;

public class WebDashboardDto implements Serializable {

    private static final long serialVersionUID = 6479415520375881650L;

    private String today;
    private Result lastMonthResult;
    private Result thisMonthResult;
    private Result todayResult;
    private List<ProgressWarningItem> progressWarnings;
    private List<BestSellerItem> bestSellers;

    public WebDashboardDto(SimpleDate today) {
        super();

        Assert.notNull(today);

        this.today = today.getIsoDate();
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public Result getLastMonthResult() {
        return lastMonthResult;
    }

    public void setLastMonthResult(Result lastMonthResult) {
        this.lastMonthResult = lastMonthResult;
    }

    public Result getThisMonthResult() {
        return thisMonthResult;
    }

    public void setThisMonthResult(Result thisMonthResult) {
        this.thisMonthResult = thisMonthResult;
    }

    public Result getTodayResult() {
        return todayResult;
    }

    public void setTodayResult(Result todayResult) {
        this.todayResult = todayResult;
    }

    public List<ProgressWarningItem> getProgressWarnings() {
        return progressWarnings;
    }

    public void setProgressWarnings(List<ProgressWarningItem> progressWarnings) {
        this.progressWarnings = progressWarnings;
    }

    public List<BestSellerItem> getBestSellers() {
        return bestSellers;
    }

    public void setBestSellers(List<BestSellerItem> bestSellers) {
        this.bestSellers = bestSellers;
    }

    public static class ProgressWarningItem implements Serializable {

        private static final long serialVersionUID = 7561770081090602624L;

        private String name;
        private ProgressDto progress;

        public ProgressWarningItem(String name, ProgressDto progress) {
            super();

            this.name = name;
            this.progress = progress;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ProgressDto getProgress() {
            return progress;
        }

        public void setProgress(ProgressDto progress) {
            this.progress = progress;
        }

    }

    public static class BestSellerItem implements Serializable {

        private static final long serialVersionUID = -6378231811906026210L;

        private String name;
        private BigDecimal result;

        public BestSellerItem(String name, BigDecimal result) {
            super();

            this.name = name;
            this.result = result;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getResult() {
            return result;
        }

        public void setResult(BigDecimal result) {
            this.result = result;
        }

    }

    public static class Result implements Serializable {

        private static final long serialVersionUID = -6100204946821714010L;

        private int nbDay;
        private ProgressDto revenue;
        private ProgressDto productivity;

        private ProgressDto visit;
        private ProgressDto visitHasOrder;
        private ProgressDto visitErrorDuration;
        private ProgressDto visitErrorPosition;

        private ProgressDto order;
        private ProgressDto orderNoVisit;

        public int getNbDay() {
            return nbDay;
        }

        public void setNbDay(int nbDay) {
            this.nbDay = nbDay;
        }

        public ProgressDto getRevenue() {
            return revenue;
        }

        public void setRevenue(ProgressDto revenue) {
            this.revenue = revenue;
        }

        public ProgressDto getProductivity() {
            return productivity;
        }

        public void setProductivity(ProgressDto productivity) {
            this.productivity = productivity;
        }

        public ProgressDto getVisit() {
            return visit;
        }

        public void setVisit(ProgressDto visit) {
            this.visit = visit;
        }

        public ProgressDto getVisitHasOrder() {
            return visitHasOrder;
        }

        public void setVisitHasOrder(ProgressDto visitHasOrder) {
            this.visitHasOrder = visitHasOrder;
        }

        public ProgressDto getVisitErrorDuration() {
            return visitErrorDuration;
        }

        public void setVisitErrorDuration(ProgressDto visitErrorDuration) {
            this.visitErrorDuration = visitErrorDuration;
        }

        public ProgressDto getVisitErrorPosition() {
            return visitErrorPosition;
        }

        public void setVisitErrorPosition(ProgressDto visitErrorPosition) {
            this.visitErrorPosition = visitErrorPosition;
        }

        public ProgressDto getOrder() {
            return order;
        }

        public void setOrder(ProgressDto order) {
            this.order = order;
        }

        public ProgressDto getOrderNoVisit() {
            return orderNoVisit;
        }

        public void setOrderNoVisit(ProgressDto orderNoVisit) {
            this.orderNoVisit = orderNoVisit;
        }

        public BigDecimal getOrderByDay() {
            if (this.nbDay > 0 && this.order != null && this.order.getActual() != null) {
                return this.order.getActual().divide(new BigDecimal(this.nbDay), 2, RoundingMode.HALF_UP);
            } else {
                return BigDecimal.ZERO;
            }
        }

        public BigDecimal getRevenueByOrder() {
            if (this.revenue != null && this.revenue.getActual() != null && this.order != null
                    && this.order.getActual() != null && this.order.getActual().compareTo(BigDecimal.ZERO) > 0) {
                return this.revenue.getActual().divide(this.order.getActual(), 2, RoundingMode.HALF_UP);
            } else {
                return BigDecimal.ZERO;
            }
        }

        public BigDecimal getVisitByDay() {
            if (this.nbDay > 0 && this.visit != null && this.visit.getActual() != null) {
                return this.visit.getActual().divide(new BigDecimal(this.nbDay), 2, RoundingMode.HALF_UP);
            } else {
                return BigDecimal.ZERO;
            }
        }

    }

}
