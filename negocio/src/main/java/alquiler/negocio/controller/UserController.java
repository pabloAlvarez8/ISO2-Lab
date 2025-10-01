package alquiler.negocio.controller;

import alquiler.negocio.entity.User;
import alquiler.negocio.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@ModelAttribute User user) {
        User newUser = userService.registerUser(user);
        return ResponseEntity.ok(newUser);
    }
}
