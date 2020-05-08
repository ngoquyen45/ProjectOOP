package com.viettel.backend.domain.embed;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.util.Assert;

import com.viettel.backend.domain.PO;

public class POEmbed implements Serializable {
    
    private static final long serialVersionUID = -6744009390560348781L;
    
    public static final String COLUMNNAME_ID = "id";
    
    private ObjectId id;
    
    public POEmbed() {
        this.id = null;
    }
    
    public POEmbed(POEmbed po) {
        Assert.notNull(po);
        
        this.id = po.getId();
    }
    
    public POEmbed(PO po) {
        Assert.notNull(po);
        
        this.id = po.getId();
    }

    public ObjectId getId() {
        return id;
    }
    
    public void setId(ObjectId id) {
        this.id = id;
    }
    
}
