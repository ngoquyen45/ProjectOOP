package com.viettel.backend.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ProductCategory")
public class ProductCategory extends Category {

    private static final long serialVersionUID = 6488644531172649264L;
    
}
