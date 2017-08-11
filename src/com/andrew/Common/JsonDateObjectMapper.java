package com.andrew.Common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

public class JsonDateObjectMapper extends ObjectMapper {

    public JsonDateObjectMapper(){
        JsonDateModule jsonDateModule = new JsonDateModule();
        this.registerModule(jsonDateModule);
        this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jackson2HttpMessageConverter.setObjectMapper(this);
    }
}
