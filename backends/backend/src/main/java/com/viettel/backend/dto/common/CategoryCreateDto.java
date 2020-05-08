package com.viettel.backend.dto.common;

public class CategoryCreateDto extends DTOSimple {

    private static final long serialVersionUID = -975777589819587341L;
    
    private String name;
    private String code;
    private String distributorId;

    public CategoryCreateDto() {
        super((String) null);

        this.name = null;
        this.code = null;
        this.setDistributorId(null);
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

}
