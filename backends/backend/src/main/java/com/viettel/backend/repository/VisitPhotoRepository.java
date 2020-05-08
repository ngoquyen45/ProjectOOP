package com.viettel.backend.repository;

import java.util.List;

import org.bson.types.ObjectId;

import com.viettel.backend.domain.VisitPhoto;
import com.viettel.backend.util.entity.SimpleDate.Period;

public interface VisitPhotoRepository {

    public List<VisitPhoto> getVisitPhoto(ObjectId clientId, ObjectId salesmanId, Period period);
    
}
