package com.viettel.backend.service.category.readonly.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.User;
import com.viettel.backend.dto.category.UserSimpleDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.service.category.readonly.UserService;
import com.viettel.backend.service.common.AbstractService;

@Service
public class UserServiceImpl extends AbstractService implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DistributorRepository distributorRepository;
    
    @Override
    public ListDto<UserSimpleDto> getSupervisors(UserLogin userLogin) {
        List<User> supervisors = userRepository.getSupervisors(userLogin.getClientId());
        if (supervisors == null || supervisors.isEmpty()) {
            return ListDto.emptyList();
        }

        List<UserSimpleDto> dtos = new ArrayList<UserSimpleDto>(supervisors.size());
        for (User supervisor : supervisors) {
            dtos.add(new UserSimpleDto(supervisor));
        }

        return new ListDto<UserSimpleDto>(dtos);
    }

    @Override
    public ListDto<UserSimpleDto> getSalesmen(UserLogin userLogin, String _distributorId) {
        Distributor distributor = getDefaultDistributor(userLogin);
        if (distributor == null) {
            distributor = getMandatoryPO(userLogin, _distributorId, distributorRepository);
            BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId()), "distributor not accisible");
        }

        List<User> salesmen = userRepository.getSalesmenByDistributors(userLogin.getClientId(),
                Collections.singleton(distributor.getId()));
        if (salesmen == null || salesmen.isEmpty()) {
            return ListDto.emptyList();
        }

        List<UserSimpleDto> dtos = new ArrayList<UserSimpleDto>(salesmen.size());
        for (User salesman : salesmen) {
            dtos.add(new UserSimpleDto(salesman));
        }

        return new ListDto<UserSimpleDto>(dtos);
    }
    
}
