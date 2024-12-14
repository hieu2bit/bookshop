package com.group7.bookshopwebsite.controller.admin;

import com.group7.bookshopwebsite.controller.common.BaseController;
import com.group7.bookshopwebsite.entity.Order;
import com.group7.bookshopwebsite.entity.Book;
import com.group7.bookshopwebsite.service.BookService;
import com.group7.bookshopwebsite.service.OrderService;
import com.group7.bookshopwebsite.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminHomeController extends BaseController {
    private OrderService orderService;
    private UserService userService;
    private BookService bookService;

    @GetMapping
    public String getAdminHomePage(Model model) {
        // Lấy dữ liệu tổng số đơn hàng
        List<Map<String, Object>> allOrderStats = orderService.getAllOrderStatistics();

        // Lấy dữ liệu doanh thu từ đơn hàng đã giao
        List<Map<String, Object>> deliveredRevenues = orderService.getDeliveredOrderRevenues();

        // Kết hợp dữ liệu vào model
        List<String> dates = allOrderStats.stream()
                .map(stat -> stat.get("date").toString())
                .collect(Collectors.toList());
        List<Long> totalOrders = allOrderStats.stream()
                .map(stat -> ((Number) stat.get("totalOrders")).longValue())
                .collect(Collectors.toList());
        List<Double> totalRevenues = dates.stream()
                .map(date -> deliveredRevenues.stream()
                        .filter(stat -> stat.get("date").toString().equals(date))
                        .map(stat -> (Double) stat.get("totalRevenue"))
                        .findFirst()
                        .orElse(0.0))
                .collect(Collectors.toList());

        // Thêm dữ liệu vào model
        model.addAttribute("dates", dates);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenues", totalRevenues);

        // Thêm dữ liệu vào model
        model.addAttribute("dates", dates);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenues", totalRevenues);

        List<Order> orders = orderService.getTop10orders();
        model.addAttribute("orders", orders);
        List<Book> books = bookService.getTop10BestSeller();
        model.addAttribute("books", books);
        BigDecimal totalRevenue = orderService.getTotalRevenue();

        Long numberOfUsers = userService.countUser();
        Long numberOfBooks = bookService.countBook();
        Long numberOfOrders = orderService.countOrder();

        model.addAttribute("numberOfUsers", numberOfUsers);
        model.addAttribute("numberOfBooks", numberOfBooks);
        model.addAttribute("numberOfOrders", numberOfOrders);
        model.addAttribute("totalRevenue", totalRevenue);

        return "admin/index";
    }

}
