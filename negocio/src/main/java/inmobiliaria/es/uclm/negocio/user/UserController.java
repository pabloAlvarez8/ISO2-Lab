package inmobiliaria.es.uclm.negocio.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encargado de exponer la API pública para la gestión de usuarios.
 * <p>
 * Proporciona endpoints HTTP que permiten a clientes externos realizar operaciones
 * de registro y consulta, devolviendo respuestas estructuradas (JSON).
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * Inyección del servicio de negocio para delegar la lógica de usuarios.
     */
    @Autowired
    private UserService userService;

    /**
     * Endpoint para el registro de nuevos usuarios vía API.
     * <p>
     * Procesa una petición POST, vincula los parámetros recibidos al objeto {@link User}
     * y delega la creación al servicio.
     *
     * @param user El objeto de dominio poblado automáticamente con los datos de la petición (form-data).
     * @return {@link ResponseEntity} conteniendo el usuario registrado y el código de estado HTTP 200 (OK).
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@ModelAttribute User user) {
        User newUser = userService.registerUser(user);
        return ResponseEntity.ok(newUser);
    }
}
