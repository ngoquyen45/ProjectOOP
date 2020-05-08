package com.viettel.backend.restful.salesman;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.category.SurveyDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.category.readonly.SurveyService;

@RestController(value = "salesmanSurveyController")
@RequestMapping(value = "/salesman/survey")
public class SurveyController extends AbstractController {

    @Autowired
    private SurveyService surveyService;

    // LIST AVAILABLE FOR CUSTOMER
    @RequestMapping(value = "/available", method = RequestMethod.GET)
    public ResponseEntity<?>
            getSurveysByCustomer(@RequestParam(value = "customerId", required = true) String customerId) {
        ListDto<SurveyDto> results = surveyService.getSurveysAvailableByCustomer(getUserLogin(), customerId);
        return new Envelope(results).toResponseEntity(HttpStatus.OK);
    }

}
