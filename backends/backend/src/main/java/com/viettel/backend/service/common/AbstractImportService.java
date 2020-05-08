package com.viettel.backend.service.common;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.viettel.backend.dto.imp.ImportConfirmDto;
import com.viettel.backend.dto.imp.ImportConfirmDto.RowData;
import com.viettel.backend.engine.file.DbFile;
import com.viettel.backend.engine.file.FileEngine;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.util.LocationUtils;
import com.viettel.backend.util.StringUtils;

public abstract class AbstractImportService extends AbstractService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FileEngine fileEngine;

    protected ImportConfirmDto getErrorRows(UserLogin userLogin, String fileId, I_CellValidator[] validators) {
        BusinessAssert.notNull(fileId, "file id null");
        DbFile file = fileEngine.get(userLogin, fileId);
        BusinessAssert.notNull(file, "file not found");

        if (validators == null) {
            throw new IllegalArgumentException("validators is null");
        }

        XSSFWorkbook wb = null;
        try {
            wb = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = wb.getSheetAt(0);

            List<RowData> rowDatas = new LinkedList<RowData>();
            int lastRowNum = sheet.getLastRowNum();

            for (int i = 1; i <= lastRowNum; i++) {

                XSSFRow row = sheet.getRow(i);
                RowData rowData = new RowData(i + 1, new LinkedList<String>(), new LinkedList<Boolean>(), null);

                boolean isError = false;

                for (int j = 0; j < validators.length; j++) {
                    I_CellValidator validator = validators[j];

                    if (validator != null) {
                        XSSFCell cell = row.getCell(j);

                        CheckResult result = validator.check(cell, rowData, j);

                        if (!result.isValid()) {
                            isError = true;
                            rowData.getErrors().add(true);
                        } else {
                            rowData.getErrors().add(false);
                        }

                        rowData.getDataTexts().add(result.getDisplay());
                    }
                }

                if (isError) {
                    rowDatas.add(rowData);
                }
            }

            return new ImportConfirmDto(false, lastRowNum, rowDatas);

        } catch (IOException e) {
            logger.error("error when read excel for import customer", e);
            return new ImportConfirmDto(true, 0, null);
        } finally {
            try {
                wb.close();
            } catch (IOException e) {
                logger.error("error when close excel for import customer", e);
            }
        }
    }

    protected ImportConfirmDto getValidRows(UserLogin userLogin, String fileId, I_CellValidator[] validators) {
        BusinessAssert.notNull(fileId, "file id null");
        DbFile file = fileEngine.get(userLogin, fileId);
        BusinessAssert.notNull(file, "file not found");

        if (validators == null) {
            throw new IllegalArgumentException("validators is null");
        }

        XSSFWorkbook wb = null;
        try {
            wb = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = wb.getSheetAt(0);

            List<RowData> rowDatas = new LinkedList<RowData>();
            int lastRowNum = sheet.getLastRowNum();

            for (int i = 1; i <= lastRowNum; i++) {

                XSSFRow row = sheet.getRow(i);
                RowData rowData = new RowData(i + 1, new LinkedList<String>(), null, new LinkedList<Object>());

                boolean isError = false;

                for (int j = 0; j < validators.length; j++) {
                    I_CellValidator validator = validators[j];

                    XSSFCell cell = row.getCell(j);

                    if (validator != null) {
                        CheckResult result = validator.check(cell, rowData, j);

                        if (!result.isValid()) {
                            isError = true;
                            break;
                        }

                        rowData.getDatas().add(result.getValue());
                        rowData.getDataTexts().add(result.getDisplay());
                    } else {
                        rowData.getDatas().add(getStringCellValue(cell));
                        rowData.getDataTexts().add(getStringCellValue(cell));
                    }
                }

                if (!isError) {
                    rowDatas.add(rowData);
                }
            }

            return new ImportConfirmDto(false, lastRowNum, rowDatas);
        } catch (IOException e) {
            logger.error("error when read excel for import customer", e);
            return new ImportConfirmDto(true, 0, Collections.<RowData> emptyList());
        } finally {
            try {
                wb.close();
            } catch (IOException e) {
                logger.error("error when close excel for import customer", e);
            }
        }
    }

    // PRIVATE
    protected static String getStringCellValue(XSSFCell cell) {
        if (cell == null) {
            return null;
        }

        try {
            String value = cell.getStringCellValue();
            return value;
        } catch (Exception e) {
            Double valueDouble = getNumericCellValue(cell);
            if (valueDouble != null) {
                String value = valueDouble.toString();
                if (value.endsWith(".0")) {
                    value = value.substring(0, value.length() - 2);
                }
                return value;
            } else {
                return null;
            }

        }
    }

    protected static Double getNumericCellValue(XSSFCell cell) {
        if (cell == null) {
            return null;
        }

        try {
            double value = cell.getNumericCellValue();
            return value;
        } catch (Exception e) {
            return null;
        }
    }

    // VALIDATOR
    public static class CheckResult {

        private Object value;
        private String display;
        private boolean valid;

        public CheckResult(boolean valid) {
            super();

            this.value = null;
            this.display = "";
            this.valid = valid;
        }

        public CheckResult(Object value, String display, boolean valid) {
            super();

            this.value = value;
            this.display = display;
            this.valid = valid;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }

    }

    public static interface I_CellValidator {

        public CheckResult check(XSSFCell cell, RowData rowData, int index);

    }

    public static class NoValidator implements I_CellValidator {

        @Override
        public CheckResult check(XSSFCell cell, RowData rowData, int index) {
            String value = getStringCellValue(cell);

            if (StringUtils.isNullOrEmpty(value, true)) {
                return new CheckResult(true);
            }

            value = value.trim();

            return new CheckResult(value, value, true);
        }

    }
    
    public static class MultiCellValidator implements I_CellValidator {

        private List<I_CellValidator> validators;

        public MultiCellValidator(I_CellValidator... validators) {
            if (validators == null || validators.length == 0) {
                throw new IllegalArgumentException("validators cannot null or empty");
            }

            this.validators = Arrays.asList(validators);
        }

        @Override
        public CheckResult check(XSSFCell cell, RowData rowData, int index) {
            CheckResult result = null;

            for (I_CellValidator validator : validators) {
                result = validator.check(cell, rowData, index);

                if (!result.isValid()) {
                    return result;
                }
            }

            return result;
        }

    }

    public static class StringMandatoryCellValidator implements I_CellValidator {

        @Override
        public CheckResult check(XSSFCell cell, RowData rowData, int index) {
            String value = getStringCellValue(cell);

            if (StringUtils.isNullOrEmpty(value, true)) {
                return new CheckResult(false);
            }

            value = value.trim();

            return new CheckResult(value, value, true);
        }

    }

    public static class StringUniqueCellValidator implements I_CellValidator {

        private Set<String> values;

        public StringUniqueCellValidator(Set<String> values) {
            this.values = values;
        }

        @Override
        public CheckResult check(XSSFCell cell, RowData rowData, int index) {
            String value = getStringCellValue(cell);

            if (StringUtils.isNullOrEmpty(value, true)) {
                return new CheckResult(true);
            }

            value = value.trim();

            if (values.contains(value.toUpperCase())) {
                return new CheckResult(value, value, false);
            }

            values.add(value.toUpperCase());

            return new CheckResult(value, value, true);
        }

    }
    
    public static class MaxLengthCellValidator implements I_CellValidator {

        private int maxLength;
        
        public MaxLengthCellValidator(int maxLength) {
            this.maxLength = maxLength;
        }

        @Override
        public CheckResult check(XSSFCell cell, RowData rowData, int index) {
            String value = getStringCellValue(cell);

            if (StringUtils.isNullOrEmpty(value, true)) {
                return new CheckResult(true);
            }

            value = value.trim();

            return new CheckResult(value, value, value.length() <= maxLength);
        }

    }
    
    public static class RegexCellValidator implements I_CellValidator {

        private String regex;
        
        public RegexCellValidator(String regex) {
            this.regex = regex;
        }

        @Override
        public CheckResult check(XSSFCell cell, RowData rowData, int index) {
            String value = getStringCellValue(cell);

            if (StringUtils.isNullOrEmpty(value, true)) {
                return new CheckResult("".matches(regex));
            }

            value = value.trim();

            return new CheckResult(value, value, value.matches(regex));
        }

    }

    public static class ReferenceCellValidator<T> implements I_CellValidator {

        private Map<String, T> map;

        public ReferenceCellValidator(Map<String, T> map) {
            this.map = map;
        }

        @Override
        public CheckResult check(XSSFCell cell, RowData rowData, int index) {
            String value = getStringCellValue(cell);

            if (StringUtils.isNullOrEmpty(value, true)) {
                return new CheckResult(true);
            }

            value = value.trim();

            T o = map.get(value.toUpperCase());

            if (o == null) {
                return new CheckResult(o, value, false);
            }

            return new CheckResult(o, value, true);
        }

    }

    public static class DoubleMandatoryCellValidator implements I_CellValidator {

        @Override
        public CheckResult check(XSSFCell cell, RowData rowData, int index) {
            Double value = getNumericCellValue(cell);

            if (value == null) {
                return new CheckResult(false);
            }

            return new CheckResult(value, value.toString(), true);
        }

    }
    
    public static class IntegerMandatoryCellValidator extends DoubleMinMaxCellValidator implements I_CellValidator {

        public IntegerMandatoryCellValidator() {
            super(null, null, 0);
        }
        
        @Override
        public CheckResult check(XSSFCell cell, RowData rowData, int index) {
            CheckResult result = super.check(cell, rowData, index);

            if (!result.isValid()) {
                return result;
            }
            
            int value = (int) Math.floor((double) result.getValue());
            
            return new CheckResult(value, String.valueOf(value), true);
        }

    }

    public static class DoubleMinMaxCellValidator extends DoubleMandatoryCellValidator implements I_CellValidator {

        private Double min;
        private Double max;
        private Integer maxDecimal;

        public DoubleMinMaxCellValidator(Double min, Double max, Integer maxDecimal) {
            this.min = min;
            this.max = max;
            this.maxDecimal = maxDecimal;
        }
        
        @Override
        public CheckResult check(XSSFCell cell, RowData rowData, int index) {
            CheckResult result = super.check(cell, rowData, index);

            if (!result.isValid()) {
                return result;
            }

            if (min != null && ((double) result.getValue()) < min) {
                result.setValid(false);
            }
            
            if (max != null && ((double) result.getValue()) > max) {
                result.setValid(false);
            }
            
            if (maxDecimal != null && new BigDecimal((double) result.getValue()).scale() > maxDecimal) {
                result.setValid(false);
            }

            return result;
        }

    }
    
    public static class IntegerMinMaxCellValidator extends IntegerMandatoryCellValidator implements I_CellValidator {

        private Integer min;
        private Integer max;

        public IntegerMinMaxCellValidator(Integer min, Integer max) {
            this.min = min;
            this.max = max;
        }
        
        @Override
        public CheckResult check(XSSFCell cell, RowData rowData, int index) {
            CheckResult result = super.check(cell, rowData, index);

            if (!result.isValid()) {
                return result;
            }

            if (min != null && ((int) result.getValue()) < min) {
                result.setValid(false);
            }
            
            if (max != null && ((int) result.getValue()) > max) {
                result.setValid(false);
            }

            return result;
        }

    }

    public static class LatitudeCellValidator extends DoubleMandatoryCellValidator implements I_CellValidator {

        @Override
        public CheckResult check(XSSFCell cell, RowData rowData, int index) {
            CheckResult result = super.check(cell, rowData, index);

            if (!result.isValid()) {
                return result;
            }

            if (!LocationUtils.checkLatitude((double) result.getValue())) {
                result.setValid(false);
            }

            return result;
        }

    }

    public static class LongitudeCellValidator extends DoubleMandatoryCellValidator implements I_CellValidator {

        @Override
        public CheckResult check(XSSFCell cell, RowData rowData, int index) {
            CheckResult result = super.check(cell, rowData, index);

            if (!result.isValid()) {
                return result;
            }

            if (!LocationUtils.checkLongitude((double) result.getValue())) {
                result.setValid(false);
            }

            return result;
        }

    }

}
