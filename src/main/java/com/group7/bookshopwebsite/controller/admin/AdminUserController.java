package com.group7.bookshopwebsite.controller.admin;

import com.group7.bookshopwebsite.controller.common.BaseController;
import com.group7.bookshopwebsite.entity.User;
import com.group7.bookshopwebsite.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@AllArgsConstructor
@Controller
@RequestMapping("admin/users_management")
public class AdminUserController extends BaseController {

    private final UserService userService;

    @GetMapping
    public String getUsersPage(@RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(value = "roles", required = false) String roles,
            Model model) {
        int pageSize = 100;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        Page<User> usersPage = userService.getAllUserOrderByCreatedDate(pageable);

        if (roles != null && !roles.isEmpty()) {
            usersPage = userService.getAllUserOrderByRoles(roles, pageable);
            model.addAttribute("selectedRoles", roles);
        }

        model.addAttribute("users", usersPage);
        return "admin/users";
    }

    @PostMapping("/update")
    public String updateUser(
            @ModelAttribute("user") @Valid User user,
            BindingResult bindingResult,
            @RequestParam("image") MultipartFile image,
            Model model,
            RedirectAttributes redirectAttributes) throws IOException {

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Thông tin không hợp lệ!");
            return "/admin/user_update";
        }

        if (user.getId() != null) {
            User existingUser = userService.getUserById(user.getId());
            if (existingUser == null) {
                redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại!");
                return "redirect:/admin/users_management";
            }

            if (image.isEmpty()) {
                user.setImage(existingUser.getImage());
            }

            userService.updateUser(user, image);
            redirectAttributes.addFlashAttribute("message", "Sửa thông tin thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "ID người dùng không hợp lệ!");
        }

        return "redirect:/admin/users_management/edit/" + user.getId();
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "/admin/user_update";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUserById(id);
        redirectAttributes.addFlashAttribute("message", "Người dùng đã được xóa thành công!");
        return "redirect:/admin/users_management";
    }
}
