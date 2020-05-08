package com.viettel.backend.restful.distributor;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.pricelist.DistributorPriceDto;
import com.viettel.backend.dto.pricelist.DistributorPriceListCreateDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.restful.Meta;
import com.viettel.backend.service.pricelist.DistributorPriceListService;

@RestController(value = "distributorPriceListController")
@RequestMapping(value = "/distributor/price-list")
public class PriceListController extends AbstractController {

    @Autowired
    private DistributorPriceListService distributorPriceListService;

    // SET
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<?> savePriceList(@RequestBody @Valid DistributorPriceListCreateDto createDto) {
        distributorPriceListService.savePriceList(getUserLogin(), createDto);
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }

    // GET
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getPriceList() {
        ListDto<DistributorPriceDto> list = distributorPriceListService.getPriceList(getUserLogin());
        return new Envelope(list).toResponseEntity(HttpStatus.OK);
    }

}
