package com.viettel.backend.domain.embed;

import com.viettel.backend.domain.User;

public class UserEmbed extends POEmbed {

    private static final long serialVersionUID = 6422701600145590857L;

    private String username;
    private String fullname;

    public UserEmbed() {
        super();
    }

    public UserEmbed(User user) {
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
