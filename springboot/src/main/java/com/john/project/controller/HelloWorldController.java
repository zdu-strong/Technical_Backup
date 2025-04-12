package com.john.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.john.project.common.baseController.BaseController;

@RestController
public class HelloWorldController extends BaseController {

	@GetMapping("/")
	public ResponseEntity<?> helloWorld() {
		return ResponseEntity.ok("Hello, World!");
	}

}
