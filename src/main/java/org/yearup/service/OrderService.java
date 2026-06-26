package org.yearup.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yearup.models.*;
import org.yearup.repository.OrderLineItemRepository;
import org.yearup.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderLineItemRepository orderLineItemRepository;
    private final ShoppingCartService shoppingCartService;
    private final UserService userService;
    private final ProfileService profileService;

    public OrderService(OrderRepository orderRepository,
                        OrderLineItemRepository orderLineItemRepository,
                        ShoppingCartService shoppingCartService,
                        UserService userService,
                        ProfileService profileService) {
        this.orderRepository = orderRepository;
        this.orderLineItemRepository = orderLineItemRepository;
        this.shoppingCartService = shoppingCartService;
        this.userService = userService;
        this.profileService = profileService;
    }

    @Transactional
    public Order checkout(int userId) {

        Profile profile = profileService.getByUserId(userId);


        Order order = new Order();
        order.setUserId(userId);
        order.setDate(LocalDateTime.now());
        order.setAddress(profile.getAddress());
        order.setCity(profile.getCity());
        order.setState(profile.getState());
        order.setZip(profile.getZip());
        order.setShippingAmount(BigDecimal.ZERO);


        Order savedOrder = orderRepository.save(order);


        ShoppingCart cart = shoppingCartService.getByUserId(userId);


        for (ShoppingCartItem item : cart.getItems().values()) {
            OrderLineItem lineItem = new OrderLineItem();
            lineItem.setOrderId(savedOrder.getOrderId());
            lineItem.setProductId(item.getProduct().getProductId());
            lineItem.setSalesPrice(BigDecimal.valueOf(item.getProduct().getPrice()));
            lineItem.setQuantity(item.getQuantity());
            lineItem.setDiscount(BigDecimal.ZERO);
            orderLineItemRepository.save(lineItem);
        }


        shoppingCartService.clearCart(userId);

        return savedOrder;
    }
}
