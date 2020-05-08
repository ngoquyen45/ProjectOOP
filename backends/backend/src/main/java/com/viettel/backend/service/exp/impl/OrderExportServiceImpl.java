package com.viettel.backend.service.exp.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.Config.OrderDateType;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.Order;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.OrderDetail;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.repository.OrderRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.exp.OrderExportService;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.entity.SimpleDate;
import com.viettel.backend.util.entity.SimpleDate.Period;

@RolePermission(value = { Role.ADMIN, Role.OBSERVER, Role.SUPERVISOR, Role.DISTRIBUTOR })
@Service
public class OrderExportServiceImpl extends AbstractService implements OrderExportService {

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public InputStream exportOrder(UserLogin userLogin, String distributorId, String _fromDate, String _toDate,
            String lang) {
        lang = getLang(lang);
        
        Distributor distributor = getDefaultDistributor(userLogin);
        if (distributor == null) {
            distributor = getMandatoryPO(userLogin, distributorId, distributorRepository);
            BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId()), "distributor not accessible");
        }

        SimpleDate fromDate = getMandatoryIsoDate(_fromDate);
        SimpleDate toDate = getMandatoryIsoDate(_toDate);
        BusinessAssert.isTrue(fromDate.compareTo(toDate) <= 0, "fromDate > toDate");

        // Constraint of duration between from - to date is no greater than 2
        // months
        BusinessAssert.isTrue(DateTimeUtils.addMonths(fromDate, 2).compareTo(toDate) >= 0, "greater than 2 month");

        Config config = getConfig(userLogin);

        Period period = new Period(fromDate, DateTimeUtils.addDays(toDate, 1));
        Sort sort = new Sort(Direction.ASC, Order.COLUMNNAME_CREATED_TIME_VALUE);

        Set<ObjectId> distributorIds = Collections.singleton(distributor.getId());

        SimpleDate today = DateTimeUtils.getToday();

        long count = orderRepository.countOrdersByDistributors(userLogin.getClientId(), distributorIds, period,
                OrderDateType.CREATED_DATE);
        int nbRecordByQuery = 2000;
        int nbQuery = ((int) count / nbRecordByQuery) + ((count % (long) nbRecordByQuery) == 0 ? 0 : 1);

        SXSSFWorkbook workbook = null;
        OutputStream outputStream = null;
        try {
            workbook = new SXSSFWorkbook();
            // Create sheet
            SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet(translate(lang, "order.list"));

            Row row;

            // Create headers
            row = sheet.createRow(1);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 15));
            createCell(row, 0, getCellStyle(workbook, true, false, (short) 11, CellStyle.ALIGN_CENTER),
                    translate(lang, "order.list"));

            row = sheet.createRow(3);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 1));
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 2, 3));
            createCell(row, 0, getCellStyle(workbook, true, false, (short) 11, CellStyle.ALIGN_LEFT),
                    translate(lang, "from.date") + ": " + fromDate.format(config.getDateFormat()));
            createCell(row, 2, getCellStyle(workbook, true, false, (short) 11, CellStyle.ALIGN_LEFT),
                    translate(lang, "to.date") + ": " + toDate.format(config.getDateFormat()));

            row = sheet.createRow(4);
            sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 1));
            createCell(row, 0, getCellStyle(workbook, true, false, (short) 11, CellStyle.ALIGN_LEFT),
                    translate(lang, "issued.date") + ": " + today.format(config.getDateFormat()));

            // Create table headers

            String[] headers = new String[] { "code", "created.date", "delivery.date", "priority", "customer.type",
                    "customer.code", "customer.name", "distributor.code", "distributor.name", "salesman.username",
                    "salesman.fullname", "customer.area", "quantity", "grand.total", "promotion.amt",
                    "discount.amt" };

            row = sheet.createRow(7);
            int cellnum = 0;

            CellStyle headerCellStyle = getCellStyle(workbook, true, true, (short) 11, CellStyle.ALIGN_CENTER);
            for (String header : headers) {
                createCell(row, cellnum++, headerCellStyle, translate(lang, header));
            }

            CellStyle textCellStyle = getCellStyle(workbook, false, true, (short) 11, CellStyle.ALIGN_LEFT);
            CellStyle numberCellStyle = getCellStyle(workbook, false, true, (short) 11, CellStyle.ALIGN_RIGHT);

            int rownum = 8;
            BigDecimal totalQuantity = BigDecimal.ZERO;
            BigDecimal totalGrandTotal = BigDecimal.ZERO;
            BigDecimal totalPromotionAmt = BigDecimal.ZERO;
            BigDecimal totalDiscountAmt = BigDecimal.ZERO;

            for (int page = 1; page <= nbQuery; page++) {
                Pageable pageable = new PageRequest(page - 1, nbRecordByQuery, null);

                List<Order> orders = orderRepository.getOrdersByDistributors(userLogin.getClientId(), distributorIds,
                        period, OrderDateType.CREATED_DATE, pageable, sort);

                for (int i = 0, length = orders.size(); i < length; i++) {
                    Order order = orders.get(i);

                    row = sheet.createRow(rownum++);

                    createCell(row, 0, textCellStyle, order.getCode());
                    createCell(row, 1, textCellStyle, order.getCreatedTime().format(config.getDateFormat()));

                    // Delivery
                    if (order.isVanSales()) {
                        createCell(row, 2, textCellStyle, translate(lang, "van.sales"));
                    } else {
                        if (order.getDeliveryType() == Order.DELIVERY_TYPE_ANOTHER_DAY
                                && order.getDeliveryTime() != null) {
                            createCell(row, 2, textCellStyle, order.getDeliveryTime().format(config.getDateFormat()));
                        } else {
                            createCell(row, 2, textCellStyle, "");
                        }
                    }

                    if (order.getDeliveryType() == Order.DELIVERY_TYPE_IMMEDIATE) {
                        createCell(row, 3, textCellStyle, translate(lang, "immediately"));
                    } else if (order.getDeliveryType() == Order.DELIVERY_TYPE_IN_DAY) {
                        createCell(row, 3, textCellStyle, translate(lang, "same.day"));
                    } else {
                        createCell(row, 3, textCellStyle, "");
                    }

                    // Customer info
                    createCell(row, 4, textCellStyle, order.getCustomer().getCustomerType().getName());
                    createCell(row, 5, textCellStyle, order.getCustomer().getCode());
                    createCell(row, 6, textCellStyle, order.getCustomer().getName());

                    // Distributor info
                    createCell(row, 7, textCellStyle, order.getDistributor().getCode());
                    createCell(row, 8, textCellStyle, order.getDistributor().getName());

                    // Salesman info
                    createCell(row, 9, textCellStyle, order.getCreatedBy().getUsername());
                    createCell(row, 10, textCellStyle, order.getCreatedBy().getFullname());

                    // District info
                    createCell(row, 11, textCellStyle, order.getCustomer().getArea().getName());

                    // Order info
                    createCell(row, 12, numberCellStyle, order.getQuantity().doubleValue());
                    totalQuantity = totalQuantity.add(order.getQuantity());

                    createCell(row, 13, numberCellStyle, order.getGrandTotal().doubleValue());
                    totalGrandTotal = totalGrandTotal.add(order.getGrandTotal());

                    if (order.getPromotionAmt() != null) {
                        createCell(row, 14, numberCellStyle, order.getPromotionAmt().doubleValue());
                        totalPromotionAmt = totalPromotionAmt.add(order.getPromotionAmt());
                    } else {
                        createCell(row, 14, numberCellStyle, 0.0);
                    }

                    if (order.getDiscountAmt() != null) {
                        createCell(row, 15, numberCellStyle, order.getDiscountAmt().doubleValue());
                        totalDiscountAmt = totalDiscountAmt.add(order.getDiscountAmt());
                    } else {
                        createCell(row, 15, numberCellStyle, 0.0);
                    }
                }
            }

            // Footer
            CellStyle footerTextCellStyle = getCellStyle(workbook, true, false, (short) 11, CellStyle.ALIGN_LEFT);
            CellStyle footerNumberCellStyle = getCellStyle(workbook, true, false, (short) 11, CellStyle.ALIGN_RIGHT);

            int lastRownum = rownum++;
            row = sheet.createRow(lastRownum);
            sheet.addMergedRegion(new CellRangeAddress(lastRownum, lastRownum, 0, 11));
            createCell(row, 0, footerTextCellStyle, count + " " + translate(lang, "record(s)"));
            createCell(row, 12, footerNumberCellStyle, totalQuantity.doubleValue());
            createCell(row, 13, footerNumberCellStyle, totalGrandTotal.doubleValue());
            createCell(row, 14, footerNumberCellStyle, totalPromotionAmt.doubleValue());
            createCell(row, 15, footerNumberCellStyle, totalDiscountAmt.doubleValue());

            for (int i = 0, length = headers.length; i < length; i++) {
                sheet.autoSizeColumn(i);
            }

            File outTempFile = File.createTempFile("OrderList" + distributorId + "_" + System.currentTimeMillis(),
                    "tmp");
            outputStream = new FileOutputStream(outTempFile);
            workbook.write(outputStream);
            workbook.dispose();
            outputStream.close();
            outputStream = null;

            return new FileInputStream(outTempFile);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(workbook);
            IOUtils.closeQuietly(outputStream);
        }
    }

    @Override
    public InputStream exportOrderDetail(UserLogin userLogin, String distributorId, String _fromDate, String _toDate,
            String lang) {
        lang = getLang(lang);
        
        Distributor distributor = getDefaultDistributor(userLogin);
        if (distributor == null) {
            distributor = getMandatoryPO(userLogin, distributorId, distributorRepository);
            BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId()), "distributor not accessible");
        }

        SimpleDate fromDate = getMandatoryIsoDate(_fromDate);
        SimpleDate toDate = getMandatoryIsoDate(_toDate);
        BusinessAssert.isTrue(fromDate.compareTo(toDate) <= 0, "fromDate > toDate");

        // Constraint of duration between from - to date is no greater than 2
        // months
        BusinessAssert.isTrue(DateTimeUtils.addMonths(fromDate, 2).compareTo(toDate) >= 0, "greater than 2 month");

        Config config = getConfig(userLogin);

        Period period = new Period(fromDate, DateTimeUtils.addDays(toDate, 1));
        Sort sort = new Sort(Direction.ASC, Order.COLUMNNAME_CREATED_TIME_VALUE);

        Set<ObjectId> distributorIds = Collections.singleton(distributor.getId());

        SimpleDate today = DateTimeUtils.getToday();

        long count = orderRepository.countOrdersByDistributors(userLogin.getClientId(), distributorIds, period,
                OrderDateType.CREATED_DATE);
        int nbRecordByQuery = 2000;
        int nbQuery = ((int) count / nbRecordByQuery) + ((count % (long) nbRecordByQuery) == 0 ? 0 : 1);

        SXSSFWorkbook workbook = null;
        OutputStream outputStream = null;
        try {
            workbook = new SXSSFWorkbook();
            // Create sheet
            SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet(translate(lang, "order.detail.list"));

            Row row;

            // Create headers
            row = sheet.createRow(1);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 8));
            createCell(row, 0, getCellStyle(workbook, true, false, (short) 11, CellStyle.ALIGN_CENTER),
                    translate(lang, "order.detail.list"));

            row = sheet.createRow(3);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 1));
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 2, 3));
            createCell(row, 0, getCellStyle(workbook, true, false, (short) 11, CellStyle.ALIGN_LEFT),
                    translate(lang, "from.date") + ": " + fromDate.format(config.getDateFormat()));
            createCell(row, 2, getCellStyle(workbook, true, false, (short) 11, CellStyle.ALIGN_LEFT),
                    translate(lang, "to.date") + ": " + toDate.format(config.getDateFormat()));

            row = sheet.createRow(4);
            sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 1));
            createCell(row, 0, getCellStyle(workbook, true, false, (short) 11, CellStyle.ALIGN_LEFT),
                    translate(lang, "issued.date") + ": " + today.format(config.getDateFormat()));

            // Create table headers

            String[] headers = new String[] { "code", "created.date", "delivery.date", "priority", "product.code",
                    "product.name", "uom", "quantity", "amount" };

            row = sheet.createRow(7);
            int cellnum = 0;

            CellStyle headerCellStyle = getCellStyle(workbook, true, true, (short) 11, CellStyle.ALIGN_CENTER);
            for (String header : headers) {
                createCell(row, cellnum++, headerCellStyle, translate(lang, header));
            }

            CellStyle textCellStyle = getCellStyle(workbook, false, true, (short) 11, CellStyle.ALIGN_LEFT);
            CellStyle numberCellStyle = getCellStyle(workbook, false, true, (short) 11, CellStyle.ALIGN_RIGHT);

            int rownum = 8;

            for (int page = 1; page <= nbQuery; page++) {
                Pageable pageable = new PageRequest(page - 1, nbRecordByQuery, null);

                List<Order> orders = orderRepository.getOrdersByDistributors(userLogin.getClientId(), distributorIds,
                        period, OrderDateType.CREATED_DATE, pageable, sort);

                for (int i = 0, length = orders.size(); i < length; i++) {
                    Order order = orders.get(i);

                    for (int j = 0, dl = order.getDetails().size(); j < dl; j++) {
                        OrderDetail detail = order.getDetails().get(j);

                        row = sheet.createRow(rownum++);

                        createCell(row, 0, textCellStyle, order.getCode());
                        createCell(row, 1, textCellStyle, order.getCreatedTime().format(config.getDateFormat()));

                        // Delivery
                        if (order.getDeliveryType() == Order.DELIVERY_TYPE_ANOTHER_DAY
                                && order.getDeliveryTime() != null) {
                            createCell(row, 2, textCellStyle, order.getDeliveryTime().format(config.getDateFormat()));
                        } else {
                            createCell(row, 2, textCellStyle, "");
                        }

                        if (order.getDeliveryType() == Order.DELIVERY_TYPE_IMMEDIATE) {
                            createCell(row, 3, textCellStyle, translate(lang, "immediately"));
                        } else if (order.getDeliveryType() == Order.DELIVERY_TYPE_IN_DAY) {
                            createCell(row, 3, textCellStyle, translate(lang, "same.day"));
                        } else {
                            createCell(row, 3, textCellStyle, "");
                        }

                        createCell(row, 4, textCellStyle, detail.getProduct().getCode());
                        createCell(row, 5, textCellStyle, detail.getProduct().getName());
                        createCell(row, 6, textCellStyle, detail.getProduct().getUom().getName());
                        createCell(row, 7, numberCellStyle, detail.getQuantity().doubleValue());
                        createCell(row, 8, numberCellStyle, detail.getAmount().doubleValue());
                    }

                }
            }

            for (int i = 0, length = headers.length; i < length; i++) {
                sheet.autoSizeColumn(i);
            }

            File outTempFile = File.createTempFile("OrderDetailList" + distributorId + "_" + System.currentTimeMillis(),
                    "tmp");
            outputStream = new FileOutputStream(outTempFile);
            workbook.write(outputStream);
            workbook.dispose();
            outputStream.close();
            outputStream = null;

            return new FileInputStream(outTempFile);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(workbook);
            IOUtils.closeQuietly(outputStream);
        }
    }

    private Cell createCell(Row row, int column, CellStyle cs, String value) {
        Cell cell = row.createCell(column);
        cell.setCellStyle(cs);
        cell.setCellValue(value != null ? value : "");
        return cell;
    }

    private Cell createCell(Row row, int column, CellStyle cs, double value) {
        Cell cell = row.createCell(column);
        cell.setCellStyle(cs);
        cell.setCellValue(value);
        return cell;
    }

    private CellStyle getCellStyle(SXSSFWorkbook workbook, boolean bold, boolean border, short height, short align) {
        Font font = workbook.createFont();
        if (bold) {
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        }
        font.setFontName("Arial");
        font.setFontHeightInPoints(height);

        CellStyle cs = workbook.createCellStyle();
        cs.setFont(font);
        cs.setAlignment(align);
        cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        if (border) {
            cs.setBorderBottom(CellStyle.BORDER_THIN);
            cs.setBorderTop(CellStyle.BORDER_THIN);
            cs.setBorderLeft(CellStyle.BORDER_THIN);
            cs.setBorderRight(CellStyle.BORDER_THIN);
        }

        return cs;
    }

}
