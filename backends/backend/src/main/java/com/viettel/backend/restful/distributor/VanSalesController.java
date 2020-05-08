package com.viettel.backend.restful.distributor;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.category.UserDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.restful.Meta;
import com.viettel.backend.service.vansale.VanSalesService;

@RestController(value = "distributorVanSalesController")
@RequestMapping(value = "/distributor/van-sales")
public class VanSalesController extends AbstractController {

    @Autowired
    private VanSalesService vanSalesService;

    // SET
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<?> updateVanSalesStatus(@RequestBody @Valid Map<String, Boolean> vanSalesStatus) {
        vanSalesService.updateVanSalesStatus(getUserLogin(), null, vanSalesStatus);
        return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
    }

    // GET
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getSalesman() {
        ListDto<UserDto> list = vanSalesService.getSalesman(getUserLogin(), null);
        return new Envelope(list).toResponseEntity(HttpStatus.OK);
    }

}
