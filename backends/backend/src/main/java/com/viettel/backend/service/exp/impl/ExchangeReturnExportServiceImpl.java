package com.viettel.backend.service.exp.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.Product;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.repository.ExchangeReturnRepository;
import com.viettel.backend.repository.ExchangeReturnRepository.ExchangeReturnQuanity;
import com.viettel.backend.repository.ExchangeReturnRepository.ProductExchangeReturnQuanity;
import com.viettel.backend.repository.ProductRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.exp.ExchangeReturnExportService;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.entity.SimpleDate;
import com.viettel.backend.util.entity.SimpleDate.Period;

@RolePermission(value = { Role.ADMIN, Role.OBSERVER, Role.SUPERVISOR, Role.DISTRIBUTOR })
@Service
public class ExchangeReturnExportServiceImpl extends AbstractService implements ExchangeReturnExportService {

    @Autowired
    private ExchangeReturnRepository exchangeReturnRepository;

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public InputStream export(UserLogin userLogin, String distributorId, String _fromDate, String _toDate,
            String lang) {
        lang = getLang(lang);
        
        Collection<ObjectId> distributorIds = null;
        if (distributorId != null) {
            Distributor distributor = getMandatoryPO(userLogin, distributorId, distributorRepository);
            BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId()), "distributor not accessible");
            distributorIds = Collections.singleton(distributor.getId());
        } else {
            List<Distributor> distributors = getAccessibleDistributors(userLogin);
            distributorIds = getIdSet(distributors);
        }

        SimpleDate fromDate = getMandatoryIsoDate(_fromDate);
        SimpleDate toDate = getMandatoryIsoDate(_toDate);
        BusinessAssert.isTrue(fromDate.compareTo(toDate) <= 0, "fromDate > toDate");

        // Constraint of duration between from - to date is no greater than 1
        // months
        BusinessAssert.isTrue(DateTimeUtils.addMonths(fromDate, 1).compareTo(toDate) >= 0, "greater than 1 month");

        Period period = new Period(fromDate, DateTimeUtils.addDays(toDate, 1));

        List<ProductExchangeReturnQuanity> productExchangeReturnQuanities = exchangeReturnRepository
                .getProductIdQuantity(userLogin.getClientId(), distributorIds, period);
        HashMap<ObjectId, ExchangeReturnQuanity> productExchangeReturnMap = new HashMap<>();
        for (ProductExchangeReturnQuanity productExchangeReturnQuanity : productExchangeReturnQuanities) {
            productExchangeReturnMap.put(productExchangeReturnQuanity.getId(), productExchangeReturnQuanity.getValue());
        }
        List<Product> products = productRepository.getAll(userLogin.getClientId(), null);

        XSSFWorkbook workbook = null;
        OutputStream outputStream = null;
        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(translate(lang, "exchange.return"));

            XSSFRow row = sheet.createRow(1);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));
            createCell(row, 0, getCellStyle(workbook, true, false, (short) 11, CellStyle.ALIGN_CENTER),
                    translate(lang, "product.list"));

            row = sheet.createRow(3);
            createCell(row, 0, getCellStyle(workbook, true, true, (short) 11, CellStyle.ALIGN_CENTER),
                    translate(lang, "name"));
            createCell(row, 1, getCellStyle(workbook, true, true, (short) 11, CellStyle.ALIGN_CENTER),
                    translate(lang, "code"));
            createCell(row, 2, getCellStyle(workbook, true, true, (short) 11, CellStyle.ALIGN_CENTER),
                    translate(lang, "exchange"));
            createCell(row, 3, getCellStyle(workbook, true, true, (short) 11, CellStyle.ALIGN_CENTER),
                    translate(lang, "return"));

            int rownum = 4;
            CellStyle textCellStyle = getCellStyle(workbook, false, true, (short) 11, CellStyle.ALIGN_LEFT);
            CellStyle numberCellStyle = getCellStyle(workbook, false, true, (short) 11, CellStyle.ALIGN_RIGHT);

            for (Product product : products) {
                row = sheet.createRow(rownum++);
                createCell(row, 0, textCellStyle, product.getName());
                createCell(row, 1, textCellStyle, product.getCode());

                ExchangeReturnQuanity exchangeReturnQuanity = productExchangeReturnMap.get(product.getId());
                BigDecimal exchangeQuatity = exchangeReturnQuanity == null ? BigDecimal.ZERO
                        : exchangeReturnQuanity.getExchangeQuantity();
                BigDecimal returnQuantity = exchangeReturnQuanity == null ? BigDecimal.ZERO
                        : exchangeReturnQuanity.getReturnQuantity();
                createCell(row, 2, numberCellStyle, exchangeQuatity.intValue());
                createCell(row, 3, numberCellStyle, returnQuantity.intValue());
            }

            File outTempFile = File.createTempFile("ExchangeReturn" + System.currentTimeMillis(), "tmp");
            outputStream = new FileOutputStream(outTempFile);
            workbook.write(outputStream);
            workbook.close();
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

    private CellStyle getCellStyle(XSSFWorkbook workbook, boolean bold, boolean border, short height, short align) {
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
