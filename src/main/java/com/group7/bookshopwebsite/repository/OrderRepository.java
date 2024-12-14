package com.group7.bookshopwebsite.repository;

import com.group7.bookshopwebsite.entity.Order;
import com.group7.bookshopwebsite.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);

    Page<Order> findByStatus(String status, Pageable pageable);

    List<Order> findTop10ByOrderByCreatedAtDesc();

    @Query("SELECT FUNCTION('DATE', o.createdAt) AS date, COUNT(o.id) AS totalOrders " +
            "FROM Order o " +
            "GROUP BY FUNCTION('DATE', o.createdAt) " +
            "ORDER BY date ASC")
    List<Object[]> findAllOrdersGroupedByDate();

    @Query("SELECT FUNCTION('DATE', o.createdAt) AS date, SUM(o.totalPrice) AS totalRevenue " +
            "FROM Order o " +
            "WHERE o.status = 'DELIVERED' " +
            "GROUP BY FUNCTION('DATE', o.createdAt) " +
            "ORDER BY date ASC")
    List<Object[]> findDeliveredOrderRevenuesGroupedByDate();

    @Query("SELECT SUM(o.totalPrice) FROM Order o where  o.status = 'DELIVERED'")
    BigDecimal sumTotalPrice();
}
