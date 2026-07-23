package com.orderready.backend.service;

import com.orderready.backend.dto.LoginRequest;
import com.orderready.backend.dto.LoginResponse;
import com.orderready.backend.entity.User;
import com.orderready.backend.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// login işlemlerini yönetir
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı adı veya şifre hatalı"));

        boolean passwordMatches = BCrypt.checkpw(request.getPassword(), user.getPassword());

        if (!passwordMatches) {
            throw new RuntimeException("Kullanıcı adı veya şifre hatalı");
        }

        // rastgele, tahmin edilemez bir token üret ve kullanıcıya kaydet
        String token = java.util.UUID.randomUUID().toString();
        user.setToken(token);
        userRepository.save(user);

        LoginResponse response = new LoginResponse();
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setToken(token);
        return response;
    }

    // token'a göre kullanıcıyı bulur (sayfa yenilendiğinde kimlik doğrulamak için)
    public LoginResponse getUserByToken(String token) {
        User user = userRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Geçersiz oturum"));

        LoginResponse response = new LoginResponse();
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setToken(token);
        return response;
    }
}
