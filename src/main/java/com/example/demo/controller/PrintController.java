package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.demo.objects.ImpresionInfo;
import com.example.demo.schedule.Schedule;

@Controller    // This means that this class is a Controller
@RequestMapping(path="/printer") // This means URL's start with /demo (after Application path)
public class PrintController {
	int contador = 0;
	@GetMapping(path="/sample")
	public @ResponseBody String sample() {
		return "true";
	}
	
	
	@PostMapping(path="/printDocument/")
	public @ResponseBody String printDocument(@RequestBody String message) {
		contador++;
		Schedule.colaImpresion.add(new ImpresionInfo(message, contador));
		return "Ticket "+contador+" agregado a la cola";
	}
}
