package com.viettel.backend.engine.file;

import java.util.HashMap;

import com.mongodb.DBObject;
import com.viettel.backend.util.entity.SimpleDate;

public class DbFileMeta extends HashMap<String, Object> {

	private static final long serialVersionUID = -66958133819893089L;
	
	private static final String META_KEY_USED = "used";
	private static final String META_KEY_UPLOAD_DATE = "uploadDate";

	public DbFileMeta() {
        this(false, null);
    }

    public DbFileMeta(boolean used, SimpleDate uploadDate) {
        super();
        this.setUsed(used);
        this.setUploadDate(getUploadDate());
    }
    
    public DbFileMeta(DBObject o) {
        super();
        
        if (o != null) {
            this.put(META_KEY_USED, o.get(META_KEY_USED));
            this.put(META_KEY_UPLOAD_DATE, o.get(META_KEY_UPLOAD_DATE));
        }
    }
    
    public boolean isUsed() {
        Object value = this.get(META_KEY_USED);
        if (value != null && value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }

        return false;
    }

    public void setUsed(boolean used) {
        this.put(META_KEY_USED, used);
    }
    
    public SimpleDate getUploadDate() {
        Object value = this.get(META_KEY_UPLOAD_DATE);
        if (value != null && value instanceof SimpleDate) {
            return (SimpleDate) value;
        }
        return null;
    }

    public void setUploadDate(SimpleDate uploadDate) {
        this.put(META_KEY_UPLOAD_DATE, uploadDate);
    }
    
}
