package com.viettel.backend.service.category.editable.impl;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.viettel.backend.domain.Product;
import com.viettel.backend.domain.ProductCategory;
import com.viettel.backend.domain.UOM;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.dto.category.ProductCreateDto;
import com.viettel.backend.dto.category.ProductDto;
import com.viettel.backend.dto.common.I_ProductPhotoFactory;
import com.viettel.backend.engine.file.FileEngine;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.exeption.BusinessExceptionCode;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CategoryRepository;
import com.viettel.backend.repository.ProductCategoryRepository;
import com.viettel.backend.repository.ProductRepository;
import com.viettel.backend.repository.UOMRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.category.editable.EditableProductService;

@RolePermission(value = { Role.ADMIN })
@Service
public class EditableProductServiceImpl
        extends AbstractCategoryEditableService<Product, ProductDto, ProductDto, ProductCreateDto>
        implements EditableProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FileEngine fileEngine;

    @Autowired
    private UOMRepository uomRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Override
    public CategoryRepository<Product> getRepository() {
        return productRepository;
    }

    @Override
    protected void beforeSetActive(UserLogin userLogin, Product domain, boolean active) {
        if (active) {
            // ACTIVE
            if (domain.getUom() != null) {
                BusinessAssert.isTrue(
                        uomRepository.exists(userLogin.getClientId(), false, true, domain.getUom().getId()),
                        BusinessExceptionCode.UOM_NOT_FOUND, "uom not found");
            }

            if (domain.getProductCategory() != null) {
                BusinessAssert.isTrue(
                        productCategoryRepository.exists(userLogin.getClientId(), false, true,
                                domain.getProductCategory().getId()),
                        BusinessExceptionCode.PRODUCT_CATEGORY_NOT_FOUND, "product category not found");
            }

        } else {
            // DEACTIVE
        }
    }

    @Override
    public Product createDomain(UserLogin userLogin, ProductCreateDto createdto) {
        Product product = new Product();
        product.setDraft(true);

        initPOWhenCreate(Product.class, userLogin, product);

        updateDomain(userLogin, product, createdto);

        return product;
    }

    @Override
    public void updateDomain(UserLogin userLogin, Product product, ProductCreateDto createdto) {
        if (product.isDraft()) {
            checkMandatoryParams(createdto.getUomId(), createdto.getProductCategoryId());

            UOM uom = getMandatoryPO(userLogin, createdto.getUomId(), uomRepository);
            product.setUom(new CategoryEmbed(uom));

            ProductCategory productCategory = getMandatoryPO(userLogin, createdto.getProductCategoryId(),
                    productCategoryRepository);
            product.setProductCategory(new CategoryEmbed(productCategory));
        }

        checkMandatoryParams(createdto.getPhoto(), createdto.getPrice(), createdto.getProductivity());

        product.setPrice(createdto.getPrice());
        product.setProductivity(createdto.getProductivity());
        product.setDescription(createdto.getDescription());

        BusinessAssert.isTrue(fileEngine.exists(userLogin, createdto.getPhoto()), "Photo not exist");
        product.setPhoto(createdto.getPhoto());
    }

    @Override
    public ProductDto createListSimpleDto(final UserLogin userLogin, final Product domain) {
        return new ProductDto(domain, new I_ProductPhotoFactory() {

            @Override
            public String getPhoto(ObjectId productId) {
                if (StringUtils.isEmpty(domain.getPhoto())) {
                    return getConfig(userLogin).getProductPhoto();
                }
                return domain.getPhoto();
            }
        }, null);
    }

    @Override
    public ProductDto createListDetailDto(final UserLogin userLogin, final Product domain) {
        return new ProductDto(domain, new I_ProductPhotoFactory() {

            @Override
            public String getPhoto(ObjectId productId) {
                if (StringUtils.isEmpty(domain.getPhoto())) {
                    return getConfig(userLogin).getProductPhoto();
                }
                return domain.getPhoto();
            }
        }, null);
    }

}
