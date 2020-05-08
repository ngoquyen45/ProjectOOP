package com.viettel.backend.service.system.impl;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.viettel.backend.config.root.AppProperties;
import com.viettel.backend.domain.Client;
import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.PO;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.dto.common.CategoryDto;
import com.viettel.backend.dto.system.ChangePasswordDto;
import com.viettel.backend.dto.system.UserInfoDto;
import com.viettel.backend.exeption.BusinessException;
import com.viettel.backend.exeption.BusinessExceptionCode;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.AuthenticateRepository;
import com.viettel.backend.repository.ClientRepository;
import com.viettel.backend.repository.ConfigRepository;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.system.AuthenticationService;
import com.viettel.backend.util.PasswordUtils;

@Service
public class AuthenticationServiceImpl extends AbstractService implements AuthenticationService {

    private static final User superAdmin = new User();

    static {
        superAdmin.setId(PO.CLIENT_ROOT_ID);
        superAdmin.setActive(true);
        superAdmin.setDraft(false);
        superAdmin.setClientId(PO.CLIENT_ROOT_ID);
        superAdmin.setUsername("", "superadmin");
        superAdmin.setFullname("Super Admin");
        superAdmin.setRole(Role.SUPER_ADMIN);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticateRepository authenticateRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AppProperties appProperties;

    @Override
    public User getUserByUsername(String username) {
        if (username.equalsIgnoreCase(superAdmin.getUsername())) {
            return superAdmin;
        } else {
            return authenticateRepository.findByUsername(username);
        }
    }

    @Override
    public CategoryDto getClient(ObjectId clientId) {
        if (!PO.CLIENT_ROOT_ID.equals(clientId)) {
            Client client = clientRepository.getById(PO.CLIENT_ROOT_ID, clientId);
            return new CategoryDto(client);
        }

        return null;
    }

    @Override
    public UserInfoDto getUserInfoDto(UserLogin userLogin) {
        if (userLogin.getClientId().equals(PO.CLIENT_ROOT_ID)) {
            Config config = configRepository.getConfig(userLogin.getClientId());
            if (userLogin.isRole(Role.SUPER_ADMIN)) {
                return new UserInfoDto(superAdmin, null, null, config == null ? new Config() : config,
                        appProperties.getLanguages());
            } else {
                User user = userRepository.getById(userLogin.getClientId(), userLogin.getUserId());
                if (user == null) {
                    throw new UnsupportedOperationException("current user not found");
                }
                return new UserInfoDto(user, null, null, config == null ? new Config() : config,
                        appProperties.getLanguages());
            }
        } else {
            User user = userRepository.getById(userLogin.getClientId(), userLogin.getUserId());

            if (user == null) {
                throw new UnsupportedOperationException("current user not found");
            }

            return new UserInfoDto(user, userLogin.getClientCode(), userLogin.getClientName(), getConfig(userLogin),
                    appProperties.getLanguages());
        }
    }

    @Override
    public void changePassword(UserLogin userLogin, ChangePasswordDto dto) {
        if (!userLogin.isRole(Role.SUPER_ADMIN)) {
            checkMandatoryParams(dto, dto.getOldPassword(), dto.getNewPassword());

            User user = getCurrentUser(userLogin);

            if (PasswordUtils.matches(passwordEncoder, dto.getOldPassword(), user.getPassword())) {
                user.setPassword(PasswordUtils.encode(passwordEncoder, dto.getNewPassword()));
                userRepository.save(userLogin.getClientId(), user);
            } else {
                throw new BusinessException(BusinessExceptionCode.INVALID_OLD_PASSWORD);
            }
        }
    }

}
