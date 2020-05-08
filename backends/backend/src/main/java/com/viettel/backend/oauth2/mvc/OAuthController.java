package com.viettel.backend.oauth2.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.viettel.backend.dto.system.ChangePasswordDto;
import com.viettel.backend.dto.system.UserInfoDto;
import com.viettel.backend.exeption.BusinessException;
import com.viettel.backend.oauth2.core.SecurityContextHelper;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.restful.Envelope;
import com.viettel.backend.restful.Meta;
import com.viettel.backend.restful.RestError;
import com.viettel.backend.service.system.AuthenticationService;

@Controller
public class OAuthController {

	@Autowired
	private ConsumerTokenServices tokenServices;

	@Autowired
    private AuthenticationService authenticationService;

	@RequestMapping(value = "/oauth/revoke/{token}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> revokeToken(@PathVariable String token) throws Exception {
		if (tokenServices.revokeToken(token)) {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}
		else {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping("/oauth/userinfo")
	public ResponseEntity<?> getUserInfo() throws Exception {
		UserLogin userLogin = SecurityContextHelper.getCurrentUser();
		try {
		    UserInfoDto userLoginDto = authenticationService.getUserInfoDto(userLogin);
	        return new ResponseEntity<UserInfoDto>(userLoginDto, HttpStatus.OK);
		} catch (UnsupportedOperationException ex) {
		    RestError error = new RestError("OAuthException", HttpStatus.UNAUTHORIZED.value(), "user.not.found");
	        Envelope response = new Envelope(error);
	        return new ResponseEntity<Envelope>(response, HttpStatus.UNAUTHORIZED);
		}
	}
	
	@RequestMapping(value = "/oauth/password", method = RequestMethod.PUT)
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto dto) {
        try {
            authenticationService.changePassword(SecurityContextHelper.getCurrentUser(), dto);
            return new Envelope(Meta.OK).toResponseEntity(HttpStatus.OK);
        } catch (BusinessException ex) {
            RestError error = new RestError("BusinessException", HttpStatus.BAD_REQUEST.value(), ex.getCode());
            Envelope response = new Envelope(error);
            return new ResponseEntity<Envelope>(response, HttpStatus.BAD_REQUEST);
        }
    }

}
