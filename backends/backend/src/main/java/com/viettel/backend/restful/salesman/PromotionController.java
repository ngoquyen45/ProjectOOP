package com.viettel.backend.restful.salesman;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.category.PromotionListDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.category.readonly.PromotionService;

@RestController("salesmanPromotionController")
@RequestMapping(value = "salesman/promotion")
public class PromotionController extends AbstractController {

    @Autowired
    private PromotionService promotionService;

    // list
    @RequestMapping(value = "/available", method = RequestMethod.GET)
    public ResponseEntity<?> available() {
        ListDto<PromotionListDto> results = promotionService.getPromotionsAvailableByCustomer(getUserLogin(), null);
        return new Envelope(results).toResponseEntity(HttpStatus.OK);
    }

}
