package com.example.demo.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entities.User;

public interface UserService {

	List<User> findAll();

	boolean saveDataFromUploadFile(MultipartFile file);

}
