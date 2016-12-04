package com.caribou.auth.jwt.extractor;

public interface TokenExtractor {
    String extract(String payload);
}
