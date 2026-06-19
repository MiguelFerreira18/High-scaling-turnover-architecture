package com.codecli.monolith.Models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class SignUpRequest {
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username must contain only letters and numbers")
    public String name;

    @NotNull
    @Email(message = "Invalid email format")
    public String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,128}$", message = "Password must contain at least one letter and one number")
    public String password;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,128}$", message = "Password must contain at least one letter and one number")
    public String repeatPassword;

    public @NotNull @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username must contain only letters and numbers") String getName() {
        return name;
    }

    public @NotNull @Email(message = "Invalid email format") String getEmail() {
        return email;
    }

    public @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,128}$", message = "Password must contain at least one letter and one number") String getPassword() {
        return password;
    }

    public @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,128}$", message = "Password must contain at least one letter and one number") String getRepeatPassword() {
        return repeatPassword;
    }

    public SignUpRequest(String name, String email, String password, String repeatPassword) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.repeatPassword = repeatPassword;
    }

    public SignUpRequest() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
