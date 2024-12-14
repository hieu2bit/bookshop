package com.group7.bookshopwebsite.controller;

import com.group7.bookshopwebsite.controller.common.BaseController;
import com.group7.bookshopwebsite.entity.User;
import com.group7.bookshopwebsite.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@AllArgsConstructor
@Controller
@RequestMapping("/profile")
public class ProfileController extends BaseController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String getUser(Model model) {
        User user = userService.getUserById(getCurrentUser().getId());
        model.addAttribute("user", user);
        return "user/profile";
    }

    @PostMapping("/update")
    public String updateUser(
            @ModelAttribute("user") @Valid User user,
            BindingResult bindingResult,
            @RequestParam(value = "image", required = false) MultipartFile image,
            RedirectAttributes redirectAttributes) {

        // Lấy thông tin người dùng hiện tại từ DB
        User existingUser = userService.getUserById(user.getId());
        if (existingUser == null) {
            redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại!");
            return "redirect:/profile";
        }

        // Cập nhật thông tin từ form
        existingUser.setFullName(user.getFullName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setGender(user.getGender());
        existingUser.setBirthday(user.getBirthday());
        existingUser.setAddress(user.getAddress());
        existingUser.setNotes(user.getNotes());
        userService.updateUser(existingUser, image);

        redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin thành công!");
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {
        User user = userService.getUserById(getCurrentUser().getId());
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu cũ không trùng khớp!");
            return "redirect:/profile";
        }

        if (oldPassword.equals(newPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu cũ trùng với mật khẩu mới!");
            return "redirect:/profile";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới không trùng khớp!");
            return "redirect:/profile";
        }

        try {
            userService.changePassword(user, newPassword);
            redirectAttributes.addFlashAttribute("message", "Đổi mật khẩu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/profile";
    }

}
