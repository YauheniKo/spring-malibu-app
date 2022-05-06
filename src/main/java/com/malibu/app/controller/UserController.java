package com.malibu.app.controller;


import com.malibu.app.config.CurrentUser;
import com.malibu.app.dto.LocalUser;
import com.malibu.app.util.GeneralUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api")
public class UserController {


//    @GetMapping
//    public ResponseEntity<List<User>> getAllUser() {
//        return new ResponseEntity<>(userService.findAllUser(), HttpStatus.OK);
//    }

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getCurrentUser(@CurrentUser LocalUser user) {
        return ResponseEntity.ok(GeneralUtils.buildUserInfo(user));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getContent() {
        return ResponseEntity.ok("Public content goes here");
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserContent() {

        return ResponseEntity.ok("User content goes here");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdminContent() {
        return ResponseEntity.ok("Admin content goes here");
    }

    @GetMapping("/mod")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<?> getModeratorContent() {
        return ResponseEntity.ok("Moderator content goes here");
    }
//    @GetMapping("/{id}")
//    public ResponseEntity<User> getArticleById(@PathVariable("id") long id) {
//        return userService.getUserById(id);
//    }

//    @PutMapping("/{id}")
//    public ResponseEntity<User> updateUser(@PathVariable("id") long id, @RequestBody User user) {
//        return userService.updateUser(id, user);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id) {
//        return userService.deleteUser(id);
//    }


}
