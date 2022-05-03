package com.malibu.springmalibuapp.controller;


import com.malibu.springmalibuapp.model.Article;
import com.malibu.springmalibuapp.model.User;
import com.malibu.springmalibuapp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping
    public ResponseEntity<List<User>> getAllUser(@RequestParam(required = false) String email) {
        return userService.getAllUser(email);
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getArticleById(@PathVariable("id") long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id) {
        return userService.deleteUser(id);
    }






    //TODO drop late
    @GetMapping("/admin")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/mod")
    public String moderatorAccess() {
        return "Moderator Board.";
    }


}
