package web_patterns.samplespring2025.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import web_patterns.samplespring2025.services.UserService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/api/auth/")
public class UserController {
    // Map to store authentication tokens with usernames
    private static final Map<String, String> tokenMap = new ConcurrentHashMap<>();

    // Exception error codes for differentiation between exception issues
    private static final int DUPLICATE_KEY_ERROR_CODE = 1062;
    private static final int FOREIGN_KEY_CONSTRAINT_FAILS = 1452;

    private UserService userService;

    public UserController(UserService userService){
        // Don't create it yourself - facilitate auto-wiring
        // Assume a service class will be provided by spring boot as a parameter
        this.userService = userService;
    }

    @GetMapping(path="/login", produces="application/json")
    public String login(@RequestParam String username, @RequestParam String password){
        try {
            boolean loggedIn = userService.login(username, password);
            if(loggedIn){
                String token = UUID.randomUUID().toString();
                tokenMap.put(token, username);
                return token;
            }
            log.info("Failed login attempt for {}", username);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }catch (IllegalArgumentException e){
            log.error("Null or empty parameters supplied: {}",
                    e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Illegal parameters"
            );
        }catch (SQLException e){
            log.error("User could not be authenticated at this time. Database error occurred: {}",
                    e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Database error"
            );
        }
    }

    @GetMapping("/secure")
    public String secure(@RequestHeader("Authorization") String header) {
        System.out.println("Header information: " + header);
        String token = header.replace("Bearer ", "");

        if (tokenMap.containsKey(token)) {
            return "Hello " + tokenMap.get(token) + ", you're authenticated!";
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
}
