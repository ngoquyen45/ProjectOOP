package com.viettel.backend.service.category.editable.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.dto.category.DistributorCreateDto;
import com.viettel.backend.dto.category.DistributorDto;
import com.viettel.backend.dto.category.DistributorListDto;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CategoryRepository;
import com.viettel.backend.repository.CodeGeneratorRepository;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.category.editable.EditableDistributorService;

@RolePermission(value={ Role.ADMIN })
@Service
public class EditableDistributorServiceImpl extends
        AbstractCategoryEditableService<Distributor, DistributorListDto, DistributorDto, DistributorCreateDto>
        implements EditableDistributorService {

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private CodeGeneratorRepository codeGeneratorRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public CategoryRepository<Distributor> getRepository() {
        return distributorRepository;
    }

    @Override
    protected void beforeSetActive(UserLogin userLogin, Distributor domain, boolean active) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Distributor createDomain(UserLogin userLogin, DistributorCreateDto createdto) {
        Distributor distributor = new Distributor();

        initPOWhenCreate(Distributor.class, userLogin, distributor);

        distributor.setDraft(true);
        distributor.setCode(codeGeneratorRepository.getDistributorCode(userLogin.getClientId().toString()));

        updateDomain(userLogin, distributor, createdto);

        return distributor;
    }

    @Override
    public void updateDomain(UserLogin userLogin, Distributor distributor, DistributorCreateDto createdto) {
        checkMandatoryParams(createdto.getSupervisorId());
        User supervisor = getMandatoryPO(userLogin, createdto.getSupervisorId(), userRepository);
        distributor.setSupervisor(new UserEmbed(supervisor));
    }

    @Override
    public DistributorListDto createListSimpleDto(UserLogin userLogin, Distributor domain) {
        return new DistributorListDto(domain);
    }

    @Override
    public DistributorDto createListDetailDto(UserLogin userLogin, Distributor domain) {
        if (domain.getId() != null) {
            Set<ObjectId> salesmanIds = getIdSet(userRepository.getSalesmenByDistributors(userLogin.getClientId(),
                    Arrays.asList(domain.getId())));

            if (salesmanIds != null && !salesmanIds.isEmpty()) {
                List<String> _salesmanIds = new ArrayList<String>(salesmanIds.size());
                for (ObjectId salesmanId : salesmanIds) {
                    _salesmanIds.add(salesmanId.toString());
                }
                return new DistributorDto(domain, _salesmanIds);
            }
        }

        return new DistributorDto(domain, null);
    }

}
