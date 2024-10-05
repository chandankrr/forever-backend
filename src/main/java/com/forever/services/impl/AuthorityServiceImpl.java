package com.forever.services.impl;

import com.forever.entities.Authority;
import com.forever.repositories.AuthorityRepository;
import com.forever.services.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    @Override
    public List<Authority> getUserAuthority() {
        List<Authority> authorities = new ArrayList<>();
        Authority authority = authorityRepository.findByRoleCode("USER");
        authorities.add(authority);

        return authorities;
    }

    @Override
    public Authority createAuthority(String roleCode, String roleDescription) {
        Authority authority = Authority.builder()
                .roleCode(roleCode)
                .roleDescription(roleDescription)
                .build();

        return authorityRepository.save(authority);
    }
}
