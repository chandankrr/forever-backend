package com.forever.services;

import com.forever.dtos.AddressRequest;
import com.forever.entities.Address;

import java.security.Principal;


public interface AddressService {

    Address createAddress(AddressRequest addressRequest, Principal principal);
}
