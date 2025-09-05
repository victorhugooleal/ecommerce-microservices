package com.ecommerce.userservice.dto;

/**
 * DTO para resposta de login (com token JWT)
 */
public class LoginResponseDTO {

    private String token;
    private String type = "Bearer";
    private UserResponseDTO user;

    // Construtores
    public LoginResponseDTO() {}

    public LoginResponseDTO(String token, UserResponseDTO user) {
        this.token = token;
        this.user = user;
    }

    // Getters e Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserResponseDTO getUser() {
        return user;
    }

    public void setUser(UserResponseDTO user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "LoginResponseDTO{" +
                "type='" + type + '\'' +
                ", user=" + user +
                '}';
    }
}
