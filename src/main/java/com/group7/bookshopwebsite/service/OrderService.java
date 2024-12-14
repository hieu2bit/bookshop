package com.group7.bookshopwebsite.service;

import com.group7.bookshopwebsite.dto.CartDTO;
import com.group7.bookshopwebsite.dto.OrderPerson;
import com.group7.bookshopwebsite.entity.Order;
import com.group7.bookshopwebsite.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderService {
    List<Order> getAllOrders();
    List<Order> getAllOrders(Sort sort);

    List<Order> getAllOrdersByUser(User user);
    Order getOrderById(Long orderId);

    Order createOrder(CartDTO cart, User user, OrderPerson orderPerson);

    Order updateOrder(Order order);

    void deleteOrder(Long orderId);

    void cancelOrder(Order order);

    Page<Order> getOrdersByStatus(String status, Pageable pageable);

    Page<Order> getAllOrdersOnPage(Pageable pageable);

    void setProcessingOrder(Order order);

    void setDeliveringOrder(Order order);

    void setDeliveredOrder(Order order);

    void setReceivedToOrder(Order order);

    public List<Map<String, Object>> getAllOrderStatistics();
    public List<Map<String, Object>> getDeliveredOrderRevenues();

    List<Order> getTop10orders();

    BigDecimal getTotalRevenue();

    Long countOrder();
}
