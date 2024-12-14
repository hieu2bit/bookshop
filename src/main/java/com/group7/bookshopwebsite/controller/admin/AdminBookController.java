package com.group7.bookshopwebsite.controller.admin;

import com.group7.bookshopwebsite.controller.common.BaseController;
import com.group7.bookshopwebsite.entity.Book;
import com.group7.bookshopwebsite.entity.Category;
import com.group7.bookshopwebsite.service.BookService;
import com.group7.bookshopwebsite.service.CategoryService;
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

@AllArgsConstructor
@Controller
@RequestMapping("/admin/books_management")
public class AdminBookController extends BaseController {

    private final BookService bookService;
    private final CategoryService categoryService;

    @GetMapping
    public String showBooksPage(Model model) {
        List<Book> books = bookService.findAllOrderByCreatedDate();
        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("books", books);
        model.addAttribute("categories", categories);

        return "admin/books";
    }

    @GetMapping("/add")
    public String showAddBookForm(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("book", new Book());
        return "admin/books_add";
    }

    @PostMapping("/add_or_update")
    public String addOrUpdateBook(
            @ModelAttribute("book") @Valid Book book,
            BindingResult bindingResult,
            @RequestParam("cover_image") MultipartFile coverImage,
            Model model,
            RedirectAttributes redirectAttributes
    ) throws IOException {
    
        // Xử lý lỗi validation
        if (bindingResult.hasErrors()) {
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            model.addAttribute("error", "Đã có lỗi xảy ra, vui lòng nhập lại");
            return "admin/books_add";
        }
    
        // Trường hợp chỉnh sửa sách
        if (book.getId() != null) {
            Book existingBook = bookService.getBookById(book.getId());
            if (existingBook != null) {
                // Nếu không cập nhật ảnh mới, giữ lại ảnh cũ
                if (coverImage.isEmpty()) {
                    book.setCoverImage(existingBook.getCoverImage());
                }
                // Cập nhật thông tin sách
                bookService.editBook(book, coverImage);
    
                // Lấy lại thông tin sách sau khi cập nhật
                Book editedBook = bookService.getBookById(book.getId());
                model.addAttribute("book", editedBook);
                redirectAttributes.addFlashAttribute("message", "Sửa thông tin sách thành công!");
            }
        }
        // Trường hợp thêm mới sách
        else {
            Book exist = bookService.getBookByName(book.getTitle());
            if (exist != null) {
                model.addAttribute("error", "Đã tồn tại sách với tên này");
                return "admin/books_add";
            } else {
                // Thêm sách mới
                bookService.addBook(book, coverImage);
    
                // Lấy lại sách mới từ cơ sở dữ liệu (nếu cần ID)
                Book savedBook = bookService.getBookByName(book.getTitle());
                redirectAttributes.addFlashAttribute("message", "Thêm sách thành công!");
                return "redirect:/admin/books_management/edit/" + savedBook.getId();
            }
        }
    
        // Điều hướng về trang chi tiết sách
        return "redirect:/admin/books_management/edit/" + book.getId();
    }
 
    @GetMapping("/edit/{id}")
    public String showEditBookForm(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("book", book);
        model.addAttribute("categories", categories);

        return "admin/books_update";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookService.deleteBook(id);

        // Add a success message to the model
        redirectAttributes.addFlashAttribute("message", "Xoá sách thành công!");

        return "redirect:/admin/books_management";
    }

}
