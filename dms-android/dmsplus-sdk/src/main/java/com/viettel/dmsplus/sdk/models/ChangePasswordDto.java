package com.viettel.dmsplus.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author PHAMHUNG
 * @since 8/6/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangePasswordDto {

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
