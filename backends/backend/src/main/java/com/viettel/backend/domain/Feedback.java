package com.viettel.backend.domain;

import java.util.List;

import org.bson.types.ObjectId;

import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.domain.embed.CustomerEmbed;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.util.entity.SimpleDate;

public interface Feedback {
    
    public static final String COLUMNNAME_CREATED_TIME_VALUE = "startTime.value";
    public static final String COLUMNNAME_IS_FEEDBACKS_READED = "feedbacksReaded";
    
    public ObjectId getId();
    
    public CategoryEmbed getDistributor();
    
    public UserEmbed getSalesman();
    
    public CustomerEmbed getCustomer();
    
    public SimpleDate getCreatedTime();
    
    public boolean isFeedbacksReaded();

    public List<String> getFeedbacks();

}
