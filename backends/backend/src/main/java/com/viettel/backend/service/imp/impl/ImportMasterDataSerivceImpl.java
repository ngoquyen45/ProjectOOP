package com.viettel.backend.service.imp.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.viettel.backend.domain.Client;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.dto.imp.ImportConfirmDto;
import com.viettel.backend.dto.imp.ImportConfirmDto.RowData;
import com.viettel.backend.engine.file.DbFile;
import com.viettel.backend.engine.file.FileEngine;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.ClientRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractImportService;
import com.viettel.backend.service.common.MasterDataService;
import com.viettel.backend.service.imp.ImportMasterDataService;
import com.viettel.backend.util.StringUtils;

@RolePermission(value = { Role.SUPER_ADMIN, Role.SUPPORTER })
@Service
public class ImportMasterDataSerivceImpl extends AbstractImportService implements ImportMasterDataService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private FileEngine fileEngine;
    
    @Autowired
    private MasterDataService masterDataService;
    
    @Autowired
    private ClientRepository clientRepository;
    
    // PUBLIC
    @Override
    public InputStream getMasterDataTemplate(String lang) {
        lang = getLang(lang);
        
        String fileName = "master-data-" + lang + ".xlsx";
        
        ClassPathResource classPathResource = new ClassPathResource(fileName);
        try {
            return classPathResource.getInputStream();
        } catch (IOException e) {
            logger.error("cannot read master data template file", e);
        }
        return null;
    }

    @Override
    public ImportConfirmDto verify(UserLogin userLogin, String fileId) {
        Assert.notNull(userLogin);
        BusinessAssert.notNull(fileId, "file id null");
        DbFile file = fileEngine.get(userLogin, fileId);
        BusinessAssert.notNull(file, "file not found");

        Set<String> usernames = new HashSet<>();
        Set<String> supervisors = new HashSet<>();
        Set<String> salesmen = new HashSet<>();
        Set<String> distributorNames = new HashSet<>();
        Set<String> uomNames = new HashSet<>();
        Set<String> uomCodes = new HashSet<>();
        Set<String> productCategoryNames = new HashSet<>();
        Set<String> productNames = new HashSet<>();
        Set<String> productCodes = new HashSet<>();
        Set<String> areaNames = new HashSet<>();
        Set<String> customerTypeNames = new HashSet<>();
        Set<String> routeNames = new HashSet<>();
        Set<String> customerNames = new HashSet<>();

        XSSFWorkbook wb = null;
        try {
            wb = new XSSFWorkbook(file.getInputStream());
            List<RowData> rowDatas = null;
            List<CellCheck> cellChecks = null;
            
            // SUPERVISOR
            cellChecks = new LinkedList<>();
            cellChecks.add(new StringCellCheck("Fullname", false, 50));
            cellChecks.add(new UniqueCell("Username", false, 30, usernames, supervisors));
            rowDatas = checkSheet(wb.getSheetAt(0), cellChecks);
            if (rowDatas != null) {
                fileEngine.delete(userLogin, fileId);
                return new ImportConfirmDto("Supervisor", 1, true, getColumns(cellChecks), rowDatas);
            }
            
            // DISTRIBUTOR
            cellChecks = new LinkedList<>();
            cellChecks.add(new UniqueCell("Name", false, 30, distributorNames));
            cellChecks.add(new ReferenceCheck("Supervisor", false, supervisors));
            cellChecks.add(new UniqueCell("Username", false, 30, usernames));
            rowDatas = checkSheet(wb.getSheetAt(1), cellChecks);
            if (rowDatas != null) {
                fileEngine.delete(userLogin, fileId);
                return new ImportConfirmDto("Distributor", 2, true, getColumns(cellChecks), rowDatas);
            }

            // SALESMAN
            cellChecks = new LinkedList<>();
            cellChecks.add(new StringCellCheck("Fullname", false, 50));
            cellChecks.add(new UniqueCell("Username", false, 30, usernames, salesmen));
            cellChecks.add(new ReferenceCheck("Distributor", false, distributorNames));
            rowDatas = checkSheet(wb.getSheetAt(2), cellChecks);
            if (rowDatas != null) {
                fileEngine.delete(userLogin, fileId);
                return new ImportConfirmDto("Salesman", 3, true, getColumns(cellChecks), rowDatas);
            }
            
            // UOM
            cellChecks = new LinkedList<>();
            cellChecks.add(new UniqueCell("Name", false, 30, uomNames));
            cellChecks.add(new UniqueCell("Code", false, 30, uomCodes));
            rowDatas = checkSheet(wb.getSheetAt(3), cellChecks);
            if (rowDatas != null) {
                fileEngine.delete(userLogin, fileId);
                return new ImportConfirmDto("UOM", 4, true, getColumns(cellChecks), rowDatas);
            }
            
            // PRODUCT CATEGORY
            cellChecks = new LinkedList<>();
            cellChecks.add(new UniqueCell("Name", false, 30, productCategoryNames));
            rowDatas = checkSheet(wb.getSheetAt(4), cellChecks);
            if (rowDatas != null) {
                fileEngine.delete(userLogin, fileId);
                return new ImportConfirmDto("Product Category", 5, true, getColumns(cellChecks), rowDatas);
            }
        
            // PRODUCT
            cellChecks = new LinkedList<>();
            cellChecks.add(new UniqueCell("Name", false, 30, productNames));
            cellChecks.add(new UniqueCell("Code", false, 10, productCodes));
            cellChecks.add(new ReferenceCheck("UOM", false, uomNames));
            cellChecks.add(new ReferenceCheck("Product Category", false, productCategoryNames));
            cellChecks.add(new NumuricCheck("Price", false, 1.0, 1000000000.0));
            cellChecks.add(new NumuricCheck("Productivity", false, 1.0, 1000000.0));
            cellChecks.add(new StringCellCheck("Description", true, 1000));
            rowDatas = checkSheet(wb.getSheetAt(5), cellChecks);
            if (rowDatas != null) {
                fileEngine.delete(userLogin, fileId);
                return new ImportConfirmDto("Product", 6, true, getColumns(cellChecks), rowDatas);
            }

            // AREA
            cellChecks = new LinkedList<>();
            cellChecks.add(new UniqueCell("Name", false, 30, areaNames));
            cellChecks.add(new ReferenceCheck("Distributor", false, distributorNames));
            rowDatas = checkSheet(wb.getSheetAt(6), cellChecks);
            if (rowDatas != null) {
                fileEngine.delete(userLogin, fileId);
                return new ImportConfirmDto("Area", 7, true, getColumns(cellChecks), rowDatas);
            }
            
            // CUSTOMER TYPE
            cellChecks = new LinkedList<>();
            cellChecks.add(new UniqueCell("Name", false, 30, customerTypeNames));
            rowDatas = checkSheet(wb.getSheetAt(7), cellChecks);
            if (rowDatas != null) {
                fileEngine.delete(userLogin, fileId);
                return new ImportConfirmDto("Customer Type", 8, true, getColumns(cellChecks), rowDatas);
            }

            // ROUTE
            cellChecks = new LinkedList<>();
            cellChecks.add(new UniqueCell("Name", false, 30, routeNames));
            cellChecks.add(new ReferenceCheck("Distributor", false, distributorNames));
            cellChecks.add(new ReferenceCheck("Salesman", true, salesmen));
            rowDatas = checkSheet(wb.getSheetAt(8), cellChecks);
            if (rowDatas != null) {
                fileEngine.delete(userLogin, fileId);
                return new ImportConfirmDto("Route", 9, true, getColumns(cellChecks), rowDatas);
            }
            
            // CUSTOMER
            cellChecks = new LinkedList<>();
            cellChecks.add(new UniqueCell("Name", false, 100, customerNames));
            cellChecks.add(new ReferenceCheck("Distributor", false, distributorNames));
            cellChecks.add(new ReferenceCheck("Area", false, areaNames));
            cellChecks.add(new ReferenceCheck("Customer Type", false, customerTypeNames));
            cellChecks.add(new StringCellCheck("Mobile", false, 15));
            cellChecks.add(new StringCellCheck("Phone", true, 15));
            cellChecks.add(new StringCellCheck("Contact", true, 30));
            cellChecks.add(new StringCellCheck("Email", true, 30));
            cellChecks.add(new NumuricCheck("Latitude", true, -90.0, 90.0));
            cellChecks.add(new NumuricCheck("Longitude", true, -180.0, 180.0));
            cellChecks.add(new ReferenceCheck("Route",true, routeNames));
            rowDatas = checkSheet(wb.getSheetAt(9), cellChecks);
            if (rowDatas != null) {
                fileEngine.delete(userLogin, fileId);
                return new ImportConfirmDto("Customer", 10, true, getColumns(cellChecks), rowDatas);
            }
        } catch (IOException e) {
            logger.error("error when read excel for import customer", e);
            throw new UnsupportedOperationException();
        } finally {
            try {
                wb.close();
            } catch (IOException e) {
                logger.error("error when close excel for import customer", e);
                throw new UnsupportedOperationException();
            }
        }
        
        return new ImportConfirmDto(false, 0, null);
    }

    @Override
    public void importMasterData(UserLogin userLogin, String clientId, String fileId) {
        Client client = getMandatoryPO(userLogin, clientId, clientRepository);
        
        ImportConfirmDto confirmDto = verify(userLogin, fileId);
        BusinessAssert.notTrue(confirmDto.isErrorTemplate());

        DbFile file = fileEngine.get(userLogin, fileId);
        masterDataService.importMasterData(client.getId(), file.getInputStream());
        
        fileEngine.delete(userLogin, fileId);
    }
    
    protected static class CellCheckResult {
        private boolean error;
        private String value;
        
        public CellCheckResult(boolean error, String value) {
            super();
            
            this.error = error;
            this.value = value;
        }
        
        public boolean isError() {
            return error;
        }
        
        public void setError(boolean error) {
            this.error = error;
        }
        
        public String getValue() {
            return value;
        }

    }    
    
    protected static abstract class CellCheck {
        
        private String name;
        
        public CellCheck(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public abstract CellCheckResult check(XSSFCell cell);
        
    }
    
    protected static class StringCellCheck extends CellCheck {
        private boolean canNull;
        private Integer maxLength;
        
        protected StringCellCheck(String name, boolean canNull, Integer maxLength) {
            super(name);
            this.canNull = canNull;
            this.maxLength = maxLength;
        }

        @Override
        public CellCheckResult check(XSSFCell cell) {
            if (cell == null) {
                return new CellCheckResult(!canNull, null);
            }

            String value = null;

            try {
                value = cell.getStringCellValue();
            } catch (Exception e) {
                Double valueDouble = null;
                try {
                    valueDouble = cell.getNumericCellValue();
                    value = valueDouble.toString();
                    if (value.endsWith(".0")) {
                        value = value.substring(0, value.length() - 2);
                    }
                } catch (Exception e2) {
                    return null;
                }
            }

            if (value == null) {
                return new CellCheckResult(!canNull, null);
            }

            value = value.trim();

            if (!canNull && StringUtils.isNullOrEmpty(value, true)) {
                return new CellCheckResult(!canNull, null);
            }

            if (maxLength != null && value.length() > maxLength) {
                return new CellCheckResult(true, value);
            }

            return new CellCheckResult(false, value);
        }
    }
    
    protected static class UniqueCell extends StringCellCheck {
        
        private Set<String> map;
        private Set<String> map2;
        
        protected UniqueCell(String name, boolean canEmpty, Integer maxLength, Set<String> map) {
            super(name, canEmpty, maxLength);
            this.map = map;
        }
        
        protected UniqueCell(String name, boolean canEmpty, Integer maxLength, Set<String> map, Set<String> map2) {
            super(name, canEmpty, maxLength);
            this.map = map;
            this.map2 = map2;
        }

        @Override
        public CellCheckResult check(XSSFCell cell) {
            CellCheckResult result = super.check(cell);
            if (!result.isError() && result.getValue() != null) {
                if (this.map.contains(result.getValue().toLowerCase())) {
                    result.setError(true);
                } else {
                    this.map.add(result.getValue().toLowerCase());
                    if (this.map2 != null) {
                        this.map2.add(result.getValue().toLowerCase());
                    }
                }
            }
            
            return result;
        }
    }
    
    protected static class ReferenceCheck extends StringCellCheck {
        private Set<String> map;
        
        protected ReferenceCheck(String name, boolean canNull, Set<String> map) {
            super(name, canNull, null);
            this.map = map;
        }

        @Override
        public CellCheckResult check(XSSFCell cell) {
            CellCheckResult result = super.check(cell);
            if (!result.isError() && result.getValue() != null) {
                if (!this.map.contains(result.getValue().toLowerCase())) {
                    result.setError(true);
                }
            }
            
            return result;
        }
    }
    
    protected static class NumuricCheck extends CellCheck {
        private boolean canNull;
        private Double min;
        private Double max;
        
        protected NumuricCheck(String name, boolean canNull, Double min, Double max) {
            super(name);
            this.canNull = canNull;
            this.min = min;
            this.max = max;
        }

        @Override
        public CellCheckResult check(XSSFCell cell) {
            if (cell == null) {
                return new CellCheckResult(!canNull, null);
            }

            Double value = null;

            try {
                value = cell.getNumericCellValue();
            } catch (Exception e) {
                return new CellCheckResult(!canNull, null);
            }

            if (min != null && value < min) {
                return new CellCheckResult(true, value.toString());
            }

            if (max != null && value > max) {
                return new CellCheckResult(true, value.toString());
            }

            return new CellCheckResult(false, value.toString());
        }
    }
    
    private List<String> getColumns(List<CellCheck> cellChecks) {
        if (cellChecks == null || cellChecks.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<String> columns = new ArrayList<>(cellChecks.size());
        for (CellCheck cellCheck : cellChecks) {
            columns.add(cellCheck.getName());
        }
        
        return columns;
    }
    
    private List<RowData> checkSheet(XSSFSheet sheet, List<CellCheck> cellChecks) {
        int lastRowNum = sheet.getLastRowNum();
        List<RowData> rowDatas = new LinkedList<RowData>();
        for (int i = 1; i <= lastRowNum; i++) {
            boolean isError = false;
            
            RowData rowData = new RowData(i + 1, new LinkedList<String>(), new LinkedList<Boolean>(), null);
            
            XSSFRow row = sheet.getRow(i);
            
            for (int j = 0; j < cellChecks.size(); j++) {
                CellCheck cellCheck = cellChecks.get(j);
                XSSFCell cell = row.getCell(j);
                
                CellCheckResult result = cellCheck.check(cell);
                rowData.getErrors().add(result.isError());
                rowData.getDataTexts().add(result.getValue());

                if (result.isError()) {
                    isError = true;
                }
            }
            if (isError) {
                rowDatas.add(rowData);
            }
        }
        
        if (!rowDatas.isEmpty()) {
            return rowDatas;
        }
        
        return null;
    }
    
}
