package com.codecli.monolith.init;

import com.codecli.monolith.Models.User;
import com.codecli.monolith.dto.SaveUser;
import com.codecli.monolith.repo.UserRepo;
import com.codecli.monolith.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(BootstrapOrder.USERS)
public class UserBootstrap implements CommandLineRunner {
    private final UserService userService;
    private final PasswordEncoder encoder;

    public UserBootstrap(UserService userService, PasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

    public void run(String... args) throws Exception {
        createUsers(100);
    }

    private void createUsers(int nUsers) {
        for (int i = 0; i < nUsers; i++) {
            SaveUser u = new SaveUser("test" + i + 1 + "@gmail.com", "test" + i + 1, "273909711", "password");
            if (userService.loadUserByUsername(u.email()) == null) {
                userService.saveUser(u, encoder);
            }
        }
    }
}
