package com.viettel.backend.service.vansale.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.dto.category.UserDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.vansale.VanSalesService;

@RolePermission(value = { Role.ADMIN, Role.SUPERVISOR, Role.DISTRIBUTOR })
@Service
public class VanSalesServiceImpl extends AbstractService implements VanSalesService {

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ListDto<UserDto> getSalesman(UserLogin userLogin, String _distributorId) {
        Distributor distributor = getDefaultDistributor(userLogin);
        if (distributor == null) {
            distributor = getMandatoryPO(userLogin, _distributorId, distributorRepository);
            BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId()), "distributor not accessible");
        }

        List<User> salesmen = userRepository.getSalesmenByDistributors(userLogin.getClientId(),
                Collections.singleton(distributor.getId()));
        List<UserDto> dtos = new ArrayList<>(salesmen.size());
        
        for (User salesman : salesmen) {
            dtos.add(new UserDto(salesman));
        }
        
        return new ListDto<>(dtos);
    }

    @Override
    public void updateVanSalesStatus(UserLogin userLogin, String _distributorId, Map<String, Boolean> vanSalesStatus) {
        Distributor distributor = getDefaultDistributor(userLogin);
        if (distributor == null) {
            distributor = getMandatoryPO(userLogin, _distributorId, distributorRepository);
            BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId()), "distributor not accessible");
        }
        
        List<User> salesmen = userRepository.getSalesmenByDistributors(userLogin.getClientId(),
                Collections.singleton(distributor.getId()));
        Map<ObjectId, User> salesmanMap = getPOMap(salesmen);
        
        Set<ObjectId> vanSales = new HashSet<>();
        Set<ObjectId> noVanSales = new HashSet<>();
        
        for (Entry<String, Boolean> entry : vanSalesStatus.entrySet()) {
            ObjectId salesmanId = getObjectId(entry.getKey());
            BusinessAssert.isTrue(salesmanMap.containsKey(salesmanId));
            if (entry.getValue() != null && entry.getValue()) {
                vanSales.add(salesmanId);
            } else {
                noVanSales.add(salesmanId);
            }
        }
        
        userRepository.updateVanSalesStatus(userLogin.getClientId(), vanSales, true);
        userRepository.updateVanSalesStatus(userLogin.getClientId(), noVanSales, false);
    }

}
