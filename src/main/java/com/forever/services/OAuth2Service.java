package com.forever.services;

import com.forever.entities.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2Service {
    User getUser(String email);
    User createUser(OAuth2User oAuth2User, String provider);
}
