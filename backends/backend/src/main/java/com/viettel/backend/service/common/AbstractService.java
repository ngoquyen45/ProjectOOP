package com.viettel.backend.service.common;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.viettel.backend.config.root.AppProperties;
import com.viettel.backend.domain.Area;
import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.CustomerType;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.PO;
import com.viettel.backend.domain.Product;
import com.viettel.backend.domain.ProductCategory;
import com.viettel.backend.domain.Route;
import com.viettel.backend.domain.UOM;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.dto.common.I_ProductPhotoFactory;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.exeption.BusinessException;
import com.viettel.backend.exeption.BusinessExceptionCode;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.BasicRepository;
import com.viettel.backend.repository.ConfigRepository;
import com.viettel.backend.repository.CustomerRepository;
import com.viettel.backend.repository.DistributorPriceListRepository;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.repository.I_POGetterRepository;
import com.viettel.backend.repository.ProductRepository;
import com.viettel.backend.repository.RouteRepository;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.util.LocationUtils;
import com.viettel.backend.util.entity.Location;
import com.viettel.backend.util.entity.SimpleDate;

public abstract class AbstractService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private DistributorPriceListRepository distributorPriceListRepository;
    
    @Autowired
    private AppProperties appProperties;

    protected final <D extends PO> void initPOWhenCreate(Class<D> clazz, UserLogin userLogin, D domain) {
        if (PO.isClientRootFixed(clazz)) {
            domain.setClientId(PO.CLIENT_ROOT_ID);
        } else {
            domain.setClientId(userLogin.getClientId());
        }

        domain.setActive(true);
    }

    /**
     * Get {@link User} from an {@link UserLogin}
     * 
     * @param userLogin
     * @return an {@link User} object, never <code>null<code>
     * @throws IllegalArgumentException
     *             if user not found
     */
    protected User getCurrentUser(UserLogin userLogin) {
        User user = userRepository.getById(userLogin.getClientId(), userLogin.getUserId());
        Assert.notNull(user, "User not found");
        return user;
    }

    protected ObjectId getObjectId(String id) {
        BusinessAssert.isTrue(ObjectId.isValid(id));

        return new ObjectId(id);
    }

    protected Set<ObjectId> getObjectIds(Collection<String> _ids) {
        if (_ids == null) {
            return null;
        }

        Set<ObjectId> ids = new HashSet<ObjectId>();
        for (String _id : _ids) {
            ids.add(getObjectId(_id));
        }

        return ids;
    }

    protected Set<ObjectId> getIdSet(Collection<? extends PO> pos) {
        if (pos == null) {
            return null;
        }
        if (pos.isEmpty()) {
            return Collections.emptySet();
        }

        Set<ObjectId> idList = new HashSet<ObjectId>(pos.size());
        for (PO po : pos) {
            idList.add(po.getId());
        }
        return idList;
    }

    protected final <D extends PO> HashMap<ObjectId, D> getPOMap(Collection<D> domains) {
        HashMap<ObjectId, D> map = new HashMap<ObjectId, D>();
        if (domains != null && !domains.isEmpty()) {
            for (D domain : domains) {
                map.put(domain.getId(), domain);
            }
        }

        return map;
    }

    protected void checkMandatoryParams(Object... params) {
        if (params != null) {
            for (Object param : params) {
                BusinessAssert.notNull(param, "param is null");

                if (param instanceof String) {
                    BusinessAssert.isTrue(StringUtils.hasText((String) param), "param text is empty");
                } else if (param instanceof Collection<?>) {
                    Collection<?> items = (Collection<?>) param;

                    BusinessAssert.notEmpty(items, "param collection is empty");

                    for (Object item : items) {
                        checkMandatoryParams(item);
                    }
                } else if (param instanceof Location) {
                    Location location = (Location) param;
                    BusinessAssert.isTrue(LocationUtils.checkLocationValid(location), "param location invalid");
                }
            }
        }
    }

    protected SimpleDate getMandatoryIsoDate(String isoDate) {
        BusinessAssert.notNull(isoDate);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(isoDate);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            SimpleDate simpleDate = new SimpleDate();

            simpleDate.setYear(cal.get(Calendar.YEAR));
            simpleDate.setMonth(cal.get(Calendar.MONTH));
            simpleDate.setDate(cal.get(Calendar.DAY_OF_MONTH));

            return simpleDate;
        } catch (ParseException e) {
            throw new BusinessException(BusinessExceptionCode.INVALID_PARAM, "iso date wrong format");
        }
    }

    protected SimpleDate getMandatoryIsoTime(String isoTime) {
        BusinessAssert.notNull(isoTime);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date date = sdf.parse(isoTime);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            SimpleDate simpleDate = new SimpleDate();

            simpleDate.setYear(cal.get(Calendar.YEAR));
            simpleDate.setMonth(cal.get(Calendar.MONTH));
            simpleDate.setDate(cal.get(Calendar.DAY_OF_MONTH));
            simpleDate.setHour(cal.get(Calendar.HOUR_OF_DAY));
            simpleDate.setMinute(cal.get(Calendar.MINUTE));
            simpleDate.setSecond(cal.get(Calendar.SECOND));

            return simpleDate;
        } catch (ParseException e) {
            throw new BusinessException(BusinessExceptionCode.INVALID_PARAM, "iso time wrong format");
        }
    }

    protected <D extends PO> D getMandatoryPO(UserLogin userLogin, String _id, I_POGetterRepository<D> repository) {
        ObjectId id = getObjectId(_id);

        D domain = repository.getById(userLogin.getClientId(), id);
        String message = String.format("Cannot find record [Type=%s, Id=%s]",
                repository.getDomainClass().getSimpleName(), id);

        String code = null;
        if (repository.getDomainClass().equals(User.class)) {
            code = BusinessExceptionCode.USER_NOT_FOUND;
        } else if (repository.getDomainClass().equals(UOM.class)) {
            code = BusinessExceptionCode.UOM_NOT_FOUND;
        } else if (repository.getDomainClass().equals(ProductCategory.class)) {
            code = BusinessExceptionCode.PRODUCT_CATEGORY_NOT_FOUND;
        } else if (repository.getDomainClass().equals(Product.class)) {
            code = BusinessExceptionCode.PRODUCT_NOT_FOUND;
        } else if (repository.getDomainClass().equals(Area.class)) {
            code = BusinessExceptionCode.AREA_NOT_FOUND;
        } else if (repository.getDomainClass().equals(CustomerType.class)) {
            code = BusinessExceptionCode.CUSTOMER_TYPE_NOT_FOUND;
        } else if (repository.getDomainClass().equals(Customer.class)) {
            code = BusinessExceptionCode.CUSTOMER_NOT_FOUND;
        } else if (repository.getDomainClass().equals(Route.class)) {
            code = BusinessExceptionCode.ROUTE_NOT_FOUND;
        } else {
            code = BusinessExceptionCode.INVALID_PARAM;
        }

        BusinessAssert.notNull(domain, code, message);

        return domain;
    }

    protected <D extends PO> D getMandatoryPO(UserLogin userLogin, String _id, Boolean draft, Boolean active,
            BasicRepository<D> repository) {
        ObjectId id = getObjectId(_id);

        D domain = repository.getById(userLogin.getClientId(), draft, active, id);
        String message = String.format("Cannot find record [Type=%s, Id=%s]",
                repository.getDomainClass().getSimpleName(), id);

        String code = null;
        if (repository.getDomainClass().equals(User.class)) {
            code = BusinessExceptionCode.USER_NOT_FOUND;
        } else if (repository.getDomainClass().equals(UOM.class)) {
            code = BusinessExceptionCode.UOM_NOT_FOUND;
        } else if (repository.getDomainClass().equals(ProductCategory.class)) {
            code = BusinessExceptionCode.PRODUCT_CATEGORY_NOT_FOUND;
        } else if (repository.getDomainClass().equals(Product.class)) {
            code = BusinessExceptionCode.PRODUCT_NOT_FOUND;
        } else if (repository.getDomainClass().equals(Area.class)) {
            code = BusinessExceptionCode.AREA_NOT_FOUND;
        } else if (repository.getDomainClass().equals(CustomerType.class)) {
            code = BusinessExceptionCode.CUSTOMER_TYPE_NOT_FOUND;
        } else if (repository.getDomainClass().equals(Customer.class)) {
            code = BusinessExceptionCode.CUSTOMER_NOT_FOUND;
        } else if (repository.getDomainClass().equals(Route.class)) {
            code = BusinessExceptionCode.ROUTE_NOT_FOUND;
        } else {
            code = BusinessExceptionCode.INVALID_PARAM;
        }

        BusinessAssert.notNull(domain, code, message);

        return domain;
    }

    protected Config getConfig(UserLogin userLogin) {
        Config config = configRepository.getConfig(userLogin.getClientId());
        if (config == null) {
            throw new UnsupportedOperationException("system config not found");
        }
        return config;
    }

    protected I_ProductPhotoFactory getProductPhotoFactory(UserLogin userLogin) {
        final Map<ObjectId, Product> productMap = productRepository.getProductMap(userLogin.getClientId(), null);
        final Config config = getConfig(userLogin);

        return new I_ProductPhotoFactory() {

            @Override
            public String getPhoto(ObjectId productId) {
                if (productId != null) {
                    Product product = productMap.get(productId);
                    if (product != null && product.getPhoto() != null) {
                        return product.getPhoto();
                    }
                }

                return config.getProductPhoto();
            }

        };
    }

    // ACCESSIBLE CHECK
    protected boolean isManager(UserLogin userLogin) {
        if (userLogin.isRole(Role.ADMIN) || userLogin.isRole(Role.SUPERVISOR) || userLogin.isRole(Role.DISTRIBUTOR)) {
            return true;
        }

        return false;
    }

    protected List<Distributor> getAccessibleDistributors(UserLogin userLogin) {
        if (userLogin.isRole(Role.ADMIN)) {
            return distributorRepository.getAll(userLogin.getClientId(), null);
        } else if (userLogin.isRole(Role.OBSERVER)) {
            User user = getCurrentUser(userLogin);
            if (user.getDistributorIds() == null || user.getDistributorIds().isEmpty()) {
                return Collections.emptyList();
            } else {
                return distributorRepository.getListByIds(userLogin.getClientId(), user.getDistributorIds());
            }
        } else if (userLogin.isRole(Role.SUPERVISOR)) {
            return distributorRepository.getDistributorsBySupervisors(userLogin.getClientId(),
                    Collections.singletonList(userLogin.getUserId()));
        } else if (userLogin.isRole(Role.DISTRIBUTOR) || userLogin.isRole(Role.SALESMAN)) {
            return Collections.singletonList(getDefaultDistributor(userLogin));
        }
        return Collections.emptyList();
    }

    protected boolean checkAccessible(UserLogin userLogin, ObjectId distributorId) {
        Assert.notNull(distributorId);

        List<Distributor> distributors = getAccessibleDistributors(userLogin);
        Set<ObjectId> distributorIds = getIdSet(distributors);

        return distributorIds.contains(distributorId);
    }

    protected boolean checkAccessible(UserLogin userLogin, ObjectId distributorId, Customer customer) {
        List<Distributor> distributors = getAccessibleDistributors(userLogin);
        Set<ObjectId> distributorIds = getIdSet(distributors);

        if (distributorId != null) {
            if (!distributorIds.contains(distributorId)) {
                return false;
            }
        }

        if (customer != null) {
            if (customer.getDistributor() == null || !distributorIds.contains(customer.getDistributor().getId())) {
                return false;
            }

            if (userLogin.isRole(Role.SALESMAN)) {
                Set<ObjectId> salesmanIds = Collections.singleton(userLogin.getUserId());

                List<Route> routes = routeRepository.getRoutesBySalesmen(userLogin.getClientId(), salesmanIds);
                Set<ObjectId> routeIds = getIdSet(routes);

                if (!customerRepository.checkCustomerRoute(userLogin.getClientId(),
                        Collections.singleton(customer.getId()), routeIds)) {
                    return false;
                }
            }
        }

        return true;
    }

    protected Distributor getDefaultDistributor(UserLogin userLogin) {
        if (userLogin.isRole(Role.SALESMAN) || userLogin.isRole(Role.DISTRIBUTOR)) {
            User user = getCurrentUser(userLogin);
            Assert.notNull(user.getDistributor(),
                    String.format("User with ID: %s doesn't have Distributor", user.getId()));

            Distributor distributor = distributorRepository.getById(userLogin.getClientId(),
                    user.getDistributor().getId());

            Assert.notNull(distributor, "Cannot find Distributor with ID: " + user.getDistributor().getId());

            return distributor;
        }
        return null;
    }

    protected String getLang(String lang) {
        if (lang == null && !appProperties.getLanguages().contains(lang)) {
            return appProperties.getLanguages().get(0);
        } else {
            return lang;
        }
    }
    
    protected String translate(String lang, String code) {
        try {
            return messageSource.getMessage(code, null, new Locale(lang));
        } catch (Exception e) {
            System.out.println(code);
            return code;
        }
    }
    
    protected Map<ObjectId, BigDecimal> getDistributorPriceList(UserLogin userLogin) {
        Distributor distributor = getDefaultDistributor(userLogin);
        if (distributor != null) {
            Map<ObjectId, BigDecimal> priceList = distributorPriceListRepository.getPriceList(userLogin.getClientId(),
                    distributor.getId());
            return priceList;
        } else {
            return null;
        }
    }

}
