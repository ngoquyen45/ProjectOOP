package com.viettel.backend.oauth2.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorPageController {

    @RequestMapping("/401")
    public ModelAndView get401Page() {
        return new ModelAndView("401");
    }

    @RequestMapping("/404")
    public ModelAndView get404Page() {
        return new ModelAndView("404");
    }
    
    @RequestMapping("/500")
    public ModelAndView get500Page() {
        return new ModelAndView("500");
    }
}
