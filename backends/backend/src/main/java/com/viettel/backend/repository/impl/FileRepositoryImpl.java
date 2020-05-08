package com.viettel.backend.repository.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.viettel.backend.engine.file.DbFile;
import com.viettel.backend.engine.file.DbFileMeta;
import com.viettel.backend.repository.FileRepository;

@Repository
public class FileRepositoryImpl implements FileRepository {

    private String collectionName = "fs.files";

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public DbFile getImageById(ObjectId fileId) {
        return getFileById(null, fileId);
    }
    
    @Override
    public String storeImage(InputStream inputStream, String fileName, String contentType, DbFileMeta metaData) {
        return store(null, inputStream, fileName, contentType, metaData);
    }
    
    @Override
    public String store(ObjectId clientId, InputStream inputStream, 
    		String fileName, String contentType, DbFileMeta metaData) {
    	GridFSFile file = this.gridFsTemplate.store(inputStream, fileName, 
    			contentType, new BasicDBObject(metaData));
    	if (file == null) {
    		return null;
    	}
    	
    	return file.getId().toString();
    }

    @Override
    public void delete(ObjectId clientId, ObjectId fileId) {
        this.gridFsTemplate.delete(new Query(Criteria.where("_id").is(fileId)));
    }
    
    @Override
    public void delete(ObjectId clientId, Collection<ObjectId> fileIds) {
        this.gridFsTemplate.delete(new Query(Criteria.where("_id").in(fileIds)));
    }

    @Override
    public void markAsUsed(ObjectId clientId, ObjectId fileId) {
        Query query = new Query(Criteria.where("_id").is(fileId));
        Update update = new Update().addToSet("metadata.active", true);

        this.mongoOperations.updateMulti(query, update, collectionName);
    }
    
    @Override
    public void markAsUsed(ObjectId clientId, Collection<ObjectId> fileIds) {
        Query query = new Query(Criteria.where("_id").in(fileIds));
        Update update = new Update().addToSet("metadata.active", true);

        this.mongoOperations.updateMulti(query, update, collectionName);
    }
    
    @Override
    public boolean existsAll(ObjectId clientId, Collection<ObjectId> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return false;
        }
        
        Query query = new Query(Criteria.where("_id").in(fileIds));
        long count = this.mongoOperations.count(query, collectionName);
        return count == fileIds.size();
    }
    
    @Override
    public boolean exists(ObjectId clientId, ObjectId fileId) {
        Query query = new Query(Criteria.where("_id").is(fileId));
        return this.mongoOperations.exists(query, collectionName);
    }

	@Override
    public DbFile getFileById(ObjectId clientId, ObjectId fileId) {
		GridFSDBFile dbFile = this.gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId)));
		return createDbFile(clientId, dbFile);
	}

	@Override
	public Collection<DbFile> getFileByIds(ObjectId clientId, Collection<ObjectId> fileIds) {
		Collection<GridFSDBFile> dbFiles = this.gridFsTemplate.find(new Query(Criteria.where("_id").in(fileIds)));
		if (dbFiles == null) {
			return Collections.<DbFile>emptyList();
		}
		
		List<DbFile> files = new ArrayList<DbFile>();
		for (GridFSDBFile dbFile : dbFiles) {
			DbFile file = createDbFile(clientId, dbFile);
			if (file != null) {
				files.add(file);
			}
		}
		
		return files;
	}
	
	private DbFile createDbFile(Object clientId, GridFSDBFile dbFile) {
		if (dbFile == null) {
			return null;
		}
		
		DbFile file = new DbFile();
		file.setId(dbFile.getId().toString());
		file.setFileName(dbFile.getFilename());
        
        file.setInputStream(dbFile.getInputStream());
        file.setContentType(dbFile.getContentType());

        if (dbFile.getMetaData() != null) {
            file.setMetaData(new DbFileMeta(dbFile.getMetaData()));
        }
        
		return file;
	}
}
