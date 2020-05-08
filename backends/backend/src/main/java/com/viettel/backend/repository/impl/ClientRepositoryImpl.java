package com.viettel.backend.repository.impl;

import org.springframework.stereotype.Repository;

import com.viettel.backend.domain.Client;
import com.viettel.backend.repository.ClientRepository;

@Repository
public class ClientRepositoryImpl extends CategoryRepositoryImpl<Client> implements ClientRepository {

}
