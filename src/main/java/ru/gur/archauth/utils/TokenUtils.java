package ru.gur.archauth.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@UtilityClass
public class TokenUtils {

    public static UUID getProfileIdFromPayload(final String tokenPayload) {
        Assert.hasText(tokenPayload, "tokenPayload must not be blank");

        log.info("auth tokenPayload: " + tokenPayload);

        final Base64.Decoder decoder = Base64.getUrlDecoder();
        final String payload = new String(decoder.decode(tokenPayload));

        log.info("payload: " + payload);

        try {
            Map<String, Object> mapping = new ObjectMapper().readValue(payload, HashMap.class);
            log.info("mapping: " + mapping);

            return UUID.fromString(mapping.get("profileId").toString());
        } catch (Exception e) {
            //do nothing
        }
        return null;
    }

    public static UUID getProfileIdFromOriginalToken(final String token) {
        Assert.hasText(token, "x-jwt-token must not be blank");

        log.info("auth token: " + token);

        final String[] chunks = token.split("\\.");
        final Base64.Decoder decoder = Base64.getUrlDecoder();
        final String payload = new String(decoder.decode(chunks[1]));

        try {
            Map<String, Object> mapping = new ObjectMapper().readValue(payload, HashMap.class);
            return UUID.fromString(mapping.get("profileId").toString());
        } catch (Exception e) {
            //do nothing
        }
        return null;
    }
}