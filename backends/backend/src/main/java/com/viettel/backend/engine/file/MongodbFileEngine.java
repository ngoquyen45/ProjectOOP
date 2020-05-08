package com.viettel.backend.engine.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.FileRepository;
import com.viettel.backend.util.ImageUtils;

public class MongodbFileEngine implements FileEngine {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private FileRepository fileRepository;
    
    public MongodbFileEngine(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }
    
    @Override
    public DbFile getImage(String fileId) {
        if (ObjectId.isValid(fileId)) {
            return fileRepository.getImageById(new ObjectId(fileId));
        } else {
            return null;
        }
    }

    @Override
    public String storeImage(InputStream inputStream, String originalFileName, String contentType,
            DbFileMeta meta, String sizeType) {
        try {
            // TODO Check Extension
            byte[] bytes = IOUtils.toByteArray(inputStream);

            bytes = ImageUtils.resizeImage(bytes, sizeType);

            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

            return fileRepository.storeImage(bis, originalFileName, contentType, meta);
        } catch (IOException e) {
            logger.error("InputStream is not correct", e);
            throw new UnsupportedOperationException("error when write photo");
        }
    }
    
    @Override
    public String store(Object context, InputStream inputStream, String originalFileName, String contentType,
            DbFileMeta meta) {
        if (context == null || !(context instanceof UserLogin)) {
            throw new UnsupportedOperationException("context invalid");
        }

        ObjectId clientId = ((UserLogin) context).getClientId();
        
        return fileRepository.store(clientId, inputStream, originalFileName, contentType, meta);
    }

    @Override
    public DbFile get(Object context, String fileId) {
        if (context == null || !(context instanceof UserLogin)) {
            throw new UnsupportedOperationException("context invalid");
        }

        ObjectId clientId = ((UserLogin) context).getClientId();
        
        if (ObjectId.isValid(fileId)) {
            return fileRepository.getFileById(clientId, new ObjectId(fileId));
        } else {
            return null;
        }
    }

    @Override
    public void delete(Object context, String fileId) {
        if (context == null || !(context instanceof UserLogin)) {
            throw new UnsupportedOperationException("context invalid");
        }

        ObjectId clientId = ((UserLogin) context).getClientId();
        
        if (ObjectId.isValid(fileId)) {
            fileRepository.delete(clientId, new ObjectId(fileId));
        }
    }

    @Override
    public void delete(Object context, Collection<String> _fileIds) {
        if (context == null || !(context instanceof UserLogin)) {
            throw new UnsupportedOperationException("context invalid");
        }

        ObjectId clientId = ((UserLogin) context).getClientId();
        
        if (_fileIds == null || _fileIds.isEmpty()) {
            return;
        }
        
        List<ObjectId> fileIds = new LinkedList<ObjectId>();
        for (String _fileId : _fileIds) {
            if (ObjectId.isValid(_fileId)) {
                fileIds.add(new ObjectId(_fileId));
            }
        }
        
        if (fileIds.isEmpty()) {
            return;
        }
        
        fileRepository.delete(clientId, fileIds);
    }

    @Override
    public void markAsUsed(Object context, String fileId) {
        if (context == null || !(context instanceof UserLogin)) {
            throw new UnsupportedOperationException("context invalid");
        }

        ObjectId clientId = ((UserLogin) context).getClientId();
        
        if (ObjectId.isValid(fileId)) {
            fileRepository.markAsUsed(clientId, new ObjectId(fileId));
        }
    }

    @Override
    public void markAsUsed(Object context, Collection<String> _fileIds) {
        if (context == null || !(context instanceof UserLogin)) {
            throw new UnsupportedOperationException("context invalid");
        }

        ObjectId clientId = ((UserLogin) context).getClientId();
        
        if (_fileIds == null || _fileIds.isEmpty()) {
            return;
        }
        
        List<ObjectId> fileIds = new LinkedList<ObjectId>();
        for (String _fileId : _fileIds) {
            if (ObjectId.isValid(_fileId)) {
                fileIds.add(new ObjectId(_fileId));
            }
        }
        
        if (fileIds.isEmpty()) {
            return;
        }
        
        fileRepository.markAsUsed(clientId, fileIds);
    }

    @Override
    public boolean exists(Object context, String fileId) {
        if (context == null || !(context instanceof UserLogin)) {
            throw new UnsupportedOperationException("context invalid");
        }

        ObjectId clientId = ((UserLogin) context).getClientId();

        if (ObjectId.isValid(fileId)) {
            return fileRepository.exists(clientId, new ObjectId(fileId));
        } else {
            return false;
        }
    }

    @Override
    public boolean exists(Object context, Collection<String> _fileIds) {
        if (context == null || !(context instanceof UserLogin)) {
            throw new UnsupportedOperationException("context invalid");
        }

        ObjectId clientId = ((UserLogin) context).getClientId();
        
        if (_fileIds == null || _fileIds.isEmpty()) {
            return true;
        }
        
        List<ObjectId> fileIds = new LinkedList<ObjectId>();
        for (String _fileId : _fileIds) {
            if (ObjectId.isValid(_fileId)) {
                fileIds.add(new ObjectId(_fileId));
            } else {
                return false;
            }
        }
        
        return fileRepository.existsAll(clientId, fileIds);
    }

}
