package com.caribou;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;

@IntegrationTest({"server.port=0"})
public class IntegrationTests {

    @Value("${local.server.port}")
    private int port = 0;

    protected String path(String context) {
        return String.format("http://localhost:%s%s", port, context);
    }

}
