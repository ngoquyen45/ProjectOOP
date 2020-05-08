package com.viettel.backend.service.target.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.Target;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.target.TargetCreateDto;
import com.viettel.backend.dto.target.TargetDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.TargetRepository;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.target.TargetService;

@RolePermission(value = { Role.SUPERVISOR })
@Service
public class TargetServiceImpl extends AbstractService implements TargetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TargetRepository targetRepository;

    @Override
    public ListDto<TargetDto> getTargets(UserLogin userLogin, int month, int year) {
        List<Distributor> distributors = getAccessibleDistributors(userLogin);
        Set<ObjectId> distributorIds = getIdSet(distributors);

        List<User> salesmen = userRepository.getSalesmenByDistributors(userLogin.getClientId(), distributorIds);
        Set<ObjectId> salesmanIds = getIdSet(salesmen);

        Sort sort = new Sort(Target.COLUMNNAME_SALESMAN_NAME);
        List<Target> targets = targetRepository.getTargetsBySalesmen(userLogin.getClientId(), salesmanIds, month, year, sort);
        HashMap<ObjectId, Target> targetBySalesman = new HashMap<>();
        if (targets != null) {
            for (Target target : targets) {
                targetBySalesman.put(target.getSalesman().getId(), target);
            }
        }
        
        List<TargetDto> dtos = new LinkedList<TargetDto>();
        for (User salesman : salesmen) {
            Target target = targetBySalesman.get(salesman.getId());
            if (target != null) {
                dtos.add(new TargetDto(target));
            } else {
                dtos.add(new TargetDto(salesman, month, year));
            }
        }

        return new ListDto<TargetDto>(dtos);
    }
    
    @Override
    public TargetDto getTarget(UserLogin userLogin, int month, int year, String _salesmanId) {
        User salesman = getMandatoryPO(userLogin, _salesmanId, userRepository);
        
        BusinessAssert.isTrue(checkSalesmanAccessible(userLogin, salesman.getId()), "salesman not accessible");
        
        Target target = targetRepository.getTargetBySalesman(userLogin.getClientId(), salesman.getId(), month, year);
        
        if (target != null) {
            return new TargetDto(target);
        } else {
            return new TargetDto(salesman, month, year);
        }
    }

    @Override
    public IdDto save(UserLogin userLogin, TargetCreateDto dto) {
        checkMandatoryParams(dto, dto.getYear(), dto.getMonth(), dto.getSalesmanId(), dto.getRevenue());
        
        BusinessAssert.isTrue(dto.getMonth() >= 0 && dto.getMonth() <= 11, "invalid month");
        BusinessAssert.isTrue(dto.getYear() >= 1900, "invalid year");
        
        User salesman = getMandatoryPO(userLogin, dto.getSalesmanId(), userRepository);
        BusinessAssert.isTrue(checkSalesmanAccessible(userLogin, salesman.getId()), "salesman not accessible");
        
        Target target = targetRepository.getTargetBySalesman(userLogin.getClientId(), salesman.getId(), dto.getMonth(), dto.getYear());
        if (target == null) {
            target = new Target();
            
            initPOWhenCreate(Target.class, userLogin, target);
            
            target.setMonth(dto.getMonth());
            target.setYear(dto.getYear());
            target.setSalesman(new UserEmbed(salesman));
        }
        
        target.setRevenue(dto.getRevenue() == null ? BigDecimal.ZERO : dto.getRevenue());
        target.setQuantity(dto.getQuantity() == null ? BigDecimal.ZERO : dto.getQuantity());
        target.setProductivity(dto.getProductivity() == null ? BigDecimal.ZERO : dto.getProductivity());
        target.setNbOrder(dto.getNbOrder());
        target.setRevenueByOrder(dto.getRevenueByOrder() == null ? BigDecimal.ZERO : dto.getRevenueByOrder());
        target.setSkuByOrder(dto.getSkuByOrder() == null ? BigDecimal.ZERO : dto.getSkuByOrder());
        target.setQuantityByOrder(dto.getQuantityByOrder() == null ? BigDecimal.ZERO : dto.getQuantityByOrder());
        target.setNewCustomer(dto.getNewCustomer());
        
        target = targetRepository.save(userLogin.getClientId(), target);
        
        return new IdDto(target);
    }

    @Override
    public boolean delete(UserLogin userLogin, int month, int year, String salesmanId) {
        User salesman = getMandatoryPO(userLogin, salesmanId, userRepository);
        BusinessAssert.isTrue(checkSalesmanAccessible(userLogin, salesman.getId()), "salesman not accessible");
        
        Target target = targetRepository.getTargetBySalesman(userLogin.getClientId(), salesman.getId(), month, year);
        if (target != null) {
            return targetRepository.delete(userLogin.getClientId(), target.getId());
        } else {
            return true;
        }
    }
    
    private boolean checkSalesmanAccessible(UserLogin userLogin, ObjectId salesmanId) {
        Assert.notNull(salesmanId);
        
        List<Distributor> distributors = getAccessibleDistributors(userLogin);
        Set<ObjectId> distributorIds = getIdSet(distributors);

        List<User> salesmen = userRepository.getSalesmenByDistributors(userLogin.getClientId(), distributorIds);
        Set<ObjectId> salesmanIds = getIdSet(salesmen);
        
        return salesmanIds.contains(salesmanId);
    }

}
