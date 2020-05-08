package com.viettel.backend.domain;

public interface POApprovable {
    
    public static final String COLUMNNAME_APPROVE_STATUS = "approveStatus";
    
    public static final int APPROVE_STATUS_PENDING = 0;
    public static final int APPROVE_STATUS_APPROVED = 1;
    public static final int APPROVE_STATUS_REJECTED = 2;
    
    public int getApproveStatus();
    
    public void setApproveStatus(int approveStatus);

}
