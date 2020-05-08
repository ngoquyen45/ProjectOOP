package com.viettel.backend.domain.embed;

import java.io.Serializable;
import java.util.List;

import org.bson.types.ObjectId;

public class Schedule implements Serializable {

    private static final long serialVersionUID = -7721585729963009430L;
    
    public static final String COLUMNNAME_ROUTE_ID = "routeId";
    public static final String COLUMNNAME_ITEM = "item";
    public static final String COLUMNNAME_ITEMS = "items";

    private ObjectId routeId;
    @SuppressWarnings("unused")
    private ScheduleItem item;
    private List<ScheduleItem> items;

    public ObjectId getRouteId() {
        return routeId;
    }

    public void setRouteId(ObjectId routeId) {
        this.routeId = routeId;
    }

//    public ScheduleItem getItem() {
//        return item;
//    }
//
//    public void setItem(ScheduleItem item) {
//        this.item = item;
//    }

    public List<ScheduleItem> getItems() {
        return items;
    }

    public void setItems(List<ScheduleItem> items) {
        this.items = items;
        
        if (items != null && !items.isEmpty()) {
            this.item = items.get(0);
        } else {
            this.item = null;
        }
    }
    
}
