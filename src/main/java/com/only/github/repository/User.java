package com.only.github.repository;

import com.only.github.common.helper.JsonHelper;

/**
 * Created by leiteng on 2017/7/3.
 */
public class User {
    private String email;
    private Integer id;
    private String login;
    private String location;
    private String company;
    private Integer status;

    public User() {
    }

    public static User valueOf(String user) {
        return JsonHelper.fromJson(user, User.class);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
