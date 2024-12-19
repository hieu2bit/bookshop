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

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminHomeController extends BaseController {
        private OrderService orderService;
        private UserService userService;
        private BookService bookService;

        @GetMapping
        public String getAdminHomePage(Model model) {
                Map<String, Object> stats = orderService.getOrdersStatsLast7Days();

                model.addAttribute("dates", stats.get("dates"));
                model.addAttribute("totalOrders", stats.get("totalOrders"));
                model.addAttribute("successfulOrders", stats.get("successfulOrders"));
                model.addAttribute("totalRevenues", stats.get("totalRevenues"));

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
