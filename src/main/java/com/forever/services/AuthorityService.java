package com.forever.services;

import com.forever.entities.Authority;

import java.util.List;

public interface AuthorityService {

    List<Authority> getUserAuthority();
    Authority createAuthority(String roleCode, String roleDescription);
}
