package com.viettel.backend.dto.category;

import com.viettel.backend.domain.Distributor;
import com.viettel.backend.dto.common.CategoryDto;

public class DistributorListDto extends CategoryDto {

    private static final long serialVersionUID = -4570514747523311583L;

    private UserSimpleDto supervisor;
    
    public DistributorListDto(Distributor distributor) {
        super(distributor);
        
        if (distributor.getSupervisor() != null) {
            this.supervisor = new UserSimpleDto(distributor.getSupervisor());
        }
    }

    public UserSimpleDto getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(UserSimpleDto supervisor) {
        this.supervisor = supervisor;
    }
    
}
