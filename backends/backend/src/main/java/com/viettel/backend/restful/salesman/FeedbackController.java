package com.viettel.backend.restful.salesman;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.feedback.FeedbackSimpleDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.feedback.FeedbackService;

@RestController(value = "salesmanFeedbackController")
@RequestMapping(value = "/salesman/feedback")
public class FeedbackController extends AbstractController {

    @Autowired
    private FeedbackService feedbackService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public final ResponseEntity<?> get(@RequestParam(required = true) String customerId) {
        ListDto<FeedbackSimpleDto> dtos = feedbackService.getLast10FeedbacksFromCustomer(getUserLogin(), customerId);
        return new Envelope(dtos).toResponseEntity(HttpStatus.OK);
    }

}
