package com.forever.controllers;

import com.forever.dtos.OrderRequest;
import com.forever.dtos.OrderResponse;
import com.forever.services.OrderService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest, Principal principal) throws BadRequestException, StripeException {
        OrderResponse orderResponse = orderService.createOrder(orderRequest, principal);
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }

    @PostMapping("/update-payment")
    public ResponseEntity< Map<String, String>> updatePaymentStatus(@RequestBody Map<String, String> request) {
        Map<String, String> response = orderService.updateStatus(request.get("payment_intent"), request.get("status"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
