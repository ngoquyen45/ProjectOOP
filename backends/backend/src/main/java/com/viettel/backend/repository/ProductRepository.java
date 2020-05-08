package com.viettel.backend.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.viettel.backend.domain.Product;

public interface ProductRepository extends CategoryRepository<Product> {
    
    /** draft = false */
    public Map<ObjectId, Product> getProductMap(ObjectId clientId, Boolean active);
    
    public List<Product> getProductsByCategories(ObjectId clientId, Collection<ObjectId> productCategoryIds);
    
    // CHECK
    public boolean checkUOMUsed(ObjectId clientId, ObjectId uomId);
    
    public boolean checkProductCategoryUsed(ObjectId clientId, ObjectId productCategoryId);
    
    // BATCH
    public void insertProducts(ObjectId clientId, Collection<Product> products);

}
