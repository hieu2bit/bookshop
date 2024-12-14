package com.group7.bookshopwebsite.service.impl;

import com.group7.bookshopwebsite.constant.OrderStatus;
import com.group7.bookshopwebsite.constant.PaymentMethod;
import com.group7.bookshopwebsite.dto.CartDTO;
import com.group7.bookshopwebsite.dto.CartItemDTO;
import com.group7.bookshopwebsite.dto.OrderPerson;
import com.group7.bookshopwebsite.entity.Book;
import com.group7.bookshopwebsite.entity.Order;
import com.group7.bookshopwebsite.entity.OrderDetail;
import com.group7.bookshopwebsite.entity.User;
import com.group7.bookshopwebsite.repository.BookRepository;
import com.group7.bookshopwebsite.repository.OrderRepository;

import com.group7.bookshopwebsite.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private BookRepository bookRepository;
    private OrderRepository orderRepository;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getAllOrders(Sort sort) {
        return orderRepository.findAll(sort);
    }

    @Override
    public Page<Order> getAllOrdersOnPage(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public void setProcessingOrder(Order order) {
        order.setStatus(OrderStatus.PROCESSING);
        orderRepository.save(order);
    }

    @Override
    public void setDeliveringOrder(Order order) {
        order.setStatus(OrderStatus.DELIVERING);
        orderRepository.save(order);
    }

    @Override
    public void setDeliveredOrder(Order order) {
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
    }

    @Override
    public void setReceivedToOrder(Order order) {
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
    }

    @Override
    public List<Order> getTop10orders() {
        return orderRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Override
    public BigDecimal getTotalRevenue() {
        return orderRepository.sumTotalPrice();
    }

    @Override
    public Long countOrder() {
        return orderRepository.count();
    }

    @Override
    public List<Order> getAllOrdersByUser(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @Override
    public Order createOrder(CartDTO cart, User user, OrderPerson orderPerson) {
        Order order = new Order();
        order.setReciever(orderPerson.getFullName());
        order.setStatus(OrderStatus.PENDING);
        order.setEmailAddress(orderPerson.getEmail());
        order.setShippingAddress(orderPerson.getAddress());
        order.setPhoneNumber(orderPerson.getPhoneNumber());
        order.setTotalPrice(cart.calculateTotalAmount());
        order.setPaymentMethod(PaymentMethod.COD);

        List<CartItemDTO> cartItems = cart.getCartItems();
        for (CartItemDTO cartItem : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            Book book = bookRepository.findById(cartItem.getBookId()).orElse(null);
            orderDetail.setBook(book);
            orderDetail.setQuantity(cartItem.getQuantity());
            assert book != null;
            orderDetail.setPrice(book.getSalePrice());
            order.addOrderDetail(orderDetail);
        }

        order.setUser(user);
        order.setCreatedAt(new Date());
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrder(Order order) {
        return null;
    }

    @Override
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    public void cancelOrder(Order order) {
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    public Page<Order> getOrdersByStatus(String status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    @Override
    public List<Map<String, Object>> getAllOrderStatistics() {
        List<Object[]> stats = orderRepository.findAllOrdersGroupedByDate();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] stat : stats) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", stat[0]); // Ngày
            map.put("totalOrders", ((Number) stat[1]).longValue()); // Tổng số đơn hàng
            result.add(map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getDeliveredOrderRevenues() {
        List<Object[]> stats = orderRepository.findDeliveredOrderRevenuesGroupedByDate();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] stat : stats) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", stat[0]); // Ngày
            map.put("totalRevenue", ((Number) stat[1]).doubleValue()); // Tổng doanh thu
            result.add(map);
        }
        return result;
    }
}
