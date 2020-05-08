package com.viettel.backend.dto.imp;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ImportConfirmDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sheetName;
    private Integer sheetIndex;
    private boolean errorTemplate;
    private int total;
    private List<String> columns;
    private List<RowData> rowDatas;

    public ImportConfirmDto(String sheetName, Integer sheetIndex, boolean errorTemplate, List<String> columns, List<RowData> rowDatas) {
        super();
        this.sheetName = sheetName;
        this.sheetIndex = sheetIndex;
        this.errorTemplate = errorTemplate;
        this.columns = columns;
        this.rowDatas = rowDatas;
    }

    public ImportConfirmDto(boolean errorTemplate, int total, List<RowData> rowDatas) {
        super();
        this.errorTemplate = errorTemplate;
        this.total = total;
        this.rowDatas = rowDatas;
    }
    
    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public Integer getSheetIndex() {
        return sheetIndex;
    }

    public void setSheetIndex(Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public boolean isErrorTemplate() {
        return errorTemplate;
    }

    public void setErrorTemplate(boolean errorTemplate) {
        this.errorTemplate = errorTemplate;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<String> getColumns() {
        return columns;
    }
    
    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
    
    public List<RowData> getRowDatas() {
        return rowDatas;
    }

    public void setRowDatas(List<RowData> rowDatas) {
        this.rowDatas = rowDatas;
    }
    
    public int getNbColumn() {
        if (rowDatas != null && !rowDatas.isEmpty()) {
            return rowDatas.get(0).getDataTexts().size();
        }
        return 0;
    }

    public static class RowData {

        private int rowNumber;
        private List<String> dataTexts;
        private List<Boolean> errors;

        @JsonIgnore
        private List<Object> datas;

        public RowData(int rowNumber, List<String> dataTexts, List<Boolean> errors, List<Object> datas) {
            super();
            this.rowNumber = rowNumber;
            this.dataTexts = dataTexts;
            this.errors = errors;
            this.datas = datas;
        }

        public int getRowNumber() {
            return rowNumber;
        }

        public void setRowNumber(int rowNumber) {
            this.rowNumber = rowNumber;
        }
        
        public List<String> getDataTexts() {
            return dataTexts;
        }

        public void setDataTexts(List<String> dataTexts) {
            this.dataTexts = dataTexts;
        }

        public List<Boolean> getErrors() {
            return errors;
        }

        public void setErrors(List<Boolean> errors) {
            this.errors = errors;
        }

        public List<Object> getDatas() {
            return datas;
        }

        public void setDatas(List<Object> datas) {
            this.datas = datas;
        }

    }

}
