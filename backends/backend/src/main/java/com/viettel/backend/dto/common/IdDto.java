package com.viettel.backend.dto.common;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.util.Assert;

import com.viettel.backend.domain.PO;

public class IdDto implements Serializable {

    private static final long serialVersionUID = -602708292436783698L;

    private String id;

    public IdDto(ObjectId id) {
        Assert.notNull(id);
        
        this.id = id.toString();
    }
    
    public IdDto(PO po) {
        Assert.notNull(po);
        Assert.notNull(po.getId());
        
        this.id = po.getId().toString();
    }
    
    public IdDto(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
