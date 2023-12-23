package com.smart.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entites.User;
import com.smart.helper.Message;

@Controller
public class HomeController {
	
	Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;

	// home handler
	@GetMapping("/")
	public String home(Model model) {
		logger.info("Method : home starts....");
		
		
		model.addAttribute("title", "Home-Smart Contact manager");

		logger.info("Method : home ends");
		return "home";
	}

	// about handler
	@GetMapping("/about")
	public String about(Model model) {

		logger.info("Method : about starts");
		
		model.addAttribute("title", "About-Smart Contact manager");

		logger.info("Method : about ends");
		return "about";
	}

	// sign up handler
	@GetMapping("/signup")
	public String signup(Model model) {
		
		logger.info("Method : signup starts");

		model.addAttribute("title", "Signup-Smart Contact manager");
		model.addAttribute("user", new User());

		logger.info("Method : signup ends");
		return "signup";
	}

	// handler for registering user
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,HttpSession session) {

		logger.info("Method : registerUser starts");
		
		try {

			if (!agreement) {
				System.out.println("You have not agreed terms and conditions....");
				throw new Exception("You have not agreed terms and conditions....");
			}
			
			if (result.hasErrors()) {
				System.out.println("Errors : "+result.toString());
				model.addAttribute("user",user);
				return "signup";
				
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			
			
			System.out.println("Agreement : " + agreement);
			System.out.println("USER : " + user);

			this.userRepository.save(user);

			model.addAttribute("user", new User());
			session.setAttribute("message",new Message("Successfully registered..."  , "alert-success") );
			
			logger.info("Method : registerUser ends");
			
			return "signup";

		} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("user",user);
				session.setAttribute("message",new Message("Something went wrong..." + e.getMessage() , "alert-danger") );
				return "signup";
		}

		
	}
	
	//handler for custom login page
	@GetMapping("/signin")
	public String customLogin(Model model) {

		logger.info("Method : customLogin starts");
		
		model.addAttribute("title", "Login page");

		logger.info("Method : customLogin ends");
		
		return "login";
	}
	
}
