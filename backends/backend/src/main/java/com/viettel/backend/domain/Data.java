package com.viettel.backend.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "VisitAndOrder")
public class Data extends Visit {

    private static final long serialVersionUID = 6049233309143659890L;

}
