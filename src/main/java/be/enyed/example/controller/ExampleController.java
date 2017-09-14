package be.enyed.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import be.enyed.example.service.ExampleService;

@Controller
public class ExampleController {

	private final ExampleService store;
	
	@Autowired
	public ExampleController(ExampleService store) {
		this.store = store;
	}
	
	@GetMapping("/")
	public String home(){
		return "redirect:budget";
	}
	
	@GetMapping("/budget")
	public String budgetlines(Model model){
		model.addAttribute("store",store)
	     	 .addAttribute("user",store.getCurrentUser());
		return "budgetlines";
	}
	
	@GetMapping("/hello")
	public String test(){
		return "hello";
	}
		
}
