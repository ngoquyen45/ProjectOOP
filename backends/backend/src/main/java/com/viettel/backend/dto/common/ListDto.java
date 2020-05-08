package com.viettel.backend.dto.common;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author trung
 */
public class ListDto<T> implements Serializable {

    private static final long serialVersionUID = -2951368320989890231L;
    
    @SuppressWarnings({ "rawtypes" })
    private static final ListDto EMPTY_LIST = new ListDto<>(Collections.emptyList(), 0l);
    
    @SuppressWarnings("unchecked")
    public static final <T> ListDto<T> emptyList() {
        return (ListDto<T>) EMPTY_LIST;
    }
    
    private List<T> list;
    private Long count;
    
    public ListDto() {
        this(null, null);
    }
    
    public ListDto(List<T> list) {
        this.list = list;
        this.count = (long) list.size();
    }
    
    public ListDto(List<T> list, Long count) {
        this.list = list;
        this.count = count;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
