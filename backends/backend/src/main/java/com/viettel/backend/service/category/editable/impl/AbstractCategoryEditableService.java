package com.viettel.backend.service.category.editable.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import com.viettel.backend.domain.Category;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.dto.common.CategoryCreateDto;
import com.viettel.backend.dto.common.CategoryDto;
import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.exeption.BusinessExceptionCode;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.service.category.AbstractCategoryService;
import com.viettel.backend.service.category.editable.I_EditableCategoryService;

public abstract class AbstractCategoryEditableService<DOMAIN extends Category, LIST_SIMPLE_DTO extends CategoryDto, LIST_DETAIL_DTO extends LIST_SIMPLE_DTO, CREATE_DTO extends CategoryCreateDto>
        extends AbstractCategoryService<DOMAIN>
        implements I_EditableCategoryService<LIST_SIMPLE_DTO, LIST_DETAIL_DTO, CREATE_DTO> {

    @Autowired
    private DistributorRepository distributorRepository;

    public abstract DOMAIN createDomain(UserLogin userLogin, CREATE_DTO createdto);

    public abstract void updateDomain(UserLogin userLogin, DOMAIN domain, CREATE_DTO createdto);

    public abstract LIST_SIMPLE_DTO createListSimpleDto(UserLogin userLogin, DOMAIN domain);

    public abstract LIST_DETAIL_DTO createListDetailDto(UserLogin userLogin, DOMAIN domain);

    protected void afterUpdate(UserLogin userLogin, DOMAIN domain, CREATE_DTO createdto) {
        // DO NOTHING
    }

    protected void beforeDelete(UserLogin userLogin, DOMAIN domain) {
        // DO NOTHING
    }

    protected void beforeSetActive(UserLogin userLogin, DOMAIN domain, boolean active) {
        // DO NOTHING
    }

    protected void afterSetActive(UserLogin userLogin, DOMAIN domain, boolean active) {
        // DO NOTHING
    }

    @Override
    public ListDto<LIST_SIMPLE_DTO> getList(UserLogin userLogin, String search, Boolean active, Boolean draft,
            String _distributorId, Pageable pageable) {
        Set<ObjectId> distributorToQuery = null;

        if (isUseDistributor()) {
            Set<ObjectId> accessibleDistributorIds = getIdSet(getAccessibleDistributors(userLogin));
            if (CollectionUtils.isEmpty(accessibleDistributorIds)) {
                return ListDto.emptyList();
            }

            distributorToQuery = accessibleDistributorIds;
            if (_distributorId != null) {
                ObjectId distributorId = getObjectId(_distributorId);
                if (distributorId != null) {
                    BusinessAssert.contain(accessibleDistributorIds, distributorId,
                            "Cannot access Distributor with ID=" + _distributorId);
                    distributorToQuery = Collections.singleton(distributorId);
                }
            }
        }

        List<DOMAIN> domains = getRepository().getList(userLogin.getClientId(), draft, active, distributorToQuery,
                search, pageable, getSort());
        if (CollectionUtils.isEmpty(domains) && pageable.getPageNumber() == 0) {
            return ListDto.emptyList();
        }

        List<LIST_SIMPLE_DTO> dtos = new ArrayList<LIST_SIMPLE_DTO>(domains.size());
        for (DOMAIN domain : domains) {
            dtos.add(createListSimpleDto(userLogin, domain));
        }

        long size = Long.valueOf(dtos.size());
        if (pageable != null) {
            if (pageable.getPageNumber() > 0 || size == pageable.getPageSize()) {
                size = getRepository().count(userLogin.getClientId(), draft, active, distributorToQuery, search);
            }
        }

        return new ListDto<LIST_SIMPLE_DTO>(dtos, size);
    }

    @Override
    public LIST_DETAIL_DTO getById(UserLogin userLogin, String _id) {
        DOMAIN domain = getMandatoryPO(userLogin, _id, null, null, getRepository());

        checkAccessible(userLogin, domain);

        return createListDetailDto(userLogin, domain);
    }

    @Override
    public IdDto create(UserLogin userLogin, CREATE_DTO createdto) {
        BusinessAssert.notNull(createdto, "Create DTO cannot be null");

        DOMAIN domain = createDomain(userLogin, createdto);

        // Fill distributor if null
        if (isUseDistributor()) {
            if (domain.getDistributor() == null) {
                // If Distributor available from DTO then load it, else load
                // default Distributor
                if (createdto.getDistributorId() != null) {
                    Distributor distributor = getMandatoryPO(userLogin, createdto.getDistributorId(),
                            distributorRepository);

                    domain.setDistributor(new CategoryEmbed(distributor));
                } else {
                    Distributor defaultDistributor = getDefaultDistributor(userLogin);
                    BusinessAssert.notNull(defaultDistributor,
                            "No default Distributor found, consisder provide distributorId within Create DTO");

                    domain.setDistributor(new CategoryEmbed(defaultDistributor));
                }
            }
            checkAccessible(userLogin, domain);
        } else {
            domain.setDistributor(null);
        }

        // Fill name if null
        if (domain.getName() == null) {
            checkMandatoryParams(createdto.getName());
            domain.setName(createdto.getName());
        }

        // Fill code if null
        if (isUseCode()) {
            if (!isAutoGenerateCode() && domain.getCode() == null) {
                checkMandatoryParams(createdto.getCode());
                domain.setCode(createdto.getCode().toUpperCase());
            }
        } else {
            domain.setCode(null);
        }

        // Check exist
        ObjectId distributorId = domain.getDistributor() == null ? null : domain.getDistributor().getId();

        BusinessAssert.notTrue(
                getRepository().checkNameExist(userLogin.getClientId(), domain.getId(), domain.getName(),
                        distributorId),
                BusinessExceptionCode.NAME_USED,
                String.format("Record with [name=%s] already exist", domain.getName()));
        
        if (isUseCode()) {
            BusinessAssert.notTrue(
                    getRepository().checkCodeExist(userLogin.getClientId(), domain.getId(), domain.getCode(),
                            distributorId),
                    BusinessExceptionCode.CODE_USED,
                    String.format("Record with [code=%s] already exist", domain.getCode()));
        }

        domain = getRepository().save(userLogin.getClientId(), domain);

        return new IdDto(domain.getId());
    }

    @Override
    public IdDto update(UserLogin userLogin, String _id, CREATE_DTO createdto) {
        BusinessAssert.notNull(createdto);

        DOMAIN domain = getMandatoryPO(userLogin, _id, null, true, getRepository());
        CategoryEmbed originalDistributor = domain.getDistributor();
        String originalName = domain.getName();
        String originalCode = domain.getCode();

        updateDomain(userLogin, domain, createdto);

        // Fill distributor if null
        if (isUseDistributor()) {
            if (domain.isDraft()) {
                if (createdto.getDistributorId() != null) {
                    Distributor distributor = getMandatoryPO(userLogin, createdto.getDistributorId(),
                            distributorRepository);

                    domain.setDistributor(new CategoryEmbed(distributor));
                }
                checkAccessible(userLogin, domain);
            } else {
                BusinessAssert.equals(originalDistributor, domain.getDistributor(),
                        "Field Distributor cannot be changed");
            }
        } else {
            domain.setDistributor(null);
        }

        // Fill name if null
        if (domain.isDraft()) {
            checkMandatoryParams(createdto.getName());
            domain.setName(createdto.getName());
        } else {
            BusinessAssert.equals(originalName, domain.getName(), "Field Name cannot be changed");
        }

        // Fill code if null
        if (isUseCode()) {
            if (domain.isDraft()) {
                if (!isAutoGenerateCode()) {
                    checkMandatoryParams(createdto.getCode());
                    domain.setCode(createdto.getCode().toUpperCase());
                }
            } else {
                BusinessAssert.equals(originalCode, domain.getCode(), "Field Code cannot be changed");
            }
        } else {
            domain.setCode(null);
        }

        // Check exist
        ObjectId distributorId = domain.getDistributor() == null ? null : domain.getDistributor().getId();
        
        BusinessAssert.notTrue(
                getRepository().checkNameExist(userLogin.getClientId(), domain.getId(), domain.getName(),
                        distributorId),
                BusinessExceptionCode.NAME_USED,
                String.format("Record with [name=%s] already exist", domain.getName()));
        
        if (isUseCode()) {
            BusinessAssert.notTrue(
                    getRepository().checkCodeExist(userLogin.getClientId(), domain.getId(), domain.getCode(),
                            distributorId),
                    BusinessExceptionCode.CODE_USED,
                    String.format("Record with [code=%s] already exist", domain.getCode()));
        }

        domain = getRepository().save(userLogin.getClientId(), domain);

        afterUpdate(userLogin, domain, createdto);

        return new IdDto(domain.getId());
    }

    @Override
    public boolean enable(UserLogin userLogin, String _id) {
        DOMAIN domain = getMandatoryPO(userLogin, _id, true, true, getRepository());

        checkAccessible(userLogin, domain);

        domain.setDraft(false);

        getRepository().save(userLogin.getClientId(), domain);

        return true;
    }

    @Override
    public boolean delete(UserLogin userLogin, String _id) {
        DOMAIN domain = getMandatoryPO(userLogin, _id, true, true, getRepository());

        checkAccessible(userLogin, domain);

        beforeDelete(userLogin, domain);

        return this.getRepository().delete(userLogin.getClientId(), domain.getId());
    }

    @Override
    public void setActive(UserLogin userLogin, String _id, boolean active) {
        DOMAIN domain = getMandatoryPO(userLogin, _id, false, !active, getRepository());

        checkAccessible(userLogin, domain);

        beforeSetActive(userLogin, domain, active);

        domain.setActive(active);

        getRepository().save(userLogin.getClientId(), domain);

        afterSetActive(userLogin, domain, active);
    }

    protected Sort getSort() {
        return null;
    }

}
