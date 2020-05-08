package com.viettel.dmsplus.sdk.network;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Thanh on 3/24/2015.
 */
public class IsoShortDateDeserializer extends DateDeserializers.DateDeserializer {

    protected SimpleDateFormat format;

    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String date = jp.getText();
        try {
            return getDateFormat().parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Failed to parse Date value '" + date
                    +"' (format: \"yyyy-MM-dd\"): " + e.getMessage());
        }
    }

    protected synchronized DateFormat getDateFormat() {
        if (format == null) {
            format = new SimpleDateFormat("yyyy-MM-dd");
        }
        return format;
    }
}
