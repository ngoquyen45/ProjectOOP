package com.viettel.dmsplus.sdk.network;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Thanh on 3/24/2015.
 */
public class IsoShortDateSerializer extends DateSerializer {

    protected SimpleDateFormat format;

    @Override
    public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(getDateFormat().format(value));
    }

    protected synchronized DateFormat getDateFormat() {
        if (format == null) {
            format = new SimpleDateFormat("yyyy-MM-dd");
        }
        return format;
    }
}
