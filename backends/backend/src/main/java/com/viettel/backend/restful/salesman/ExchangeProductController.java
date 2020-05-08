package com.viettel.backend.restful.salesman;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.exchangereturn.ExchangeReturnCreateDto;
import com.viettel.backend.dto.exchangereturn.ExchangeReturnDto;
import com.viettel.backend.dto.exchangereturn.ExchangeReturnSimpleDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.exchangereturn.ExchangeReturnCreatingService;

@RestController(value = "salesmanExchangeProductController")
@RequestMapping(value = "/salesman/exchange-product")
public class ExchangeProductController extends AbstractController {

    @Autowired
    private ExchangeReturnCreatingService exchangeReturnCreatingService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createExchangeProduct(@RequestBody ExchangeReturnCreateDto createDto) {
        IdDto orderId = exchangeReturnCreatingService.createExchangeProduct(getUserLogin(), createDto);
        return new Envelope(orderId).toResponseEntity();
    }

    @RequestMapping(value = "/today", method = RequestMethod.GET)
    public ResponseEntity<?> getExchangeProductToday() {
        ListDto<ExchangeReturnSimpleDto> dtos = exchangeReturnCreatingService.getExchangeProductToday(getUserLogin());
        return new Envelope(dtos).toResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getExchangeProduct(@PathVariable String id) {
        ExchangeReturnDto dto = exchangeReturnCreatingService.getExchangeProduct(getUserLogin(), id);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

}
