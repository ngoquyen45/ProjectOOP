package com.viettel.backend.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.viettel.backend.domain.annotation.UseCode;
import com.viettel.backend.domain.embed.UserEmbed;

@UseCode(generate=true)
@Document(collection = "Distributor")
public class Distributor extends Category {

    private static final long serialVersionUID = 8782304292353101761L;

    public static final String COLUMNNAME_SUPERVISOR_ID = "supervisor.id";

    private UserEmbed supervisor;

    public UserEmbed getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(UserEmbed supervisor) {
        this.supervisor = supervisor;
    }

}
