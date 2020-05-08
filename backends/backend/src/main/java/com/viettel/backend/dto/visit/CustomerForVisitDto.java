package com.viettel.backend.dto.visit;

import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.Visit;
import com.viettel.backend.dto.category.CustomerSimpleDto;
import com.viettel.backend.util.entity.Location;

public class CustomerForVisitDto extends CustomerSimpleDto {

    private static final long serialVersionUID = -6146358781236339238L;

    public static final int STATUS_VISITING = 0;
    public static final int STATUS_UNVISITED = 1;
    public static final int STATUS_VISITED = 2;

    private boolean planned;
    private int visitStatus;
    private int seqNo;
    private VisitSimpleInfoDto visitInfo;
    private Location location;
    private String phone;
    private String mobile;
    private String contact;

    public CustomerForVisitDto(Customer customer, boolean planned, int seqNo, Visit visit) {
        super(customer);

        this.planned = planned;
        this.seqNo = seqNo;

        if (customer.getLocation() != null && customer.getLocation().length == 2) {
            this.location = new Location(customer.getLocation());
        }

        this.mobile = customer.getMobile();
        this.phone = customer.getPhone();
        this.contact = customer.getContact();

        if (visit != null) {
            if (visit.getVisitStatus() == Visit.VISIT_STATUS_VISITING) {
                visitStatus = CustomerForVisitDto.STATUS_VISITING;
            } else {
                visitStatus = CustomerForVisitDto.STATUS_VISITED;
                visitInfo = new VisitSimpleInfoDto(visit);
            }
        } else {
            visitStatus = CustomerForVisitDto.STATUS_UNVISITED;
        }
    }

    public boolean isPlanned() {
        return planned;
    }

    public void setPlanned(boolean planned) {
        this.planned = planned;
    }

    public int getVisitStatus() {
        return visitStatus;
    }

    public void setVisitStatus(int visitStatus) {
        this.visitStatus = visitStatus;
    }

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    public VisitSimpleInfoDto getVisitInfo() {
        return visitInfo;
    }

    public void setVisitInfo(VisitSimpleInfoDto visitInfo) {
        this.visitInfo = visitInfo;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

}
