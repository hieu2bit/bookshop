package com.group7.bookshopwebsite.controller.admin;

import com.group7.bookshopwebsite.controller.common.BaseController;
import com.group7.bookshopwebsite.entity.Category;
import com.group7.bookshopwebsite.service.CategoryService;
import lombok.AllArgsConstructor;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
@RequestMapping("/admin/categories_management")
public class AdminCategoryController extends BaseController {
    private final CategoryService categoryService;

    @GetMapping()
    public String getAllCategories(Model model) {
        List<Category> category = categoryService.getAllCategories();

        model.addAttribute("categories", category);

        return "admin/categories";
    }

    @GetMapping("/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category_add";
    }

    @PostMapping("/add_or_update")
    public String addOrUpdateCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        if (category.getId() != null) {
            // Có ID -> Cập nhật
            categoryService.updateCategory(category.getId(), category);
            redirectAttributes.addFlashAttribute("message", "Cập nhật danh mục thành công");

        } else {
            // Không có ID -> Thêm mới
            categoryService.addCategory(category);
            redirectAttributes.addFlashAttribute("message", "Thêm mới danh mục thành công");
        }
        return "redirect:/admin/categories_management/edit/" + category.getId();
    }

    @GetMapping("/edit/{id}")
    public String editCategory(Model model, @PathVariable Long id) {
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "admin/category_update";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.deleteCategory(id);
        redirectAttributes.addFlashAttribute("message", "Xoá danh mục thành công");

        return "redirect:/admin/categories_management";
    }
}