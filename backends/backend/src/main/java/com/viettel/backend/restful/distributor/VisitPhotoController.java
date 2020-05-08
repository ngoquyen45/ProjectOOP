package com.viettel.backend.restful.distributor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.visit.VisitPhotoDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.visit.VisitPhotoService;

@RestController(value = "distributorVisitPhotoController")
@RequestMapping(value = "/distributor/visit-photo")
public class VisitPhotoController extends AbstractController {

    @Autowired
    private VisitPhotoService visitPhotoService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public final ResponseEntity<?> getVisitPhotos(@RequestParam(required = true) String salesmanId,
            @RequestParam(required = true) String fromDate, @RequestParam(required = true) String toDate) {
        ListDto<VisitPhotoDto> dtos = visitPhotoService.getVisitPhotos(getUserLogin(), salesmanId, fromDate, toDate);
        return new Envelope(dtos).toResponseEntity(HttpStatus.OK);
    }

}
