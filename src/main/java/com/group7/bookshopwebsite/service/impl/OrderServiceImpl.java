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
import com.group7.bookshopwebsite.repository.OrderDetailRepository;
import com.group7.bookshopwebsite.repository.OrderRepository;

import com.group7.bookshopwebsite.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AllArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private BookRepository bookRepository;
    private OrderRepository orderRepository;
    private OrderDetailRepository orderDetailRepository;

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
            book.setBuyCount(book.getBuyCount() + cartItem.getQuantity());
            book.setQty(book.getQty() - cartItem.getQuantity());
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
        List<OrderDetail> oDetails = orderDetailRepository.findByOrder(order);
        for (OrderDetail orderDetail : oDetails) {
            Book book = orderDetail.getBook();
            book.setBuyCount(book.getBuyCount() - orderDetail.getQuantity());
            book.setQty(book.getQty() + orderDetail.getQuantity());
        }
        orderRepository.save(order);
    }

    @Override
    public Page<Order> getOrdersByStatus(String status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    @Override
    public Map<String, Object> getOrdersStatsLast7Days() {
        LocalDate now = LocalDate.now();
        List<LocalDate> last7Days = IntStream.rangeClosed(0, 6)
        .mapToObj(i -> now.minusDays(6 - i))
        .collect(Collectors.toList());

        List<Order> orders = orderRepository.findByCreatedAtBetween(
                Date.from(now.minusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(now.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        Map<LocalDate, Long> orderCounts = orders.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        Collectors.counting()));

        Map<LocalDate, Long> successOrderCounts = orders.stream()
                .filter(o -> "DELIVERED".equals(o.getStatus()))
                .collect(Collectors.groupingBy(
                        o -> o.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        Collectors.counting()));

        Map<LocalDate, BigDecimal> revenues = orders.stream()
                .filter(o -> "DELIVERED".equals(o.getStatus()))
                .collect(Collectors.groupingBy(
                        o -> o.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        Collectors.mapping(
                                Order::getTotalPrice,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::valueOf, BigDecimal::add))));

        List<String> dates = new ArrayList<>();
        List<Long> totalOrders = new ArrayList<>();
        List<Long> successfulOrders = new ArrayList<>();
        List<BigDecimal> totalRevenues = new ArrayList<>();

        for (LocalDate date : last7Days) {
            dates.add(date.toString());
            totalOrders.add(orderCounts.getOrDefault(date, 0L));
            successfulOrders.add(successOrderCounts.getOrDefault(date, 0L));
            totalRevenues.add(revenues.getOrDefault(date, BigDecimal.ZERO));
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("dates", dates);
        stats.put("totalOrders", totalOrders);
        stats.put("successfulOrders", successfulOrders);
        stats.put("totalRevenues", totalRevenues);

        return stats;
    }
}
