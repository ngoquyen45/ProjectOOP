package com.viettel.backend.domain;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.util.Assert;

import com.viettel.backend.domain.annotation.ClientRootFixed;
import com.viettel.backend.domain.annotation.ClientRootInclude;

public abstract class PO implements Serializable {

    private static final long serialVersionUID = 3629393676420041333L;
    
    public static final ObjectId CLIENT_ROOT_ID = ObjectId.createFromLegacyFormat(0, 0, 0);
    
    public static final String COLUMNNAME_ID = "id";
    public static final String COLUMNNAME_CLIENT_ID = "clientId";
    public static final String COLUMNNAME_DRAFT = "draft";
    public static final String COLUMNNAME_ACTIVE = "active";

    @Id
    private ObjectId id;
    private ObjectId clientId;
    private boolean draft;
    private boolean active;

    public PO() {
        this.id = null;
        this.clientId = null;
        this.draft = false;
        this.active = true;
    }
    
    public PO(PO po) {
        Assert.notNull(po);
        
        this.id = po.getId();
        this.clientId = po.getClientId();
        this.draft = po.isDraft();
        this.active = po.isActive();
    }

    public ObjectId getId() {
        return id;
    }
    
    public void setId(ObjectId id) {
        this.id = id;
    }
    
    public ObjectId getClientId() {
        return clientId;
    }

    public void setClientId(ObjectId clientId) {
        this.clientId = clientId;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /******************** OBJECT'S METHODS ********************/
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass().equals(this.getClass())) {
            PO po = (PO) obj;
            if (po.getId() != null && this.getId() != null) {
                return po.getId().equals(this.getId());
            }

            return super.equals(obj);
        }

        return false;
    }
    
    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    @Override
    public String toString() {
        return new StringBuilder(this.getClass().getSimpleName()).append("=").append(getId()).toString();
    }
    
    /******************** STATIC METHODS ********************/
    public static <D extends PO> boolean isClientRootFixed(Class<D> clazz) {
        return clazz.isAnnotationPresent(ClientRootFixed.class);
    }
    
    public static <D extends PO> boolean isClientRootInclude(Class<D> clazz) {
        return clazz.isAnnotationPresent(ClientRootInclude.class);
    }
    
}
