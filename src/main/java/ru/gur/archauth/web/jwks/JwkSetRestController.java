package ru.gur.archauth.web.jwks;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Map;

@RestController
public class JwkSetRestController {

    @Autowired
    private JWKSet jwkSet;
    @Autowired
    private KeyPair keyPair;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> keys() {
        return this.jwkSet.toJSONObject();
    }

//    @PostMapping(value = "/validate")
//    public Claims validate(@RequestBody String token) {
//
//        Claims claims;
//        try {
//            claims = Jwts.parser().setSigningKey(keyPair.getPublic()).parseClaimsJws(token).getBody();
//
//            System.out.println(claims.get("iss"));
//
//        } catch (Exception e) {
//
//            claims = null;
//        }
//        return claims;
//    }

    @PostMapping(value = "/validate")
    public boolean validate(@RequestHeader String token) throws IOException, ParseException, JOSEException {
        JWKSet set = JWKSet.load(new URL("http://localhost:8082/.well-known/jwks.json"));
        JWK key = set.getKeyByKeyId("gur-id");
        JWSVerifier verifier = new RSASSAVerifier((RSAKey) key);

        RSAPublicKey rsaPublicKey = ((RSAKey) key).toRSAPublicKey();
        Claims claims = Jwts.parser().setSigningKey(rsaPublicKey).parseClaimsJws(token).getBody();
        System.out.println("!! " + claims.get("profileId"));

        return SignedJWT.parse(token).verify(verifier);
    }
}
