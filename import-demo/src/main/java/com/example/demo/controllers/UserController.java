package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.User;
import com.example.demo.services.UserService;

@Controller
public class UserController {
	@Autowired
	private UserService userService;

	@GetMapping(value = "/")
	public String home(Model model) {
		model.addAttribute("user", new User());
		List<User> users = userService.findAll();
		model.addAttribute("users", users);
		return "view/users";
	}
	@PostMapping(value="/fileupload")
public String uploadFile(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
		boolean isFlag = userService.saveDataFromUploadFile(user.getFile());
		if(isFlag) {
			redirectAttributes.addFlashAttribute("successmessage","File Upload Successfully");
		}else {
			redirectAttributes.addFlashAttribute("errormessage","File Upload not done, please try again");
		}
	return "redirect:/";
	
}
}
