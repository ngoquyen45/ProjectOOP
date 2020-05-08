package com.viettel.backend.oauth2.mvc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.viettel.backend.config.root.AppProperties;
import com.viettel.backend.config.root.FrontendProperties;
import com.viettel.backend.config.security.captcha.RecaptchaAuthenticationFilter;
import com.viettel.backend.config.security.captcha.RecaptchaProperties;
import com.viettel.backend.oauth2.core.SecurityContextHelper;

/**
 * @author thanh
 */
@Controller
public class AccountController {
	
    @Autowired
    private FrontendProperties frontendProperties;
	
    @Autowired
    private AppProperties appProperties;
    
	@Autowired(required = false)
	private RecaptchaProperties recaptchaProperties;
	
	@RequestMapping(value = "/")
    public String index() {
		if (SecurityContextHelper.isLoggedInAuthorizationServer()) {
			return "redirect:" + frontendProperties.getWebUrl();
		}
		return "redirect:/account/login";
    }

	@RequestMapping(value = "/account/login", method = RequestMethod.GET)
	public ModelAndView login(HttpServletRequest request) throws Exception {
		if (SecurityContextHelper.isLoggedInAuthorizationServer()) {
			return new ModelAndView("redirect:" + frontendProperties.getWebUrl());
		}
		
		Map<String, Object> model = new HashMap<>(2);
		boolean requiredCaptcha = isRequireCAPTCHA(request);
        model.put("requireCAPTCHA", requiredCaptcha);
		if (requiredCaptcha) {
		    model.put("siteKey", recaptchaProperties.getValidation().getSiteKey());
		}
		
		model.put("languages", appProperties.getLanguages());
		
		return new ModelAndView("login", model);
	}
	
	@RequestMapping(value = "/account/ping")
	@ResponseBody
	public String autoPing() {
	    return "OK";
	}
    
    private boolean isRequireCAPTCHA(HttpServletRequest request) {
        if (recaptchaProperties != null && recaptchaProperties.getSecurity().isStrictMode()) {
            return true;
        }
        Object require = request.getSession().getAttribute(RecaptchaAuthenticationFilter.LOGIN_REQUIRE_CAPTCHA);
        return require != null ? (boolean)require : false;
    }
	
}
