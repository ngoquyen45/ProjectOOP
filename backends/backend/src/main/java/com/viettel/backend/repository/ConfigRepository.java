package com.viettel.backend.repository;

import org.bson.types.ObjectId;

import com.viettel.backend.domain.Config;

public interface ConfigRepository {

    public Config getConfig(ObjectId clientId);

    public Config save(ObjectId clientId, Config config);

}
