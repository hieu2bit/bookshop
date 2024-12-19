package com.group7.bookshopwebsite.repository;

import com.group7.bookshopwebsite.entity.Order;
import com.group7.bookshopwebsite.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

        List<Order> findByUserOrderByCreatedAtDesc(User user);

        Page<Order> findByStatus(String status, Pageable pageable);

        List<Order> findTop10ByOrderByCreatedAtDesc();
        
        @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
        List<Order> findByCreatedAtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
        
        @Query("SELECT SUM(o.totalPrice) FROM Order o where  o.status = 'DELIVERED'")
        BigDecimal sumTotalPrice();
}
