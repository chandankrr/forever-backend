package com.forever.services.impl;

import com.forever.dtos.OrderRequest;
import com.forever.dtos.OrderResponse;
import com.forever.entities.*;
import com.forever.entities.enums.OrderStatus;
import com.forever.entities.enums.PaymentStatus;
import com.forever.repositories.OrderRepository;
import com.forever.services.OrderService;
import com.forever.services.PaymentIntentService;
import com.forever.services.ProductService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserDetailsService userDetailsService;
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final PaymentIntentService paymentIntentService;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest, Principal principal) throws BadRequestException, StripeException {
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Address address = user.getAddresses().stream()
                .filter(address1 -> orderRequest.getAddressId().equals(address1.getId()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Address not found"));

        Order order = Order.builder()
                .user(user)
                .address(address)
                .totalAmount(orderRequest.getTotalAmount())
                .orderDate(orderRequest.getOrderDate())
                .discount(orderRequest.getDiscount())
                .expectedDeliveryDate(orderRequest.getExpectedDeliveryDate())
                .paymentMethod(orderRequest.getPaymentMethod())
                .orderStatus(OrderStatus.PENDING)
                .build();

        List<OrderItem> orderItems = orderRequest.getOrderItemRequests().stream()
                .map(orderItemRequest -> {
                    try {
                        Product product = productService.fetchProductById(orderItemRequest.getProductId());
                        return OrderItem.builder()
                                .product(product)
                                .productVariantID(orderItemRequest.getProductVariantId())
                                .quantity(orderItemRequest.getQuantity())
                                .order(order)
                                .itemPrice(product.getPrice())
                                .build();
                    } catch (BadRequestException e) {
                        throw new IllegalArgumentException("Error fetching product: " + e.getMessage(), e);
                    }
                }).toList();

        order.setOrderItems(orderItems);

        Payment payment = Payment.builder()
                .order(order)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentDate(new Date())
                .amount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .build();

        order.setPayment(payment);
        Order savedOrder = orderRepository.save(order);

        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(savedOrder.getId())
                .paymentMethod(orderRequest.getPaymentMethod())
                .build();
        if ("CARD".equals(orderRequest.getPaymentMethod())) {
            orderResponse.setCredentials(paymentIntentService.createPaymentIntent(savedOrder));
        }

        return orderResponse;
    }

    @Override
    public Map<String, String> updateStatus(String paymentIntentId, String status) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

            if (paymentIntent == null) {
                throw new IllegalArgumentException("PaymentIntent not found");
            }

            String orderId = paymentIntent.getMetadata().get("orderId");
            if (orderId == null) {
                throw new IllegalArgumentException("PaymentIntent missing order_id in metadata");
            }

            Order order = orderRepository.findById(UUID.fromString(orderId))
                    .orElseThrow(() -> new BadRequestException("Order not found"));

            Payment payment = order.getPayment();

            switch (status) {
                case "succeeded":
                    payment.setPaymentStatus(PaymentStatus.COMPLETED);
                    break;
                case "pending":
                    payment.setPaymentStatus(PaymentStatus.PENDING);
                    break;
                case "failed":
                    payment.setPaymentStatus(PaymentStatus.FAILED);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid payment status: " + status);
            }

            order.setPayment(payment);
            Order savedOrder = orderRepository.save(order);

            Map<String, String> response = new HashMap<>();
            response.put("orderId", savedOrder.getId().toString());
            return response;
        } catch (StripeException e) {
            throw new RuntimeException("Error retrieving PaymentIntent from Stripe", e);
        } catch (IllegalArgumentException | BadRequestException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error updating payment status", e);
        }
    }
}
