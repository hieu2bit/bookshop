package com.group7.bookshopwebsite.service.impl;

import com.group7.bookshopwebsite.entity.Contact;
import com.group7.bookshopwebsite.repository.ContactRepository;
import com.group7.bookshopwebsite.service.ContactService;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
@AllArgsConstructor
@Service
public class ContactServiceImp implements ContactService {
    private ContactRepository contactRepository;
    public Contact saveContact(Contact contact) {
        return contactRepository.save(contact);
    }


    @Override
    public void deleteById(Long id) {
        contactRepository.deleteById(id);
    }

    @Override
    public Contact getContactById(Long id) {
        return contactRepository.findById(id).orElse(null);
    }


    @Override
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }


    @Override
    public List<Contact> getAllContacts(Sort sort) {
        return contactRepository.findAll(sort);
    }
}
