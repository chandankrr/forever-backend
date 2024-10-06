package com.forever.services.impl;

import com.forever.entities.User;
import com.forever.repositories.UserRepository;
import com.forever.services.AuthorityService;
import com.forever.services.OAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2ServiceImpl implements OAuth2Service {

    private final UserRepository userRepository;
    private final AuthorityService authorityService;

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User createUser(OAuth2User oAuth2User, String provider) {
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");
        String email = oAuth2User.getAttribute("email");

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .enabled(true)
                .provider(provider)
                .authorities(authorityService.getUserAuthority())
                .build();

        return userRepository.save(user);
    }
}
