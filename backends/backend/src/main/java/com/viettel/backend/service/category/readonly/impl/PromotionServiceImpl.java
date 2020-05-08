package com.viettel.backend.service.category.readonly.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Promotion;
import com.viettel.backend.dto.category.PromotionListDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.PromotionRepository;
import com.viettel.backend.service.category.readonly.PromotionService;
import com.viettel.backend.service.common.AbstractService;

@Service
public class PromotionServiceImpl extends AbstractService implements PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;
    
    @Override
    public ListDto<PromotionListDto> getPromotionsAvailableByCustomer(UserLogin userLogin, String _customerId) {
        List<Promotion> promotions = promotionRepository.getPromotionsAvailableToday(userLogin.getClientId());
        List<PromotionListDto> dtos = new ArrayList<PromotionListDto>(promotions.size());
        for (Promotion promotion : promotions) {
            dtos.add(new PromotionListDto(promotion));
        }
        
        return new ListDto<PromotionListDto>(dtos);
    }
    
}
