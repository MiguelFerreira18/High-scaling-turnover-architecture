package com.codecli.monolith.Models;

import java.util.List;

public class UserView {
    String id;
    String name;
    List<String> authorities;

    public UserView(String id, String name, List<String> authorities) {
        this.id = id;
        this.name = name;
        this.authorities = authorities;
    }

    public UserView() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

}
