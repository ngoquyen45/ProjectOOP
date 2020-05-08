package com.viettel.backend.dto.system;

import java.io.Serializable;

public class ChangePasswordDto implements Serializable {

    private static final long serialVersionUID = 7084421756715354610L;
    
    private String newPassword;
    private String oldPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

}
