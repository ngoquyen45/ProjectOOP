package com.viettel.backend.dto.category;

import com.viettel.backend.domain.User;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.dto.common.DTOSimple;

public class UserSimpleDto extends DTOSimple {

    private static final long serialVersionUID = 5689092223088750722L;

    private String username;
    private String fullname;
    
    public UserSimpleDto(User user) {
        super(user);

        this.username = user.getUsername();
        this.fullname = user.getFullname();
    }

    public UserSimpleDto(UserEmbed user) {
        super(user);

        this.username = user.getUsername();
        this.fullname = user.getFullname();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

}
