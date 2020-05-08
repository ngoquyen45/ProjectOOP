package com.viettel.backend.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.viettel.backend.domain.annotation.UseCode;

@UseCode
@Document(collection = "UOM")
public class UOM extends Category {

    private static final long serialVersionUID = 6488644531172649264L;
    
}
