package com.viettel.backend.service.exchangereturn.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.ExchangeReturn;
import com.viettel.backend.domain.Product;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.domain.embed.CustomerEmbed;
import com.viettel.backend.domain.embed.ProductEmbed;
import com.viettel.backend.domain.embed.ProductQuantity;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.dto.common.I_ProductPhotoFactory;
import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.exchangereturn.ExchangeReturnCreateDto;
import com.viettel.backend.dto.exchangereturn.ExchangeReturnCreateDto.ExchangeReturnDetailCreateDto;
import com.viettel.backend.dto.exchangereturn.ExchangeReturnDto;
import com.viettel.backend.dto.exchangereturn.ExchangeReturnSimpleDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CustomerRepository;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.repository.ExchangeReturnRepository;
import com.viettel.backend.repository.ProductRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.exchangereturn.ExchangeReturnCreatingService;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.entity.SimpleDate.Period;

@RolePermission(value = { Role.SALESMAN })
@Service
public class ExchangeReturnCreatingServiceImpl extends AbstractService implements ExchangeReturnCreatingService {

    @Autowired
    private ExchangeReturnRepository exchangeReturnRepository;

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ListDto<ExchangeReturnSimpleDto> getExchangeProductToday(UserLogin userLogin) {
        return getExchangeReturnToday(userLogin, true);
    }

    @Override
    public ExchangeReturnDto getExchangeProduct(UserLogin userLogin, String id) {
        return getDetail(userLogin, true, id);
    }
    
    @Override
    public IdDto createExchangeProduct(UserLogin userLogin, ExchangeReturnCreateDto createDto) {
        return createExchangeReturn(userLogin, true, createDto);
    }

    @Override
    public ListDto<ExchangeReturnSimpleDto> getReturnProductToday(UserLogin userLogin) {
        return getExchangeReturnToday(userLogin, false);
    }

    @Override
    public ExchangeReturnDto getReturnProduct(UserLogin userLogin, String id) {
        return getDetail(userLogin, false, id);
    }
    
    @Override
    public IdDto createReturnProduct(UserLogin userLogin, ExchangeReturnCreateDto createDto) {
        return createExchangeReturn(userLogin, false, createDto);
    }

    // PRIVATE
    private ListDto<ExchangeReturnSimpleDto> getExchangeReturnToday(UserLogin userLogin, boolean exchange) {
        Period todayPeriod = DateTimeUtils.getPeriodToday();
        Sort sort = new Sort(Direction.DESC, ExchangeReturn.COLUMNNAME_CREATED_TIME_VALUE);

        List<ExchangeReturn> exchangeReturns = exchangeReturnRepository.getListByCreatedBys(userLogin.getClientId(),
                exchange, Collections.singleton(userLogin.getUserId()), todayPeriod, null, sort);

        if (exchangeReturns == null || exchangeReturns.isEmpty()) {
            return ListDto.emptyList();
        }
        
        List<ExchangeReturnSimpleDto> dtos = new ArrayList<>(exchangeReturns.size());
        for (ExchangeReturn exchangeReturn : exchangeReturns) {
            dtos.add(new ExchangeReturnSimpleDto(exchangeReturn));
        }

        return new ListDto<>(dtos);
    }
    
    private ExchangeReturnDto getDetail(UserLogin userLogin, boolean exchange, String id) {
        ExchangeReturn exchangeReturn = getMandatoryPO(userLogin, id, exchangeReturnRepository);
        
        BusinessAssert.equals(exchangeReturn.isExchange(), exchange);
        BusinessAssert.equals(exchangeReturn.getCreatedBy().getId(), userLogin.getUserId());
        
        I_ProductPhotoFactory productPhotoFactory = getProductPhotoFactory(userLogin);
        
        return new ExchangeReturnDto(exchangeReturn, productPhotoFactory);
    }

    private IdDto createExchangeReturn(UserLogin userLogin, boolean exchange, ExchangeReturnCreateDto createDto) {
        BusinessAssert.notNull(createDto);
        
        Distributor distributor = null;
        if (createDto.getDistributorId() != null) {
            distributor = getMandatoryPO(userLogin, createDto.getDistributorId(), distributorRepository);
        } else {
            distributor = getDefaultDistributor(userLogin);
            BusinessAssert.notNull(distributor, "Distributor is required");
        }

        Customer customer = getMandatoryPO(userLogin, createDto.getCustomerId(), customerRepository);
        BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId(), customer),
                "distributor or customer is not accessible");

        User currentUser = getCurrentUser(userLogin);

        Map<ObjectId, Product> productMap = productRepository.getProductMap(userLogin.getClientId(), null);
        List<ProductQuantity> productQuantities = new ArrayList<>(createDto.getDetails().size());
        BigDecimal quantity = BigDecimal.ZERO;
        for (ExchangeReturnDetailCreateDto detail : createDto.getDetails()) {
            ObjectId productId = getObjectId(detail.getProductId());
            Product product = productMap.get(productId);
            BusinessAssert.notNull(product);

            ProductQuantity productQuantity = new ProductQuantity();
            productQuantity.setProduct(new ProductEmbed(product));
            productQuantity.setQuantity(detail.getQuantity());

            productQuantities.add(productQuantity);
            
            quantity = quantity.add(detail.getQuantity());
        }

        ExchangeReturn exchangeReturn = new ExchangeReturn();
        initPOWhenCreate(ExchangeReturn.class, userLogin, exchangeReturn);
        exchangeReturn.setExchange(exchange);
        exchangeReturn.setCreatedTime(DateTimeUtils.getCurrentTime());
        exchangeReturn.setDistributor(new CategoryEmbed(distributor));
        exchangeReturn.setCustomer(new CustomerEmbed(customer));
        exchangeReturn.setCreatedBy(new UserEmbed(currentUser));
        exchangeReturn.setQuantity(quantity);
        exchangeReturn.setDetails(productQuantities);
        exchangeReturn = exchangeReturnRepository.save(userLogin.getClientId(), exchangeReturn);

        return new IdDto(exchangeReturn);
    }

}
