package com.viettel.backend.service.report.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.Visit;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.report.DistributorVisitResultDto;
import com.viettel.backend.dto.report.SalesmanVisitResultDto;
import com.viettel.backend.dto.report.VisitResultDailyDto;
import com.viettel.backend.dto.report.VisitResultDto;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.repository.VisitRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.report.VisitReportService;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.entity.SimpleDate;
import com.viettel.backend.util.entity.SimpleDate.Period;

@RolePermission(value = { Role.ADMIN, Role.OBSERVER, Role.SUPERVISOR, Role.DISTRIBUTOR })
@Service
public class VisitReportServiceImpl extends AbstractService implements VisitReportService {

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ListDto<VisitResultDailyDto> getVisitResultDaily(UserLogin userLogin, int month, int year) {
        List<Distributor> distributors = getAccessibleDistributors(userLogin);
        Set<ObjectId> distributorIds = getIdSet(distributors);

        Period period = DateTimeUtils.getPeriodByMonth(month, year);

        List<Visit> visits = visitRepository.getVisitedsByDistributors(userLogin.getClientId(), distributorIds, period,
                null, null);

        if (visits == null || visits.isEmpty()) {
            return ListDto.emptyList();
        }

        HashMap<String, VisitResultDailyDto> reportByDate = new HashMap<String, VisitResultDailyDto>();
        HashMap<String, HashSet<ObjectId>> distributorIdsByDate = new HashMap<String, HashSet<ObjectId>>();
        HashMap<String, HashSet<ObjectId>> customerIdsByDate = new HashMap<String, HashSet<ObjectId>>();
        HashMap<String, HashSet<ObjectId>> salesmanIdsByDate = new HashMap<String, HashSet<ObjectId>>();

        for (Visit visit : visits) {
            String isoDate = visit.getStartTime().getIsoDate();

            VisitResultDailyDto report = reportByDate.get(isoDate);
            if (report == null) {
                report = new VisitResultDailyDto(isoDate, new VisitResultDto());
                reportByDate.put(isoDate, report);
            }

            HashSet<ObjectId> distributorIdsTemp = distributorIdsByDate.get(isoDate);
            if (distributorIdsTemp == null) {
                distributorIdsTemp = new HashSet<ObjectId>();
                distributorIdsByDate.put(isoDate, distributorIdsTemp);
            }
            distributorIdsTemp.add(visit.getDistributor().getId());

            HashSet<ObjectId> customerIds = customerIdsByDate.get(isoDate);
            if (customerIds == null) {
                customerIds = new HashSet<ObjectId>();
                customerIdsByDate.put(isoDate, customerIds);
            }
            customerIds.add(visit.getCustomer().getId());

            HashSet<ObjectId> salesmanIds = salesmanIdsByDate.get(isoDate);
            if (salesmanIds == null) {
                salesmanIds = new HashSet<ObjectId>();
                salesmanIdsByDate.put(isoDate, salesmanIds);
            }
            salesmanIds.add(visit.getSalesman().getId());

            report.getVisitResult().setNbVisit(report.getVisitResult().getNbVisit() + 1);
            if (visit.isErrorDuration()) {
                report.getVisitResult().setNbVisitErrorDuration(report.getVisitResult().getNbVisitErrorDuration() + 1);
            }
            if (visit.getLocationStatus() != Visit.LOCATION_STATUS_LOCATED) {
                report.getVisitResult().setNbVisitErrorPosition(report.getVisitResult().getNbVisitErrorPosition() + 1);
            }

            report.getVisitResult().setNbDistributor(distributorIdsTemp.size());
            report.getVisitResult().setNbCustomer(customerIds.size());
            report.getVisitResult().setNbSalesman(salesmanIds.size());
        }

        List<VisitResultDailyDto> dtos = new LinkedList<VisitResultDailyDto>();
        SimpleDate tempDate = period.getFromDate();
        while (tempDate.compareTo(period.getToDate()) < 0) {
            String isoDate = tempDate.getIsoDate();
            VisitResultDailyDto report = reportByDate.get(isoDate);
            if (report == null) {
                report = new VisitResultDailyDto(isoDate, new VisitResultDto());
            }
            dtos.add(report);

            tempDate = DateTimeUtils.addDays(tempDate, 1);
        }

        return new ListDto<VisitResultDailyDto>(dtos);
    }

