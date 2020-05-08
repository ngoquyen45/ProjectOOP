package com.viettel.backend.domain;

import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.viettel.backend.domain.annotation.UseCode;
import com.viettel.backend.domain.annotation.UseDistributor;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.domain.embed.Schedule;
import com.viettel.backend.domain.embed.ScheduleItem;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.util.entity.SimpleDate;

@UseCode(generate=true)
@UseDistributor
@Document(collection = "Customer")
public class Customer extends Category implements POApprovable {

    private static final long serialVersionUID = 6488644531172649264L;

    public static final String COLUMNNAME_CONTACT = "contact";
    public static final String COLUMNNAME_MOBILE = "mobile";
    public static final String COLUMNNAME_PHONE = "phone";
    public static final String COLUMNNAME_EMAIL = "email";
    public static final String COLUMNNAME_CUSTOMER_TYPE_ID = "customerType.id";
    public static final String COLUMNNAME_AREA_ID = "area.id";
    public static final String COLUMNNAME_PHOTOS = "photos";
    public static final String COLUMNNAME_LOCATION = "location";
    public static final String COLUMNNAME_CREATED_BY_ID = "createdBy.id";
    public static final String COLUMNNAME_CREATED_TIME_VALUE = "createdTime.value";
    public static final String COLUMNNAME_SCHEDULE = "schedule";
    public static final String COLUMNNAME_SCHEDULE_ROUTE_ID = "schedule." + Schedule.COLUMNNAME_ROUTE_ID;
    public static final String COLUMNNAME_SCHEDULE_ITEMS = "schedule." + Schedule.COLUMNNAME_ITEMS;
    public static final String COLUMNNAME_SCHEDULE_ITEM = "schedule." + Schedule.COLUMNNAME_ITEM;
    public static final String COLUMNNAME_SCHEDULE_ITEM_WEEKS = "schedule." + Schedule.COLUMNNAME_ITEM + "."
            + ScheduleItem.COLUMNNAME_WEEKS;

    private String contact;
    private String mobile;
    private String phone;
    private String email;
    private List<String> photos;
    private double[] location; // [Longitude, Latitude]
    private SimpleDate createdTime;
    private int approveStatus;

    private UserEmbed createdBy;
    private CategoryEmbed customerType;
    private CategoryEmbed area;
    private Schedule schedule;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CategoryEmbed getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CategoryEmbed customerType) {
        this.customerType = customerType;
    }

    public CategoryEmbed getArea() {
        return area;
    }
    
    public void setArea(CategoryEmbed area) {
        this.area = area;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    public UserEmbed getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserEmbed createdBy) {
        this.createdBy = createdBy;
    }

    public SimpleDate getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(SimpleDate createdTime) {
        this.createdTime = createdTime;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public int getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(int approveStatus) {
        this.approveStatus = approveStatus;
    }

    @Transient
    public boolean checkScheduleDate(Config config, int dayOfWeek, int weekIndex) {
        if (this.schedule == null) {
            return false;
        }

        List<ScheduleItem> items = this.schedule.getItems();
        if (items == null) {
            return false;
        }

        for (ScheduleItem item : items) {
            if (item != null) {
                if (config.getNumberWeekOfFrequency() > 1) {
                    if (item.getDays().contains(dayOfWeek) && item.getWeeks().contains(weekIndex)) {
                        return true;
                    }
                } else {
                    if (item.getDays().contains(dayOfWeek)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
