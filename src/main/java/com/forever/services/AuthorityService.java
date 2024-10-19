package com.forever.services;

import com.forever.entities.Authority;
import com.forever.repositories.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityRepository authorityRepository;

    public List<Authority> getUserAuthority() {
        List<Authority> authorities = new ArrayList<>();
        Authority authority = authorityRepository.findByRoleCode("USER");
        authorities.add(authority);

        return authorities;
    }

    public Authority createAuthority(String roleCode, String roleDescription) {
        Authority authority = Authority.builder()
                .roleCode(roleCode)
                .roleDescription(roleDescription)
                .build();

        return authorityRepository.save(authority);
    }
}
