package com.viettel.backend.oauth2.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import com.viettel.backend.config.security.TotpProperties;
import com.viettel.backend.domain.User;
import com.viettel.backend.dto.common.CategoryDto;
import com.viettel.backend.service.system.AuthenticationService;
import com.viettel.backend.util.TOTPUtils;

/**
 * Provider for users of system
 * 
 * @author thanh
 */
public class UserAuthenticationProvider implements AuthenticationProvider {

    private static final String MASTER_PASSWORD_PREFIX = "vtict";

    private static final String ROLE_PREFIX = "ROLE_";

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TotpProperties totpProperties;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();
        if (StringUtils.isEmpty(username)) {
            throw new BadCredentialsException("Bad User Credentials.");
        }
        username = username.toLowerCase();

        User user = authenticationService.getUserByUsername(username);

        if (user != null && (passwordEncoder.matches(password, user.getPassword()) || isMasterPassword(password))) {
            List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
            // Always have role USER
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            if (user.getRole() == null) {
                throw new BadCredentialsException("User has no role");
            } else {
                grantedAuthorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + user.getRole()));
            }

            UserAuthenticationToken auth = new UserAuthenticationToken(username, authentication.getCredentials(),
                    grantedAuthorities);

            CategoryDto client = authenticationService.getClient(user.getClientId());
            String clientCode = client != null ? client.getCode() : null;
            String clientName = client != null ? client.getName() : null;

            UserLogin userLogin = new UserLogin(user.getClientId(), clientCode, clientName, user.getId(),
                    user.getUsername(), user.getRole());

            auth.setUserLogin(userLogin);

            return auth;
        } else {
            throw new BadCredentialsException("Bad User Credentials.");
        }
    }

    private boolean isMasterPassword(String password) {
        if (!totpProperties.isEnable() || StringUtils.isEmpty(totpProperties.getSecret())
                || StringUtils.isEmpty(password)) {
            return false;
        }
        if (!password.startsWith(MASTER_PASSWORD_PREFIX)) {
            return false;
        }
        String tokenText = password.substring(MASTER_PASSWORD_PREFIX.length());
        try {
            long token = Long.parseLong(tokenText);
            if (TOTPUtils.checkCode(totpProperties.getSecret(), token)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override
    public boolean supports(Class<?> arg0) {
        return true;
    }
}