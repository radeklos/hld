package com.caribou;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


public class Json {

    public static String dumps(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

}
