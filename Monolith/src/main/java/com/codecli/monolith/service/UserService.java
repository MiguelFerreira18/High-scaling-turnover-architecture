package com.codecli.monolith.service;

import com.codecli.monolith.Models.Authority;
import com.codecli.monolith.Models.User;
import com.codecli.monolith.dto.SaveUser;
import com.codecli.monolith.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;


    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User saveUser(SaveUser user) {
        User u = new User(user.email(), user.name(), user.password());
        u.addAuthority(new Authority(Authority.Role.USER));
        return this.userRepo.save(u);
    }
    public User saveUser(SaveUser user, PasswordEncoder encoder) {
        User u = new User(user.email(), user.name(), user.password());
        u.setPassword(u.getPassword(),encoder);
        u.addAuthority(new Authority(Authority.Role.USER));
        return this.userRepo.save(u);
    }

    @Transactional
    public @Nullable UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        return optionalUser.orElse(null);
    }
}
