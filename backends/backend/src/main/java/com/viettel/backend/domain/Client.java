package com.viettel.backend.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.viettel.backend.domain.annotation.ClientRootFixed;
import com.viettel.backend.domain.annotation.ClientRootInclude;
import com.viettel.backend.domain.annotation.UseCode;

@ClientRootFixed
@ClientRootInclude
@UseCode
@Document(collection = "Client")
public class Client extends Category {

    private static final long serialVersionUID = 4538417826817640859L;

}
