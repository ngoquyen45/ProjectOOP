package com.viettel.backend.dto.imp;

import java.io.Serializable;

public class ImportResultDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int total;
    private int success;
    
    public ImportResultDto(int total, int success) {
        super();
        
        this.total = total;
        this.success = success;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

}
