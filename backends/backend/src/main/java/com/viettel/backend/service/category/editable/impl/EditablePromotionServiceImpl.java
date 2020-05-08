package com.viettel.backend.service.category.editable.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Product;
import com.viettel.backend.domain.Promotion;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.PromotionCondition;
import com.viettel.backend.domain.embed.PromotionDetail;
import com.viettel.backend.domain.embed.PromotionReward;
import com.viettel.backend.dto.category.PromotionCreateDto;
import com.viettel.backend.dto.category.PromotionCreateDto.PromotionDetailCreateDto;
import com.viettel.backend.dto.category.PromotionDto;
import com.viettel.backend.dto.category.PromotionListDto;
import com.viettel.backend.dto.common.I_ProductPhotoFactory;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CategoryRepository;
import com.viettel.backend.repository.ProductRepository;
import com.viettel.backend.repository.PromotionRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.category.editable.EditablePromotionService;
import com.viettel.backend.util.entity.SimpleDate;

@RolePermission(value={ Role.ADMIN })
@Service
public class EditablePromotionServiceImpl extends
        AbstractCategoryEditableService<Promotion, PromotionListDto, PromotionDto, PromotionCreateDto> implements
        EditablePromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public CategoryRepository<Promotion> getRepository() {
        return promotionRepository;
    }
    
    @Override
    protected void beforeSetActive(UserLogin userLogin, Promotion domain, boolean active) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Promotion createDomain(UserLogin userLogin, PromotionCreateDto createdto) {
        Promotion domain = new Promotion();
        initPOWhenCreate(Promotion.class, userLogin, domain);
        domain.setDraft(true);

        updateDomain(userLogin, domain, createdto);

        return domain;
    }

    @Override
    public void updateDomain(UserLogin userLogin, Promotion domain, PromotionCreateDto createdto) {
        // Not allow update non draft record
        BusinessAssert.isTrue(domain.isDraft(), "Modify non-draft record are not allowed");

        checkMandatoryParams(createdto.getDescription(), createdto.getApplyFor(), createdto.getStartDate(),
                createdto.getEndDate(), createdto.getDetails());

        SimpleDate startDate = getMandatoryIsoDate(createdto.getStartDate());
        SimpleDate endDate = getMandatoryIsoDate(createdto.getEndDate());
        BusinessAssert.isTrue(startDate.compareTo(endDate) <= 0, "startDate > endDate");

        domain.setDescription(createdto.getDescription());
        domain.setApplyFor(createdto.getApplyFor());
        domain.setStartDate(startDate);
        domain.setEndDate(endDate);

        List<PromotionDetail> details = new ArrayList<PromotionDetail>(createdto.getDetails().size());
        for (PromotionDetailCreateDto detailDto : createdto.getDetails()) {
            BusinessAssert.notNull(detailDto, "detailt promotion null");
            BusinessAssert.isTrue(detailDto.isValid(), "detailt promotion invalid");

            Product conditionProduct = getMandatoryPO(userLogin, detailDto.getCondition().getProductId(),
                    productRepository);

            PromotionReward reward = new PromotionReward();
            reward.setPercentage(detailDto.getReward().getPercentage());
            reward.setQuantity(detailDto.getReward().getQuantity());
            if (detailDto.getReward().getProductId() != null) {
                Product rewardProduct = getMandatoryPO(userLogin, detailDto.getReward().getProductId(),
                        productRepository);
                reward.setProductId(rewardProduct.getId());
            } else {
                reward.setProductText(detailDto.getReward().getProductText());
            }

            PromotionDetail detail = new PromotionDetail();
            detail.setId(new ObjectId());
            detail.setType(detailDto.getType());
            detail.setCondition(new PromotionCondition(conditionProduct.getId(), detailDto.getCondition().getQuantity()));
            detail.setReward(reward);

            details.add(detail);
        }
        domain.setDetails(details);
    }

    @Override
    public PromotionListDto createListSimpleDto(UserLogin userLogin, Promotion domain) {
        return new PromotionListDto(domain);
    }

    @Override
    public PromotionDto createListDetailDto(final UserLogin userLogin, Promotion domain) {
        final Map<ObjectId, Product> productMap = productRepository.getProductMap(userLogin.getClientId(), null);
        PromotionDto dto = new PromotionDto(domain, productMap, new I_ProductPhotoFactory() {

            @Override
            public String getPhoto(ObjectId productId) {
                Product product = productMap.get(productId);
                if (product == null || product.getPhoto() == null) {
                    return getConfig(userLogin).getProductPhoto();
                }
                return product.getPhoto();
            }

        });
        
        return dto;
    }

}
