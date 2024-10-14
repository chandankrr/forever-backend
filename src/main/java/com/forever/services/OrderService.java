package com.forever.services;

import com.forever.dtos.OrderRequest;
import com.forever.dtos.OrderResponse;
import com.stripe.exception.StripeException;
import org.apache.coyote.BadRequestException;

import java.security.Principal;
import java.util.Map;

public interface OrderService {

    OrderResponse createOrder(OrderRequest orderRequest, Principal principal) throws BadRequestException, StripeException;
    Map<String, String> updateStatus(String paymentIntent, String status);
}
