package com.group7.bookshopwebsite.controller.admin;

import com.group7.bookshopwebsite.controller.common.BaseController;
import com.group7.bookshopwebsite.dto.Email;
import com.group7.bookshopwebsite.entity.Contact;
import com.group7.bookshopwebsite.service.ContactService;
import com.group7.bookshopwebsite.service.EmailService;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
@RequestMapping("/admin/contacts")
public class AdminContactController extends BaseController {
    private final ContactService contactService;
    private final EmailService emailService;
    @GetMapping
    public String adminContacts(Model model){
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Contact> contacts = contactService.getAllContacts(sort);
        model.addAttribute("contacts", contacts);

        return "/admin/contacts";
    }
    @GetMapping("/delete/{id}")
    public String deleteContact(@PathVariable Long id){
        contactService.deleteById(id);
        return "redirect:/admin/contacts";
    }

    @GetMapping("/response/{id}")
    public String response(@PathVariable Long id, Model model){
        String userEmail = contactService.getContactById(id).getEmail();
        Email email = new Email();
        email.setTo(userEmail);
        model.addAttribute("newEmail",email);
        model.addAttribute("uid",id);

        return "admin/contact_response";
    }
    @PostMapping("/submit_email")
    public String responseTo(@ModelAttribute Email email,
                             @RequestParam Long uid,
                             RedirectAttributes redirectAttributes){

        emailService.sendSimpleEmail(email.getTo(),email.getSubject(),email.getMessage());
        redirectAttributes.addFlashAttribute("message","Gửi mail thành công!");

        return "redirect:/admin/contacts/response/" + uid + "?success=true";
    }

}
