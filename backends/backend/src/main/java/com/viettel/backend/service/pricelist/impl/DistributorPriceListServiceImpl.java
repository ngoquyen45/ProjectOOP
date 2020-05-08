package com.viettel.backend.service.pricelist.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.Product;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.dto.common.I_ProductPhotoFactory;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.pricelist.DistributorPriceDto;
import com.viettel.backend.dto.pricelist.DistributorPriceListCreateDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.DistributorPriceListRepository;
import com.viettel.backend.repository.ProductRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.pricelist.DistributorPriceListService;

@RolePermission(value = { Role.DISTRIBUTOR })
@Service
public class DistributorPriceListServiceImpl extends AbstractService implements DistributorPriceListService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DistributorPriceListRepository distributorPriceListRepository;

    @Override
    public ListDto<DistributorPriceDto> getPriceList(UserLogin userLogin) {
        Distributor distributor = getDefaultDistributor(userLogin);
        Map<ObjectId, BigDecimal> distributorPriceList = distributorPriceListRepository
                .getPriceList(userLogin.getClientId(), distributor.getId());

        I_ProductPhotoFactory productPhotoFactory = getProductPhotoFactory(userLogin);

        List<Product> products = productRepository.getAll(userLogin.getClientId(), null);
        List<DistributorPriceDto> dtos = new ArrayList<>(products.size());
        for (Product product : products) {
            dtos.add(new DistributorPriceDto(product, productPhotoFactory, distributorPriceList.get(product.getId())));
        }

        return new ListDto<>(dtos);
    }

    @Override
    public void savePriceList(UserLogin userLogin, DistributorPriceListCreateDto createDto) {
        Distributor distributor = getDefaultDistributor(userLogin);

        BusinessAssert.notNull(createDto);

        Map<ObjectId, Product> productMap = productRepository.getProductMap(userLogin.getClientId(), true);

        Map<String, BigDecimal> _priceList = createDto.getPriceList();
        Map<ObjectId, BigDecimal> priceList = new HashMap<>();

        for (Entry<String, BigDecimal> entry : _priceList.entrySet()) {
            if (entry.getValue() != null) {
                BusinessAssert.isTrue(entry.getValue().compareTo(BigDecimal.ZERO) >= 0);
            }

            ObjectId productId = getObjectId(entry.getKey());
            BusinessAssert.isTrue(productMap.containsKey(productId));

            priceList.put(productId, entry.getValue());
        }

        distributorPriceListRepository.savePriceList(userLogin.getClientId(), distributor.getId(), priceList);
    }

}
