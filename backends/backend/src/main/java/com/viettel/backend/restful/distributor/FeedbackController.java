package com.viettel.backend.restful.distributor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.feedback.FeedbackDto;
import com.viettel.backend.restful.AbstractController;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.service.feedback.FeedbackService;

@RestController(value = "distributorFeedbackController")
@RequestMapping(value = "/distributor/feedback")
public class FeedbackController extends AbstractController {

    @Autowired
    private FeedbackService feedbackService;

    /**
     * Lấy danh sách feedback cho GSBH hiện tại - có phân trang (theo thời gian
     * và chưa đọc lên đầu)
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getFeedbacks(@RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size, @RequestParam(required = false) Boolean read) {
        ListDto<FeedbackDto> dtos = feedbackService.getFeedbacks(getUserLogin(), getPageRequest(page, size), read);
        return new Envelope(dtos).toResponseEntity(HttpStatus.OK);
    }

    /** Lấy chi tiết 1 feedback và cập nhật trạng thái đã đọc */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> readFeedback(@PathVariable String id) {
        FeedbackDto dto = feedbackService.readFeedback(getUserLogin(), id);
        return new Envelope(dto).toResponseEntity(HttpStatus.OK);
    }

}
