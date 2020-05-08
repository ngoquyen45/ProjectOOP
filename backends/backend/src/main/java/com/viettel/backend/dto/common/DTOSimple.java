package com.viettel.backend.dto.common;

import java.io.Serializable;

import com.viettel.backend.domain.PO;
import com.viettel.backend.domain.embed.POEmbed;

public abstract class DTOSimple implements Serializable {

    private static final long serialVersionUID = -862151765440311950L;

    private String id;
    
    public DTOSimple(String id) {
        super();
        this.id = id;
    }

    public DTOSimple(PO po) {
        super();

        if (po != null && po.getId() != null) {
            this.id = po.getId().toString();
        }
    }

    public DTOSimple(POEmbed po) {
        super();

        if (po != null && po.getId() != null) {
            this.id = po.getId().toString();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
