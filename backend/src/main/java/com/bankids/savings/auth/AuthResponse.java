package com.bankids.savings.auth;

public class AuthResponse {

    private String token;
    private String childName;
    private int age;

    public AuthResponse(String token, String childName, int age) {
        this.token = token;
        this.childName = childName;
        this.age = age;
    }

    public String getToken() {
        return token;
    }

    public String getChildName() {
        return childName;
    }

    public int getAge() {
        return age;
    }
}
