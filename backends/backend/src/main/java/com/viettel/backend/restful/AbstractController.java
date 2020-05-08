package com.viettel.backend.restful;

import org.springframework.data.domain.PageRequest;

import com.viettel.backend.oauth2.core.SecurityContextHelper;
import com.viettel.backend.oauth2.core.UserLogin;

public abstract class AbstractController {

    protected final UserLogin getUserLogin() {
        return SecurityContextHelper.getCurrentUser();
    }

    protected final PageRequest getPageRequest(Integer page, Integer size) {
        if (page == null || page < 0) {
            page = 1;
        }

        if (size == null || size < 1) {
            size = 10;
        }

        return new PageRequest(page - 1, size, null);
    }
    
}
