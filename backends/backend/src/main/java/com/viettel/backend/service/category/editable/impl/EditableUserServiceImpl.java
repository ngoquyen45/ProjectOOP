package com.viettel.backend.service.category.editable.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.PO;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.dto.category.UserCreateDto;
import com.viettel.backend.dto.category.UserDto;
import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.common.CategorySeletionDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.exeption.BusinessException;
import com.viettel.backend.exeption.BusinessExceptionCode;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.repository.RouteRepository;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.category.editable.EditableUserService;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.common.UserDeactivationEvent;
import com.viettel.backend.util.PasswordUtils;

import reactor.bus.EventBus;

@RolePermission(value = { Role.ADMIN })
@Service
public class EditableUserServiceImpl extends AbstractService implements EditableUserService {

    private static final Sort DEFAULT_SORT = new Sort(new Order(Direction.DESC, PO.COLUMNNAME_DRAFT),
            new Order(Direction.ASC, User.COLUMNNAME_USERNAME));

    @Autowired
    private EventBus eventBus;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    protected Sort getSort() {
        return DEFAULT_SORT;
    }

    public User createDomain(UserLogin userLogin, UserCreateDto createdto) {
        BusinessAssert.notNull(createdto);
        checkMandatoryParams(createdto.getUsername(), createdto.getFullname(), createdto.getRole());

        User user = new User();
        user.setDraft(true);
        user.setPassword(PasswordUtils.getDefaultPassword(passwordEncoder));

        initPOWhenCreate(User.class, userLogin, user);

        updateDomain(userLogin, user, createdto);

        return user;
    }

    public void updateDomain(UserLogin userLogin, User user, UserCreateDto createdto) {
        // Not allow update non draft record
        BusinessAssert.isTrue(user.isDraft(), "Modify non-draft record are not allowed");

        BusinessAssert.notTrue(
                userRepository.checkUsernameExist(userLogin.getClientId(), user.getId(), createdto.getUsername()),
                BusinessExceptionCode.USERNAME_USED, "username already exists");

        BusinessAssert.notTrue(
                userRepository.checkFullnameExist(userLogin.getClientId(), user.getId(), createdto.getFullname()),
                BusinessExceptionCode.FULLNAME_USED, "fullname already exists");

        user.setUsername(userLogin.getClientCode(), createdto.getUsername());
        user.setFullname(createdto.getFullname());
        user.setRole(createdto.getRole());

        if (user.getRole().equals(Role.DISTRIBUTOR) || user.getRole().equals(Role.SALESMAN)) {
            Distributor distributor = getMandatoryPO(userLogin, createdto.getDistributorId(), distributorRepository);
            user.setDistributor(new CategoryEmbed(distributor));
        }

        if (user.getRole().equals(Role.SALESMAN)) {
            user.setVanSales(createdto.isVanSales());
        }
    }

    public UserDto createListSimpleDto(UserLogin userLogin, User domain) {
        return new UserDto(domain);
    }

    public UserDto createListDetailDto(UserLogin userLogin, User domain) {
        return new UserDto(domain);
    }

    protected void afterUpdate(UserLogin userLogin, User domain, UserCreateDto createdto) {
        // DO NOTHING
    }

    protected void beforeDelete(UserLogin userLogin, User domain) {
        // DO NOTHING
    }

    protected void beforeSetActive(UserLogin userLogin, User user, boolean active) {
        if (active) {
            // ACTIVE

        } else {
            // DEACTIVE
            if (user.getId().equals(userLogin.getUserId())) {
                throw new BusinessException(BusinessExceptionCode.CANNOT_CHANGE_CURRENT_USER);
            }

            if (user.getRole().equals(Role.SUPERVISOR)) {
                BusinessAssert.notTrue(distributorRepository.checkSupervisorUsed(userLogin.getClientId(), user.getId()),
                        BusinessExceptionCode.RECORD_USED_IN_DISTRIBUTOR, "supervisor used in distributor");
            } else if (user.getRole().equals(Role.SALESMAN)) {
                BusinessAssert.notTrue(routeRepository.checkSalesmanUsed(userLogin.getClientId(), user.getId()),
                        BusinessExceptionCode.RECORD_USED_IN_ROUTE, "salesman used in route");
            }
        }
    }

    protected void afterSetActive(UserLogin userLogin, User domain, boolean active) {
        // If deactivating user, log them out
        if (!active) {
            eventBus.notify(UserDeactivationEvent.EVENT_NAME, new UserDeactivationEvent(domain));
        }
    }

    @Override
    public ListDto<UserDto> getList(UserLogin userLogin, String search, Boolean active, Boolean draft, String role,
            String _distributorId, Pageable pageable) {

        ObjectId distributorId = null;
        if (role != null) {
            BusinessAssert.isTrue(role.equals(Role.ADMIN) || role.equals(Role.OBSERVER) || role.equals(Role.SUPERVISOR)
                    || role.equals(Role.DISTRIBUTOR) || role.equals(Role.SALESMAN));

            if (role.equals(Role.DISTRIBUTOR) || role.equals(Role.SALESMAN)) {
                if (_distributorId != null) {
                    Distributor distributor = getMandatoryPO(userLogin, _distributorId, distributorRepository);
                    distributorId = distributor.getId();
                }
            }
        }

        List<User> domains = userRepository.getList(userLogin.getClientId(), draft, active, role, distributorId, search,
                pageable, getSort());
        if (CollectionUtils.isEmpty(domains) && pageable.getPageNumber() == 0) {
            return ListDto.emptyList();
        }

        List<UserDto> dtos = new ArrayList<UserDto>(domains.size());
        for (User domain : domains) {
            dtos.add(createListSimpleDto(userLogin, domain));
        }

        long size = Long.valueOf(dtos.size());
        if (pageable != null) {
            if (pageable.getPageNumber() > 0 || size == pageable.getPageSize()) {
                size = userRepository.count(userLogin.getClientId(), draft, active, role, distributorId, search);
            }
        }

        return new ListDto<UserDto>(dtos, size);
    }

