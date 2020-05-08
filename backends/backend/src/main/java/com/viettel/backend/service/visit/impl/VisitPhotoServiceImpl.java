package com.viettel.backend.service.visit.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.VisitPhoto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.visit.VisitPhotoDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.repository.VisitPhotoRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.visit.VisitPhotoService;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.entity.SimpleDate;
import com.viettel.backend.util.entity.SimpleDate.Period;

@RolePermission(value = { Role.ADMIN, Role.OBSERVER, Role.SUPERVISOR, Role.DISTRIBUTOR })
@Service
public class VisitPhotoServiceImpl extends AbstractService implements VisitPhotoService {

    @Autowired
    private VisitPhotoRepository visitPhotoRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ListDto<VisitPhotoDto> getVisitPhotos(UserLogin userLogin, String salesmanId, String _fromDate,
            String _toDate) {
        User salesman = getMandatoryPO(userLogin, salesmanId, userRepository);
        BusinessAssert.isTrue(checkAccessible(userLogin, salesman.getDistributor().getId()), "salesman not accessible");

        SimpleDate fromDate = getMandatoryIsoDate(_fromDate);
        SimpleDate toDate = getMandatoryIsoDate(_toDate);
        BusinessAssert.isTrue(fromDate.compareTo(toDate) <= 0, "fromDate > toDate");
        BusinessAssert.isTrue(DateTimeUtils.addMonths(fromDate, 1).compareTo(toDate) >= 0, "greater than 1 month");

        Period period = new Period(fromDate, DateTimeUtils.addDays(toDate, 1));

        List<VisitPhoto> photos = visitPhotoRepository.getVisitPhoto(userLogin.getClientId(), salesman.getId(), period);

        if (photos == null || photos.isEmpty()) {
            return ListDto.emptyList();
        }

        List<VisitPhotoDto> dtos = new ArrayList<>(photos.size());
        for (VisitPhoto photo : photos) {
            dtos.add(new VisitPhotoDto(photo));
        }

        return new ListDto<VisitPhotoDto>(dtos);
    }

}
