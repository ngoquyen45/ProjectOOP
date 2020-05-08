package com.viettel.backend.repository.impl;

import org.springframework.stereotype.Repository;

import com.viettel.backend.domain.CustomerType;
import com.viettel.backend.repository.CustomerTypeRepository;

@Repository
public class CustomerTypeRepositoryImpl extends CategoryRepositoryImpl<CustomerType> implements CustomerTypeRepository {

}
