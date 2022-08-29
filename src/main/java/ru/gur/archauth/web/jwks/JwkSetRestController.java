package ru.gur.archauth.web.jwks;

import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
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
}
