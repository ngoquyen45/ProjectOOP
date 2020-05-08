package com.viettel.backend.service.imp.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.Product;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.dto.imp.ImportConfirmDto;
import com.viettel.backend.dto.imp.ImportConfirmDto.RowData;
import com.viettel.backend.dto.imp.ImportResultDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.DistributorPriceListRepository;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.repository.ProductRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractImportService;
import com.viettel.backend.service.imp.ImportPriceListService;

@RolePermission(value = { Role.DISTRIBUTOR })
@Service
public class ImportPriceListServiceImpl extends AbstractImportService implements ImportPriceListService {

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DistributorPriceListRepository distributorPriceListRepository;

    @Override
    public byte[] getTemplate(UserLogin userLogin, String _distributorId, String lang) {
        lang = getLang(lang);
        
        Distributor distributor = getDefaultDistributor(userLogin);
        if (distributor == null) {
            distributor = getMandatoryPO(userLogin, _distributorId, distributorRepository);
            BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId()), "distributor not accessible");
        }
                
        List<Product> products = productRepository.getAll(userLogin.getClientId(), null);

        Map<ObjectId, BigDecimal> priceList = getDistributorPriceList(userLogin);
        
        // Finds the workbook instance for XLSX file
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet scheduleSheet = workbook.createSheet(translate(lang, "product.price.list"));

        XSSFRow row = scheduleSheet.createRow(0);
        row.createCell(0).setCellValue(translate(lang, "code"));
        row.createCell(1).setCellValue(translate(lang, "name"));
        row.createCell(2).setCellValue(translate(lang, "product.category"));
        row.createCell(3).setCellValue(translate(lang, "uom"));
        row.createCell(4).setCellValue(translate(lang, "company.price"));
        row.createCell(5).setCellValue(translate(lang, "distributor.price"));

        Collections.sort(products, new Comparator<Product>() {

            @Override
            public int compare(Product p1, Product p2) {
                if (p1.getProductCategory().getId().equals(p2.getProductCategory().getId())) {
                    return p1.getName().compareTo(p2.getName());
                } else {
                    return p1.getProductCategory().getName().compareTo(p2.getProductCategory().getName());
                }
            }
            
        });
        
        int index = 1;
        for (Product product : products) {
            row = scheduleSheet.createRow(index);

            row.createCell(0).setCellValue(product.getCode());
            row.createCell(1).setCellValue(product.getName());
            row.createCell(2).setCellValue(product.getProductCategory().getName());
            row.createCell(3).setCellValue(product.getUom().getName());
            row.createCell(4).setCellValue(product.getPrice().doubleValue());
            BigDecimal distributorPrice = priceList.get(product.getId());
            if (distributorPrice == null) {
                row.createCell(5).setCellValue("");
            } else {
                row.createCell(5).setCellValue(distributorPrice.doubleValue());
            }
            
            index++;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            workbook.write(baos);
            workbook.close();

            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private I_CellValidator[] getValidators(UserLogin userLogin) {
        List<Product> products = productRepository.getAll(userLogin.getClientId(), null);
        Map<String, Product> productByCode = new HashMap<String, Product>();
        for (Product product : products) {
            productByCode.put(product.getCode().trim().toUpperCase(), product);
        }

        LinkedList<I_CellValidator> validators = new LinkedList<AbstractImportService.I_CellValidator>();
        validators.add(new ReferenceCellValidator<Product>(productByCode));
        validators.add(new NoValidator());
        validators.add(new NoValidator());
        validators.add(new NoValidator());
        validators.add(new NoValidator());
        validators.add(new I_CellValidator() {
            
            @Override
            public CheckResult check(XSSFCell cell, RowData rowData, int index) {
                Double value = getNumericCellValue(cell);

                if (value == null) {
                    return new CheckResult(true);
                }
                
                if (value < 0d) {
                    return new CheckResult(value, value.toString(), false);
                }
                
                if (value > 1000000000d) {
                    return new CheckResult(value, value.toString(), false);
                }
                
                if (new BigDecimal(value).scale() > 0) {
                    return new CheckResult(value, value.toString(), false);
                }

                return new CheckResult(value, value.toString(), true);
            }
            
        });

        I_CellValidator[] array = new I_CellValidator[validators.size()];
        validators.toArray(array);
        return array;
    }

    @Override
    public ImportConfirmDto verify(UserLogin userLogin, String _distributorId, String fileId) {
        I_CellValidator[] validators = getValidators(userLogin);
        return getErrorRows(userLogin, fileId, validators);
    }

    @Override
    public ImportResultDto doImport(UserLogin userLogin, String _distributorId, String fileId) {
        Distributor distributor = getDefaultDistributor(userLogin);
        if (distributor == null) {
            distributor = getMandatoryPO(userLogin, _distributorId, distributorRepository);
            BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId()), "distributor not accessible");
        }
        
        I_CellValidator[] validators = getValidators(userLogin);

        ImportConfirmDto dto = getValidRows(userLogin, fileId, validators);

        Map<ObjectId, BigDecimal> priceList = new HashMap<>();
        
        for (RowData row : dto.getRowDatas()) {
            Product product = (Product) row.getDatas().get(0);
            Double distributorPrice = (Double) row.getDatas().get(5);
            if (distributorPrice != null) {
                priceList.put(product.getId(), new BigDecimal(distributorPrice));
            }
        }

        distributorPriceListRepository.savePriceList(userLogin.getClientId(), distributor.getId(), priceList);
        
        return new ImportResultDto(dto.getTotal(), dto.getRowDatas().size());
    }

}
