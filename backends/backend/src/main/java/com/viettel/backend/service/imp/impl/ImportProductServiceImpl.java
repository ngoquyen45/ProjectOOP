package com.viettel.backend.service.imp.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Product;
import com.viettel.backend.domain.ProductCategory;
import com.viettel.backend.domain.UOM;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.dto.imp.ImportConfirmDto;
import com.viettel.backend.dto.imp.ImportConfirmDto.RowData;
import com.viettel.backend.dto.imp.ImportProductPhotoDto;
import com.viettel.backend.dto.imp.ImportResultDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.ProductCategoryRepository;
import com.viettel.backend.repository.ProductRepository;
import com.viettel.backend.repository.UOMRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractImportService;
import com.viettel.backend.service.imp.ImportProductService;
import com.viettel.backend.util.StringUtils;

@RolePermission(value = { Role.ADMIN })
@Service
public class ImportProductServiceImpl extends AbstractImportService implements ImportProductService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private UOMRepository uomRepository;

    @Autowired
    private ProductRepository productRepository;

    // PUBLIC
    @Override
    public byte[] getImportProductTemplate(UserLogin userLogin, String lang) {
        lang = getLang(lang);
        
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet productSheet = workbook.createSheet(translate(lang, "product"));
        XSSFRow row = productSheet.createRow(0);
        row.createCell(0).setCellValue(translate(lang, "code") + "*");
        row.createCell(1).setCellValue(translate(lang, "name") + "*");
        row.createCell(2).setCellValue(translate(lang, "product.category") + "*");
        row.createCell(3).setCellValue(translate(lang, "uom") + "*");
        row.createCell(4).setCellValue(translate(lang, "price") + "*");
        row.createCell(5).setCellValue(translate(lang, "productitvity") + "*");
        row.createCell(6).setCellValue(translate(lang, "description"));

        // PRODUCT CATEGORY
        List<ProductCategory> productCategories = productCategoryRepository.getAll(userLogin.getClientId(), null);
        int index = 0;
        XSSFSheet productCategorySheet = workbook.createSheet(translate(lang, "product.category"));
        for (ProductCategory productCategory : productCategories) {
            row = productCategorySheet.createRow(index);
            row.createCell(0).setCellValue(productCategory.getName());
            index++;
        }
        
        // UOM
        List<UOM> uoms = uomRepository.getAll(userLogin.getClientId(), null);
        index = 0;
        XSSFSheet uomSheet = workbook.createSheet(translate(lang, "uom"));
        for (UOM uom : uoms) {
            row = uomSheet.createRow(index);
            row.createCell(0).setCellValue(uom.getName());
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
        Map<String, ProductCategory> productCategoryMap = getProductCategory(userLogin.getClientId());
        Map<String, UOM> uomMap = getUom(userLogin.getClientId());

        List<Product> products = productRepository.getAll(userLogin.getClientId(), null);
        HashSet<String> productNames = new HashSet<String>();
        HashSet<String> productCodes = new HashSet<String>();
        for (Product product : products) {
            if (!StringUtils.isNullOrEmpty(product.getName(), true)) {
                productNames.add(product.getName().trim().toUpperCase());
            }

            if (!StringUtils.isNullOrEmpty(product.getCode(), true)) {
                productCodes.add(product.getCode().trim().toUpperCase());
            }
        }

        return new I_CellValidator[] {
                new MultiCellValidator(new StringMandatoryCellValidator(), new StringUniqueCellValidator(productCodes),
                        new MaxLengthCellValidator(30), new RegexCellValidator("^\\S+$")),
                new MultiCellValidator(new StringMandatoryCellValidator(), new StringUniqueCellValidator(productNames),
                        new MaxLengthCellValidator(50)),
                new MultiCellValidator(new StringMandatoryCellValidator(), new ReferenceCellValidator<ProductCategory>(
                        productCategoryMap)),
                new MultiCellValidator(new StringMandatoryCellValidator(), new ReferenceCellValidator<UOM>(uomMap)),
                new DoubleMinMaxCellValidator(1d, 1000000000d, 0), new DoubleMinMaxCellValidator(1d, 1000000000d, 0), null };
    }

    @Override
    public ImportConfirmDto verify(UserLogin userLogin, String fileId) {
        I_CellValidator[] validators = getValidators(userLogin);
        return getErrorRows(userLogin, fileId, validators);
    }

    @Override
    public ImportConfirmDto confirm(UserLogin userLogin, String fileId) {
        I_CellValidator[] validators = getValidators(userLogin);
        return getValidRows(userLogin, fileId, validators);
    }

    @Override
    public ImportResultDto importProduct(UserLogin userLogin, String fileId, ImportProductPhotoDto photoDto) {
        I_CellValidator[] validators = getValidators(userLogin);
        ImportConfirmDto dto = getValidRows(userLogin, fileId, validators);

        BusinessAssert.notNull(photoDto.getPhotos());
        BusinessAssert.equals(photoDto.getPhotos().length, dto.getRowDatas().size());
        
        List<Product> productToInsert = new ArrayList<Product>(dto.getRowDatas().size());
        int index = 0;
        for (RowData row : dto.getRowDatas()) {
            String photo = photoDto.getPhotos()[index];

            Product product = new Product();

            initPOWhenCreate(Product.class, userLogin, product);
            product.setDraft(false);

            product.setCode((String) row.getDatas().get(0));
            product.setName((String) row.getDatas().get(1));
            product.setProductCategory(new CategoryEmbed((ProductCategory) row.getDatas().get(2)));
            product.setUom(new CategoryEmbed((UOM) row.getDatas().get(3)));
            product.setPrice(new BigDecimal((Double) row.getDatas().get(4)));
            product.setProductivity(new BigDecimal((Double) row.getDatas().get(5)));
            product.setDescription((String) row.getDatas().get(6));

            product.setPhoto(photo);

            productToInsert.add(product);

            index++;
        }

        if (!productToInsert.isEmpty()) {
            productRepository.insertProducts(userLogin.getClientId(), productToInsert);
        }

        return new ImportResultDto(dto.getTotal(), productToInsert.size());
    }

    private Map<String, ProductCategory> getProductCategory(ObjectId clientId) {
        List<ProductCategory> productCategories = productCategoryRepository.getAll(clientId, null);

        HashMap<String, ProductCategory> map = new HashMap<String, ProductCategory>();

        if (productCategories != null) {
            for (ProductCategory productCategory : productCategories) {
                map.put(productCategory.getName().toUpperCase(), productCategory);
            }
        }

        return map;
    }

    private Map<String, UOM> getUom(ObjectId clientId) {
        List<UOM> uoms = uomRepository.getAll(clientId, null);

        HashMap<String, UOM> map = new HashMap<String, UOM>();

        if (uoms != null) {
            for (UOM uom : uoms) {
                map.put(uom.getName().toUpperCase(), uom);
            }
        }

        return map;
    }

}
