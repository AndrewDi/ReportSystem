package com.andrew.Common;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

public class JsonDateModule extends SimpleModule {
    public JsonDateModule(){
        final SimpleModule simpleModule = this.addDeserializer(Date.class, new JsonDeserializer<Date>() {

            @Override
            public Date deserialize(JsonParser p, DeserializationContext ctxt)
                    throws IOException {
                try {
                    System.out.println("from my custom deserializer!!!!!!");
                    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(p.getValueAsString());
                } catch (ParseException e) {
                    System.err.println("aw, it fails: " + e.getMessage());
                    throw new IOException(e.getMessage());
                }
            }
        });
    }
}
