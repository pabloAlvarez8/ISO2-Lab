package inmobiliaria.es.uclm.negocio.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // 1. Declaramos la dependencia como 'final' (inmutable)
    private final UserService userService;

    // 2. Creamos el constructor para la inyección de dependencias
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@ModelAttribute User user) {
        User newUser = userService.registerUser(user);
        return ResponseEntity.ok(newUser);
    }
}