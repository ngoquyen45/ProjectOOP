package com.viettel.backend.repository.impl;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.viettel.backend.domain.Counter;
import com.viettel.backend.repository.CodeGeneratorRepository;
import com.viettel.backend.util.entity.SimpleDate;

@Repository
public class CodeGeneratorRepositoryImpl implements CodeGeneratorRepository {

    private static final long serialVersionUID = 2110219098776321841L;

    @Autowired
    protected MongoTemplate dataTemplate;

    private int getNextSequence(String keyValue, int startValue, int step) {
        Counter counter = dataTemplate.findAndModify(query(where("_id").is(keyValue)), new Update().inc("seq", step),
                options().returnNew(true), Counter.class);

        if (counter == null) {
            counter = new Counter();
            counter.setId(keyValue);
            counter.setSeq(startValue);
            try {
                dataTemplate.insert(counter);
            } catch (DuplicateKeyException dupEx) {
                return getNextSequence(keyValue, startValue, step);
            }
        }

        return counter.getSeq();
    }

    @Override
    public String getDistributorCode(String clientId) {
        String keyValue = "distributor_" + clientId;
        return "DIS" + String.format("%04d", getNextSequence(keyValue, 1, 1));
    }

    @Override
    public String getOrderCode(String clientId, SimpleDate createdDate) {
        Assert.notNull(createdDate);
        String year = String.valueOf(createdDate.getYear() % 100);
        
        String keyValue = "order_" + year + "_" + clientId;
        return "PO" + year + String.format("%08d", getNextSequence(keyValue, 1, 1));
    }

    @Override
    public String getCustomerCode(String clientId) {
        String keyValue = "customer_" + clientId;
        return "C" + String.format("%07d", getNextSequence(keyValue, 1, 1));
    }
    
    @Override
    public List<String> getBatchCustomerCode(String clientId, int size) {
        String keyValue = "customer_" + clientId;
        int sequence = getNextSequence(keyValue, 1, size);
        
        List<String> codes = new ArrayList<String>(size);
        
        sequence = sequence - size + 1;
        
        for (int i = 0; i < size; i++) {
            codes.add("CUS" + String.format("%06d", sequence));
            sequence++;
        }
        return codes;
    }

}
