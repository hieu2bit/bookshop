package com.group7.bookshopwebsite.service;

import com.group7.bookshopwebsite.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    Page<User> getAllUserOrderByCreatedDate(Pageable pageable);

    User getUserById(Long userId);

    List<User> getAllUsers();

    void updateUser(User user, MultipartFile image);
    void changePassword(User user,String newPassword);

    void deleteUser(Long userId);

    boolean registerUser(User user);

    void deleteUserById(Long id);

    void saveUser(User user);

    void addBookToUser(Long userId, Long bookId);

    void removeBookFromUser(Long userId, Long bookId);

    Long countUser();
}
