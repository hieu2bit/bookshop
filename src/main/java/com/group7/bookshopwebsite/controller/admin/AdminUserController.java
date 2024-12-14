package com.group7.bookshopwebsite.controller.admin;

import com.group7.bookshopwebsite.controller.common.BaseController;
import com.group7.bookshopwebsite.entity.User;
import com.group7.bookshopwebsite.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Controller
@RequestMapping("admin/users_management")
public class AdminUserController extends BaseController{

    private final UserService userService;

    @GetMapping
    public String getUsersPage(@RequestParam(value = "roles", required = false) String roles, Model model) {
        List<User> users = userService.getAllUsers();

        if (roles != null && !roles.isEmpty()) {
            users = users.stream()
                    .filter(user -> user.getRoles().stream()
                            .anyMatch(role -> role.getName().equals(roles))) // Kiểm tra role.name
                    .collect(Collectors.toList());
            model.addAttribute("selectedRoles", roles);
        }

        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/update")
    public String updateUser(
            @ModelAttribute("user") @Valid User user,
            BindingResult bindingResult,
            @RequestParam("image") MultipartFile image,
            Model model,
            RedirectAttributes redirectAttributes) throws IOException {

        // Kiểm tra lỗi đầu vào (nếu có)
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

            // Xử lý ảnh (nếu có)
            if (image.isEmpty()) {
                user.setImage(existingUser.getImage()); // Giữ ảnh cũ nếu không có ảnh mới
            }

            // Cập nhật thông tin người dùng
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
