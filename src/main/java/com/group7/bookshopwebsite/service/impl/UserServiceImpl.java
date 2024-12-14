package com.group7.bookshopwebsite.service.impl;

import com.group7.bookshopwebsite.entity.Book;
import com.group7.bookshopwebsite.entity.Role;
import com.group7.bookshopwebsite.entity.User;
import com.group7.bookshopwebsite.repository.BookRepository;
import com.group7.bookshopwebsite.repository.RoleRepository;
import com.group7.bookshopwebsite.repository.UserRepository;
import com.group7.bookshopwebsite.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<User> getAllUserOrderByCreatedDate(Pageable pageable) {
        return userRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> 
            new RuntimeException("User with ID " + userId + " not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void updateUser(User user, MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            saveUserImage(user, image);
        }
        userRepository.save(user); // Lưu thông tin đã cập nhật
    }
    
    

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User with ID " + userId + " does not exist");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public boolean registerUser(User user) {
        // Kiểm tra email đã tồn tại
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email này đã được sử dụng.");
        }
        if (userRepository.findByPhoneNumber(user.getPhoneNumber()) != null) {
            throw new RuntimeException("Số điện thoại này đã được sử dụng.");
        }
    
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }
    
        user.getRoles().add(userRole);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }
     
    @Override
    public void deleteUserById(Long id) {
        deleteUser(id);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void addBookToUser(Long userId, Long bookId) {
        User user = userRepository.findById(userId).orElseThrow(() -> 
            new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> 
            new RuntimeException("Book not found"));

        user.addFavoriteBook(book);
        userRepository.save(user);
    }

    @Override
    public void removeBookFromUser(Long userId, Long bookId) {
        User user = userRepository.findById(userId).orElseThrow(() -> 
            new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> 
            new RuntimeException("Book not found"));

        user.removeFavoriteBook(book);
        userRepository.save(user);
    }

    @Override
    public Long countUser() {
        return userRepository.count();
    }

    private void saveUserImage(User user, MultipartFile image) {
                
        String originalFileName = image.getOriginalFilename();
        String uniqueFileName = generateUniqueFileName(originalFileName);        
        Path coverImagePath = Paths.get("src/main/resources/static/user_images/" + uniqueFileName);

        try {
            Files.copy(image.getInputStream(), coverImagePath, StandardCopyOption.REPLACE_EXISTING);
            user.setImage(uniqueFileName);
            userRepository.save(user);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return timestamp + "_" + originalFileName;
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);        
    }
    
}
