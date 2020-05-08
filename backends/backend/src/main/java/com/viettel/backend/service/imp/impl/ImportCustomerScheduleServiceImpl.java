package com.viettel.backend.service.imp.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.Route;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.Schedule;
import com.viettel.backend.domain.embed.ScheduleItem;
import com.viettel.backend.dto.imp.ImportConfirmDto;
import com.viettel.backend.dto.imp.ImportConfirmDto.RowData;
import com.viettel.backend.dto.imp.ImportResultDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CustomerRepository;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.repository.RouteRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractImportService;
import com.viettel.backend.service.imp.ImportCustomerScheduleService;
import com.viettel.backend.util.StringUtils;

@RolePermission(value = { Role.ADMIN, Role.SUPERVISOR, Role.DISTRIBUTOR })
@Service
public class ImportCustomerScheduleServiceImpl extends AbstractImportService implements ImportCustomerScheduleService {

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Override
    public byte[] getTemplate(UserLogin userLogin, String _distributorId, String lang) {
        lang = getLang(lang);
        
        Distributor distributor = getDefaultDistributor(userLogin);
        if (distributor == null) {
            distributor = getMandatoryPO(userLogin, _distributorId, distributorRepository);
            BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId()), "distributor not accessible");
        }
                
        List<Route> routes = routeRepository.getAll(userLogin.getClientId(), distributor.getId());
        HashMap<ObjectId, Route> routeMap = getPOMap(routes);
        
        List<Customer> customers = customerRepository.getAll(userLogin.getClientId(), distributor.getId());

        Config config = getConfig(userLogin);

        // Finds the workbook instance for XLSX file
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet scheduleSheet = workbook.createSheet(translate(lang, "schedule"));

        XSSFRow row = scheduleSheet.createRow(0);
        row.createCell(0).setCellValue(translate(lang, "code"));
        row.createCell(1).setCellValue(translate(lang, "name"));
        row.createCell(2).setCellValue(translate(lang, "area"));
        row.createCell(3).setCellValue(translate(lang, "route"));
        row.createCell(4).setCellValue(translate(lang, "monday"));
        row.createCell(5).setCellValue(translate(lang, "tuesday"));
        row.createCell(6).setCellValue(translate(lang, "wednesday"));
        row.createCell(7).setCellValue(translate(lang, "thursday"));
        row.createCell(8).setCellValue(translate(lang, "friday"));
        row.createCell(9).setCellValue(translate(lang, "saturday"));
        row.createCell(10).setCellValue(translate(lang, "sunday"));

        if (config.getNumberWeekOfFrequency() > 1) {
            for (int i = 1; i <= config.getNumberWeekOfFrequency(); i++) {
                row.createCell(10 + i).setCellValue("W" + i);
            }
        }

        int index = 1;
        for (Customer customer : customers) {
            row = scheduleSheet.createRow(index);

            Schedule schedule = customer.getSchedule();
            if (schedule != null) {
                Route route = routeMap.get(schedule.getRouteId());

                if (schedule.getItems() != null) {
                    for (ScheduleItem item : schedule.getItems()) {
                        fillDataToRow(row, config, customer, route, item);
                        index++;
                    }
                }
            } else {
                fillDataToRow(row, config, customer, null, null);
                index++;
            }
        }

        index = 0;
        XSSFSheet routeSheet = workbook.createSheet(translate(lang, "route"));
        for (Route route : routes) {
            row = routeSheet.createRow(index);
            row.createCell(0).setCellValue(route.getName());
            index++;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            workbook.write(baos);
            workbook.close();

            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void fillDataToRow(XSSFRow row, Config config, Customer customer, Route route, ScheduleItem item) {
        row.createCell(0).setCellValue(customer.getCode());
        row.createCell(1).setCellValue(customer.getName());
        row.createCell(2).setCellValue(customer.getArea().getName());

        if (route != null) {
            row.createCell(3).setCellValue(route.getName());
        }

        if (item != null) {
            row.createCell(4).setCellValue(!item.isMonday() ? null : "x");
            row.createCell(5).setCellValue(!item.isTuesday() ? null : "x");
            row.createCell(6).setCellValue(!item.isWednesday() ? null : "x");
            row.createCell(7).setCellValue(!item.isThursday() ? null : "x");
            row.createCell(8).setCellValue(!item.isFriday() ? null : "x");
            row.createCell(9).setCellValue(!item.isSaturday() ? null : "x");
            row.createCell(10).setCellValue(!item.isSunday() ? null : "x");

            if (config.getFirstDayOfWeek() > 1) {
                for (int i = 1; i <= config.getNumberWeekOfFrequency(); i++) {
                    if (item.getWeeks() != null && item.getWeeks().contains(i)) {
                        row.createCell(10 + i).setCellValue("x");
                    }
                }
            }
        }
    }

    private I_CellValidator[] getValidators(UserLogin userLogin, ObjectId distributorId) {
        Config config = getConfig(userLogin);

        List<Customer> customers = customerRepository.getAll(userLogin.getClientId(), distributorId);
        Map<String, Customer> customerByCode = new HashMap<String, Customer>();
        for (Customer customer : customers) {
            customerByCode.put(customer.getCode().trim().toUpperCase(), customer);
        }

        List<Route> routes = routeRepository.getAll(userLogin.getClientId(), distributorId);
        Map<String, Route> routeByName = new HashMap<String, Route>();
        for (Route route : routes) {
            routeByName.put(route.getName().trim().toUpperCase(), route);
        }

        LinkedList<I_CellValidator> validators = new LinkedList<AbstractImportService.I_CellValidator>();
        validators.add(new MultiCellValidator(new StringMandatoryCellValidator(),
                new ReferenceCellValidator<Customer>(customerByCode)));
        validators.add(new NoValidator());
        validators.add(new NoValidator());
        validators.add(new ReferenceCellValidator<Route>(routeByName));
        validators.add(null);
        validators.add(null);
        validators.add(null);
        validators.add(null);
        validators.add(null);
        validators.add(null);
        validators.add(null);

        if (config.getNumberWeekOfFrequency() > 1) {
            for (int i = 0; i < config.getNumberWeekOfFrequency(); i++) {
                validators.add(null);
            }
        }

        I_CellValidator[] array = new I_CellValidator[validators.size()];
        validators.toArray(array);
        return array;
    }

    @Override
    public ImportConfirmDto verify(UserLogin userLogin, String _distributorId, String fileId) {
        Distributor distributor = getDefaultDistributor(userLogin);
        if (distributor == null) {
            distributor = getMandatoryPO(userLogin, _distributorId, distributorRepository);
            BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId()), "distributor not accessible");
        }
        
        I_CellValidator[] validators = getValidators(userLogin, distributor.getId());
        return getErrorRows(userLogin, fileId, validators);
    }

    @Override
    public ImportResultDto doImport(UserLogin userLogin, String _distributorId, String fileId) {
        Distributor distributor = getDefaultDistributor(userLogin);
        if (distributor == null) {
            distributor = getMandatoryPO(userLogin, _distributorId, distributorRepository);
            BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId()), "distributor not accessible");
        }
        
        Config config = getConfig(userLogin);
        
        I_CellValidator[] validators = getValidators(userLogin, distributor.getId());

        ImportConfirmDto dto = getValidRows(userLogin, fileId, validators);

        for (RowData row : dto.getRowDatas()) {
            Customer customer = (Customer) row.getDatas().get(0);
            Route route = (Route) row.getDatas().get(3);
            if (route != null) {
                ScheduleItem item = new ScheduleItem();
                item.setMonday(!StringUtils.isNullOrEmpty((String) row.getDatas().get(4), true));
                item.setTuesday(!StringUtils.isNullOrEmpty((String) row.getDatas().get(5), true));
                item.setWednesday(!StringUtils.isNullOrEmpty((String) row.getDatas().get(6), true));
                item.setThursday(!StringUtils.isNullOrEmpty((String) row.getDatas().get(7), true));
                item.setFriday(!StringUtils.isNullOrEmpty((String) row.getDatas().get(8), true));
                item.setSaturday(!StringUtils.isNullOrEmpty((String) row.getDatas().get(9), true));
                item.setSunday(!StringUtils.isNullOrEmpty((String) row.getDatas().get(10), true));

                boolean weeksValid = true;
                if (config.getNumberWeekOfFrequency() > 1) {
                    weeksValid = false;
                    List<Integer> weeks = new LinkedList<Integer>();
                    for (int i = 1; i <= config.getNumberWeekOfFrequency(); i++) {
                        if (!StringUtils.isNullOrEmpty((String) row.getDatas().get(10 + i))) {
                            weeks.add(i);
                            weeksValid = true;
                        }
                    }
                    item.setWeeks(weeks);
                }

                if ((item.isMonday() || item.isTuesday() || item.isWednesday() || item.isThursday() || item.isFriday()
                        || item.isSaturday() || item.isSunday()) && weeksValid) {
                    Schedule schedule = new Schedule();
                    schedule.setRouteId(route.getId());
                    schedule.setItems(Arrays.asList(item));

                    customer.setSchedule(schedule);
                } else {
                    customer.setSchedule(null);
                }
            } else {
                customer.setSchedule(null);
            }

            customerRepository.save(userLogin.getClientId(), customer);
        }

        return new ImportResultDto(dto.getTotal(), dto.getRowDatas().size());
    }

}
