package com.viettel.backend.repository.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.viettel.backend.domain.Product;
import com.viettel.backend.repository.ProductRepository;
import com.viettel.backend.repository.common.CacheRepository;

@Repository
public class ProductRepositoryImpl extends CategoryRepositoryImpl<Product> implements ProductRepository {

    @Autowired
    private CacheRepository cacheRepository;
    
    @Override
    public Map<ObjectId, Product> getProductMap(ObjectId clientId, Boolean active) {
        Map<ObjectId, Product> productMap = cacheRepository.getProductMapCache(clientId);
        
        if (productMap == null) {
            List<Product> products = super._getList(clientId, false, null, null, null, null);
            productMap = super._getMap(products);
            cacheRepository.setProductMapCache(clientId, productMap);
        }
        
        if (active != null) {
            HashMap<ObjectId, Product> map = new HashMap<>();
            for (Entry<ObjectId, Product> entry : productMap.entrySet()) {
                if (entry.getValue().isActive() == active) {
                    map.put(entry.getKey(), entry.getValue());
                }
            }
            
            return map;
        } else {
            return productMap;
        }
    }
    
    @Override
    public List<Product> getProductsByCategories(ObjectId clientId, Collection<ObjectId> productCategoryIds) {
        Assert.notNull(productCategoryIds);

        if (productCategoryIds.isEmpty()) {
            return Collections.emptyList();
        }

        Criteria productCategoryCriteria = Criteria.where(Product.COLUMNNAME_PRODUCT_CATEGORY_ID)
                .in(productCategoryIds);

        return super._getList(clientId, false, true, productCategoryCriteria, null, null);
    }

    // CHECK
    @Override
    public boolean checkUOMUsed(ObjectId clientId, ObjectId uomId) {
        Assert.notNull(uomId);

        Criteria criteria = Criteria.where(Product.COLUMNNAME_UOM_ID).is(uomId);

        return _checkUsed(clientId, criteria);
    }

    @Override
    public boolean checkProductCategoryUsed(ObjectId clientId, ObjectId productCategoryId) {
        Assert.notNull(productCategoryId);

        Criteria criteria = Criteria.where(Product.COLUMNNAME_PRODUCT_CATEGORY_ID).is(productCategoryId);

        return _checkUsed(clientId, criteria);
    }

    // BATCH
    @Override
    public void insertProducts(ObjectId clientId, Collection<Product> products) {
        super._insertBatch(clientId, products);
    }
    
    @Override
    protected void onChange(ObjectId clientId) {
        super.onChange(clientId);
        cacheRepository.setProductMapCache(clientId, null);
    }

}
