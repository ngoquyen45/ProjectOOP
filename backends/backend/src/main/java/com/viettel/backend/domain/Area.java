package com.viettel.backend.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.viettel.backend.domain.annotation.UseDistributor;

@UseDistributor
@Document(collection = "Area")
public class Area extends Category {

    private static final long serialVersionUID = -1350241576145500083L;

}
