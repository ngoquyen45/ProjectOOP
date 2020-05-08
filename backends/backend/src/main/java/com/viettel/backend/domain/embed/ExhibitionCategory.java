package com.viettel.backend.domain.embed;

import java.util.List;

import com.viettel.backend.domain.Category;

/**
 * @author thanh
 */
public class ExhibitionCategory extends Category {

    private static final long serialVersionUID = 3227127774937212395L;

    public static final String COLUMNNAME_ITEMS = "items";

    private List<Category> items;

    public List<Category> getItems() {
        return items;
    }

    public void setItems(List<Category> items) {
        this.items = items;
    }

}
