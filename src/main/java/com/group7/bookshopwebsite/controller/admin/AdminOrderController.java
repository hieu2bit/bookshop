package com.group7.bookshopwebsite.controller.admin;

import com.group7.bookshopwebsite.controller.common.BaseController;
import com.group7.bookshopwebsite.entity.Order;
import com.group7.bookshopwebsite.entity.OrderDetail;
import com.group7.bookshopwebsite.service.OrderDetailService;
import com.group7.bookshopwebsite.service.OrderService;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/orders_management")
@AllArgsConstructor
public class AdminOrderController extends BaseController {

    private final OrderService orderService;
    private OrderDetailService orderDetailService;

    @GetMapping
    public String getAllOrders(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "status", required = false) String status,
            Model model) {

        int pageSize = 100;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());

        Page<Order> orderPage = orderService.getAllOrdersOnPage(pageable);;
        if (status != null && !status.isEmpty()) {
            orderPage = orderService.getOrdersByStatus(status, pageable);
        }

        model.addAttribute("orders", orderPage);
        model.addAttribute("selectedStatus", status);
        return "admin/orders";
    }

    @GetMapping("/details/{id}")
    public String details(Model model, @PathVariable Long id) {

        Order order = orderService.getOrderById(id);
        List<OrderDetail> orderDetails = orderDetailService.getAllOrderDetailByOrder(order);
        model.addAttribute("order", order);

        model.addAttribute("ordersDetails", orderDetails);

        return "admin/order_detail";
    }

    @GetMapping("/details/process/{id}")
    public String process(@PathVariable Long id) {

        Order order = orderService.getOrderById(id);
        orderService.setProcessingOrder(order);

        return "redirect:/admin/orders_management/details/" + id;
    }

    @GetMapping("/details/deliver/{id}")
    public String deliver(@PathVariable Long id) {

        Order order = orderService.getOrderById(id);
        orderService.setDeliveringOrder(order);

        return "redirect:/admin/orders_management/details/" + id;
    }

    @GetMapping("/details/delivered/{id}")
    public String delivered(@PathVariable Long id) {

        Order order = orderService.getOrderById(id);
        orderService.setDeliveredOrder(order);

        return "redirect:/admin/orders_management/details/" + id;
    }

    @GetMapping("/details/cancel/{id}")
    public String cancel(@PathVariable Long id) {

        Order order = orderService.getOrderById(id);
        orderService.cancelOrder(order);

        return "redirect:/admin/orders_management/details/" + id;
    }
}
