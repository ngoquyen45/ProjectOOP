package com.viettel.backend.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.viettel.backend.domain.PO;
import com.viettel.backend.domain.User;
import com.viettel.backend.repository.AuthenticateRepository;
import com.viettel.backend.util.CriteriaUtils;

@Repository
public class AuthenticateRepositoryImpl implements AuthenticateRepository {

    @Autowired
    private MongoTemplate dataTemplate;

    @Override
    public User findByUsername(String username) {
        Query query = new Query().addCriteria(CriteriaUtils.andOperator(
                CriteriaUtils.getSearchInsensitiveCriteria(User.COLUMNNAME_USERNAME_FULL, username),
                Criteria.where(PO.COLUMNNAME_ACTIVE).is(true).and(PO.COLUMNNAME_DRAFT).is(false)));

        return dataTemplate.findOne(query, User.class);
    }

}