    @Override
    public ListDto<DistributorVisitResultDto> getDistributorVisitResult(UserLogin userLogin, int month, int year) {
        List<Distributor> distributors = getAccessibleDistributors(userLogin);
        Set<ObjectId> distributorIds = getIdSet(distributors);

        Period period = DateTimeUtils.getPeriodByMonth(month, year);

        List<Visit> visits = visitRepository.getVisitedsByDistributors(userLogin.getClientId(), distributorIds, period,
                null, null);

        if (visits == null || visits.isEmpty()) {
            return ListDto.emptyList();
        }

        HashMap<ObjectId, DistributorVisitResultDto> reportByDistributor = new HashMap<ObjectId, DistributorVisitResultDto>();
        HashMap<ObjectId, HashSet<ObjectId>> customerIdsByDistributor = new HashMap<ObjectId, HashSet<ObjectId>>();
        HashMap<ObjectId, HashSet<ObjectId>> salesmanIdsByDistributor = new HashMap<ObjectId, HashSet<ObjectId>>();

        for (Visit visit : visits) {
            DistributorVisitResultDto report = reportByDistributor.get(visit.getDistributor().getId());
            if (report == null) {
                report = new DistributorVisitResultDto(visit.getDistributor(), new VisitResultDto());
                reportByDistributor.put(visit.getDistributor().getId(), report);
            }

            HashSet<ObjectId> customerIds = customerIdsByDistributor.get(visit.getDistributor().getId());
            if (customerIds == null) {
                customerIds = new HashSet<ObjectId>();
                customerIdsByDistributor.put(visit.getDistributor().getId(), customerIds);
            }
            customerIds.add(visit.getCustomer().getId());

            HashSet<ObjectId> salesmanIds = salesmanIdsByDistributor.get(visit.getDistributor().getId());
            if (salesmanIds == null) {
                salesmanIds = new HashSet<ObjectId>();
                salesmanIdsByDistributor.put(visit.getDistributor().getId(), salesmanIds);
            }
            salesmanIds.add(visit.getCreatedBy().getId());

            report.getVisitResult().setNbVisit(report.getVisitResult().getNbVisit() + 1);
            if (visit.isErrorDuration()) {
                report.getVisitResult().setNbVisitErrorDuration(report.getVisitResult().getNbVisitErrorDuration() + 1);
            }
            if (visit.getLocationStatus() != Visit.LOCATION_STATUS_LOCATED) {
                report.getVisitResult().setNbVisitErrorPosition(report.getVisitResult().getNbVisitErrorPosition() + 1);
            }

            report.getVisitResult().setNbCustomer(customerIds.size());
            report.getVisitResult().setNbSalesman(salesmanIds.size());
        }

        List<DistributorVisitResultDto> dtos = new ArrayList<DistributorVisitResultDto>(distributors.size());
        for (Distributor distributor : distributors) {
            DistributorVisitResultDto report = reportByDistributor.get(distributor.getId());
            if (report == null) {
                report = new DistributorVisitResultDto(distributor, new VisitResultDto());
            }
            dtos.add(report);
        }

        return new ListDto<DistributorVisitResultDto>(dtos);
    }

    @Override
    public ListDto<SalesmanVisitResultDto> getSalesmanVisitResult(UserLogin userLogin, int month, int year) {
        List<Distributor> distributors = getAccessibleDistributors(userLogin);
        Set<ObjectId> distributorIds = getIdSet(distributors);

        Period period = DateTimeUtils.getPeriodByMonth(month, year);

        List<Visit> visits = visitRepository.getVisitedsByDistributors(userLogin.getClientId(), distributorIds, period,
                null, null);

        if (visits == null || visits.isEmpty()) {
            return ListDto.emptyList();
        }

        HashMap<ObjectId, SalesmanVisitResultDto> reportBySalesman = new HashMap<ObjectId, SalesmanVisitResultDto>();
        for (Visit visit : visits) {
            SalesmanVisitResultDto report = reportBySalesman.get(visit.getCreatedBy().getId());
            if (report == null) {
                report = new SalesmanVisitResultDto(visit.getCreatedBy(), new VisitResultDto());
                reportBySalesman.put(visit.getCreatedBy().getId(), report);
            }

            report.getVisitResult().setNbVisit(report.getVisitResult().getNbVisit() + 1);
            if (visit.isErrorDuration()) {
                report.getVisitResult().setNbVisitErrorDuration(report.getVisitResult().getNbVisitErrorDuration() + 1);
            }
            if (visit.getLocationStatus() != Visit.LOCATION_STATUS_LOCATED) {
                report.getVisitResult().setNbVisitErrorPosition(report.getVisitResult().getNbVisitErrorPosition() + 1);
            }
        }

        List<User> salesmen = userRepository.getSalesmenByDistributors(userLogin.getClientId(), distributorIds);
        List<SalesmanVisitResultDto> dtos = new ArrayList<SalesmanVisitResultDto>(salesmen.size());
        for (User salesman : salesmen) {
            SalesmanVisitResultDto report = reportBySalesman.get(salesman.getId());
            if (report == null) {
                report = new SalesmanVisitResultDto(salesman, new VisitResultDto());
            }
            dtos.add(report);

            reportBySalesman.remove(salesman.getId());
        }

        // ADD NHUNG SALESMAN MA CO ORDER TRONG THANG TUY NHIEN KHONG CON THUOC
        // DISTRIBUTOR NAY NUA
        for (SalesmanVisitResultDto report : reportBySalesman.values()) {
            dtos.add(report);
        }

        return new ListDto<SalesmanVisitResultDto>(dtos);
    }

}
