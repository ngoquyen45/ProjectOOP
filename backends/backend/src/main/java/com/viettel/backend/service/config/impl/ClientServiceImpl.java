package com.viettel.backend.service.config.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.viettel.backend.domain.CalendarConfig;
import com.viettel.backend.domain.Client;
import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.dto.common.CategoryDto;
import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.config.ClientCreateDto;
import com.viettel.backend.dto.config.ClientDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.exeption.BusinessExceptionCode;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CalendarConfigRepository;
import com.viettel.backend.repository.CategoryRepository;
import com.viettel.backend.repository.ClientRepository;
import com.viettel.backend.repository.ConfigRepository;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.category.AbstractCategoryService;
import com.viettel.backend.service.common.GenerateDataService;
import com.viettel.backend.service.common.MasterDataService;
import com.viettel.backend.service.config.ClientService;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.PasswordUtils;
import com.viettel.backend.util.entity.SimpleDate;

@RolePermission(value = { Role.SUPER_ADMIN, Role.SUPPORTER })
@Service
public class ClientServiceImpl extends AbstractCategoryService<Client> implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private CalendarConfigRepository calendarConfigRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MasterDataService masterDataService;

    @Autowired
    private GenerateDataService generateDataService;

    @Override
    public CategoryRepository<Client> getRepository() {
        return clientRepository;
    }

    @Override
    public ListDto<CategoryDto> getList(UserLogin userLogin, String search, Boolean active, Boolean draft,
            String distributorId, Pageable pageable) {
        List<Client> clients = getRepository().getList(userLogin.getClientId(), draft, active, null, search, pageable,
                null);

        if (CollectionUtils.isEmpty(clients) && pageable.getPageNumber() == 0) {
            return ListDto.emptyList();
        }

        List<CategoryDto> dtos = new ArrayList<CategoryDto>(clients.size());
        for (Client client : clients) {
            dtos.add(new CategoryDto(client));
        }

        long size = Long.valueOf(dtos.size());
        if (pageable != null) {
            if (pageable.getPageNumber() > 0 || size == pageable.getPageSize()) {
                size = getRepository().count(userLogin.getClientId(), draft, active, null, search);
            }
        }

        return new ListDto<CategoryDto>(dtos, size);
    }

    @Override
    public ClientDto getById(UserLogin userLogin, String _id) {
        Client client = getMandatoryPO(userLogin, _id, clientRepository);

        Config config = configRepository.getConfig(client.getId());
        CalendarConfig calendarConfig = calendarConfigRepository.getCalendarConfig(client.getId());

        return new ClientDto(client, config, calendarConfig);
    }

    @Override
    public IdDto create(UserLogin userLogin, ClientCreateDto dto) {
        BusinessAssert.notNull(dto, "Create DTO cannot be null");
        checkMandatoryParams(dto.getName(), dto.getCode(), dto.getClientConfig(), dto.getCalendarConfig(),
                dto.getCalendarConfig().getWorkingDays());

        Client client = new Client();

        initPOWhenCreate(Client.class, userLogin, client);

        client.setDraft(false);
        client.setName(dto.getName());
        client.setCode(dto.getCode().toUpperCase());

        // Check exist
        BusinessAssert.notTrue(
                getRepository().checkNameExist(userLogin.getClientId(), client.getId(), client.getName(), null),
                BusinessExceptionCode.NAME_USED,
                String.format("Record with [name=%s] already exist", client.getName()));

        BusinessAssert.notTrue(
                getRepository().checkCodeExist(userLogin.getClientId(), client.getId(), client.getCode(), null),
                BusinessExceptionCode.CODE_USED,
                String.format("Record with [code=%s] already exist", client.getCode()));

        client = getRepository().save(userLogin.getClientId(), client);

        // Create Admin
        User admin = new User();
        admin.setClientId(client.getId());
        admin.setActive(true);
        admin.setDraft(false);
        admin.setPassword(PasswordUtils.getDefaultPassword(passwordEncoder));
        admin.setUsername(client.getCode(), "admin");
        admin.setFullname("Admin");
        admin.setRole(Role.ADMIN);
        admin.setDefaultAdmin(true);
        admin = userRepository.save(client.getId(), admin);

        // Client Config
        Config config = new Config();
        config.setClientId(client.getId());
        config.setActive(true);

        config.setVisitDistanceKPI(dto.getClientConfig().getVisitDistanceKPI());
        config.setVisitDurationKPI(dto.getClientConfig().getVisitDurationKPI());
        config.setCanEditCustomerLocation(dto.getClientConfig().isCanEditCustomerLocation());
        config.setLocation(dto.getClientConfig().getLocation());

        config = configRepository.save(client.getId(), config);

        // Calendar config
        CalendarConfig calendarConfig = new CalendarConfig();
        calendarConfig.setClientId(client.getId());
        calendarConfig.setActive(true);

        calendarConfig.setWorkingDays(dto.getCalendarConfig().getWorkingDays());
        if (dto.getCalendarConfig().getHolidays() != null) {
            List<SimpleDate> holidays = new ArrayList<SimpleDate>(dto.getCalendarConfig().getHolidays().size());
            for (String holiday : dto.getCalendarConfig().getHolidays()) {
                holidays.add(getMandatoryIsoDate(holiday));
            }

            Collections.sort(holidays);

            calendarConfig.setHolidays(holidays);
        }

        calendarConfig = calendarConfigRepository.save(client.getId(), calendarConfig);

        return new IdDto(client.getId());
    }

    @Override
    public IdDto update(UserLogin userLogin, String _id, ClientCreateDto dto) {
        BusinessAssert.notNull(dto, "Create DTO cannot be null");
        checkMandatoryParams(dto.getClientConfig(), dto.getCalendarConfig(), dto.getCalendarConfig().getWorkingDays());

        Client client = getMandatoryPO(userLogin, _id, getRepository());

        //
        // Client Config
        Config config = configRepository.getConfig(client.getId());

        if (config == null) {
            config = new Config();
            config.setClientId(client.getId());
            config.setActive(true);
        }

        config.setVisitDistanceKPI(dto.getClientConfig().getVisitDistanceKPI());
        config.setVisitDurationKPI(dto.getClientConfig().getVisitDurationKPI());
        config.setCanEditCustomerLocation(dto.getClientConfig().isCanEditCustomerLocation());
        config.setLocation(dto.getClientConfig().getLocation());

        config = configRepository.save(client.getId(), config);

        //
        // Calendar Config
        CalendarConfig calendarConfig = calendarConfigRepository.getCalendarConfig(client.getId());

        if (calendarConfig == null) {
            calendarConfig = new CalendarConfig();
            calendarConfig.setClientId(client.getId());
            calendarConfig.setActive(true);
        }

        calendarConfig.setWorkingDays(dto.getCalendarConfig().getWorkingDays());
        if (dto.getCalendarConfig().getHolidays() != null) {
            List<SimpleDate> holidays = new ArrayList<SimpleDate>(dto.getCalendarConfig().getHolidays().size());
            for (String holiday : dto.getCalendarConfig().getHolidays()) {
                holidays.add(getMandatoryIsoDate(holiday));
            }

            Collections.sort(holidays);

            calendarConfig.setHolidays(holidays);
        }

        calendarConfig = calendarConfigRepository.save(client.getId(), calendarConfig);

        return new IdDto(client.getId());
    }

    @Override
    public boolean enable(UserLogin userLogin, String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(UserLogin userLogin, String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setActive(UserLogin userLogin, String id, boolean active) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void createSampleMasterData(UserLogin userLogin, String clientId) {
        Client client = getMandatoryPO(userLogin, clientId, clientRepository);
        ClassPathResource classPathResource = new ClassPathResource("demo-data.xlsx");
        try {
            masterDataService.importMasterData(client.getId(), classPathResource.getInputStream());
        } catch (IOException ex) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void generateVisitAndOrder(UserLogin userLogin, String clientId) {
        Client client = getMandatoryPO(userLogin, clientId, clientRepository);
        generateDataService.generateVisitAndOrder(client, DateTimeUtils.getFirstOfLastMonth(),
                DateTimeUtils.getTomorrow());
    }

}