    @Override
    public UserDto getById(UserLogin userLogin, String _id) {
        User domain = getMandatoryPO(userLogin, _id, null, null, userRepository);

        BusinessAssert.notTrue(domain.isDefaultAdmin(), "cannot access default admin");

        return createListDetailDto(userLogin, domain);
    }

    @Override
    public IdDto create(UserLogin userLogin, UserCreateDto createdto) {
        BusinessAssert.notNull(createdto, "Create DTO cannot be null");

        User domain = createDomain(userLogin, createdto);

        // Check exist
        BusinessAssert.notTrue(
                userRepository.checkUsernameExist(userLogin.getClientId(), domain.getId(), createdto.getUsername()),
                String.format("User with [username=%s] already exist", createdto.getUsername()));

        BusinessAssert.notTrue(
                userRepository.checkUsernameExist(userLogin.getClientId(), domain.getId(), createdto.getUsername()),
                String.format("User with [username=%s] already exist", createdto.getUsername()));

        domain = userRepository.save(userLogin.getClientId(), domain);

        return new IdDto(domain.getId());
    }

    @Override
    public IdDto update(UserLogin userLogin, String _id, UserCreateDto createdto) {
        BusinessAssert.notNull(createdto);

        User domain = getMandatoryPO(userLogin, _id, null, true, userRepository);

        BusinessAssert.notTrue(domain.isDefaultAdmin(), "cannot access default admin");

        updateDomain(userLogin, domain, createdto);

        domain = userRepository.save(userLogin.getClientId(), domain);

        afterUpdate(userLogin, domain, createdto);

        return new IdDto(domain.getId());
    }

    @Override
    public boolean enable(UserLogin userLogin, String _id) {
        User domain = getMandatoryPO(userLogin, _id, true, true, userRepository);

        BusinessAssert.notTrue(domain.isDefaultAdmin(), "cannot access default admin");

        domain.setDraft(false);

        userRepository.save(userLogin.getClientId(), domain);

        return true;
    }

    @Override
    public boolean delete(UserLogin userLogin, String _id) {
        User domain = getMandatoryPO(userLogin, _id, true, true, userRepository);

        BusinessAssert.notTrue(domain.isDefaultAdmin(), "cannot access default admin");

        beforeDelete(userLogin, domain);

        return userRepository.delete(userLogin.getClientId(), domain.getId());
    }

    @Override
    public void setActive(UserLogin userLogin, String _id, boolean active) {
        User domain = getMandatoryPO(userLogin, _id, false, !active, userRepository);

        BusinessAssert.notTrue(domain.isDefaultAdmin(), "cannot access default admin");

        beforeSetActive(userLogin, domain, active);

        domain.setActive(active);

        userRepository.save(userLogin.getClientId(), domain);

        afterSetActive(userLogin, domain, active);
    }

    @Override
    public void resetPassword(UserLogin userLogin, String userId) {
        User user = getMandatoryPO(userLogin, userId, userRepository);
        user.setPassword(PasswordUtils.getDefaultPassword(passwordEncoder));
        userRepository.save(userLogin.getClientId(), user);
    }
    
    @Override
    public ListDto<CategorySeletionDto> getDistributorOfObserver(UserLogin userLogin, String id) {
        User user = getMandatoryPO(userLogin, id, userRepository);
        
        BusinessAssert.equals(user.getRole(), Role.OBSERVER);
        
        Set<ObjectId> distributorIds = user.getDistributorIds();
        distributorIds = distributorIds == null ? Collections.<ObjectId>emptySet() : distributorIds;
        
        List<Distributor> distributors = distributorRepository.getAll(userLogin.getClientId(), null);
        List<CategorySeletionDto> dtos = new ArrayList<>(distributors.size());
        for (Distributor distributor : distributors) {
            dtos.add(new CategorySeletionDto(distributor, distributorIds.contains(distributor.getId())));
        }
        
        return new ListDto<>(dtos);
    }
    
    @Override
    public void updateDistributorSetForObserver(UserLogin userLogin, String id, List<String> _distributorIds) {
        User user = getMandatoryPO(userLogin, id, userRepository);
        
        BusinessAssert.equals(user.getRole(), Role.OBSERVER);
        
        Set<ObjectId> distributorIds = null;
        if (_distributorIds != null && !_distributorIds.isEmpty()) {
            distributorIds = new HashSet<>();
            
            for (String _distributorId : _distributorIds) {
                distributorIds.add(getObjectId(_distributorId));
            }
            
            List<Distributor> distributors = distributorRepository.getListByIds(userLogin.getClientId(), distributorIds);
            distributorIds = getIdSet(distributors);
        }

        user.setDistributorIds(distributorIds);
        
        userRepository.save(userLogin.getClientId(), user);
    }

}
