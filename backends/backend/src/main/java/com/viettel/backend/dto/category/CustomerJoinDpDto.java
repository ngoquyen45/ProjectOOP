package com.viettel.backend.dto.category;

import com.viettel.backend.dto.common.DTOSimple;

public class CustomerJoinDpDto extends DTOSimple {

    private static final long serialVersionUID = 5792631379372655576L;
    
    private boolean joinDP;
    private Integer dpOption;
    
    public CustomerJoinDpDto() {
        super((String) null);
        
        this.joinDP = false;
    }
    
    public boolean isJoinDP() {
        return joinDP;
    }
    
    public void setJoinDP(boolean joinDP) {
        this.joinDP = joinDP;
    }
    
    public Integer getDpOption() {
        return dpOption;
    }
    
    public void setDpOption(Integer dpOption) {
        this.dpOption = dpOption;
    }

}
