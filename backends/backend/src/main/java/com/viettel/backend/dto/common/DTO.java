package com.viettel.backend.dto.common;

import java.io.Serializable;

import com.viettel.backend.domain.PO;

public abstract class DTO implements Serializable {

    private static final long serialVersionUID = -862151765440311950L;

    private String id;
    private boolean draft;
    private boolean active;

    public DTO() {
        super();

        this.id = null;
        this.draft = false;
        this.active = true;
    }

    public DTO(PO po) {
        super();

        if (po != null && po.getId() != null) {
            this.id = po.getId().toString();
            this.draft = po.isDraft();
            this.active = po.isActive();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

}
