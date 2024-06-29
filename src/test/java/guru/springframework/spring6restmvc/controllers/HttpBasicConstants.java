package guru.springframework.spring6restmvc.controllers;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.time.Instant;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

public class HttpBasicConstants {
    
    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor JWT_REQUEST_POST_PROCESSOR = jwt().jwt(jwt -> {
        jwt.claims(claims -> {
                    claims.put("scope", "message.read");
                    claims.put("scope", "message.write");
                })
                .subject("messaging-client")
                .notBefore(Instant.now().minusSeconds(5L));

    });
}
