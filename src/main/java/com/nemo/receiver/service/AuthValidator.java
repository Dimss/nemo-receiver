package com.nemo.receiver.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

@Service
public class AuthValidator {

    private DecodedJWT userIdentity;

    private String token;

    public DecodedJWT getUserIdentity() {
        return userIdentity;
    }

    public void setUserIdentity(DecodedJWT userIdentity) {
        this.userIdentity = userIdentity;
    }

    public Boolean validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm).withIssuer("identity").build();
            this.setUserIdentity(verifier.verify(token));
            return true;
        } catch (JWTVerificationException exception) {
            throw exception;
        }
    }
}
