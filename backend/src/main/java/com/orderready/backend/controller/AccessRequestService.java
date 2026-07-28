package com.orderready.backend.service;

import com.orderready.backend.entity.AccessRequest;
import com.orderready.backend.entity.User;
import com.orderready.backend.repository.AccessRequestRepository;
import com.orderready.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// kullanıcıların ek rol taleplerini yönetir
@Service
public class AccessRequestService {

    @Autowired
    private AccessRequestRepository accessRequestRepository;

    @Autowired
    private UserRepository userRepository;

    // yeni bir talep oluşturur
    public AccessRequest createRequest(Long userId, String requestedRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        AccessRequest request = new AccessRequest();
        request.setUser(user);
        request.setRequestedRole(requestedRole);
        request.setStatus("PENDING");
        request.setCreatedAt(LocalDateTime.now());

        return accessRequestRepository.save(request);
    }

    // sadece bekleyen talepleri listeler (admin ekranı için)
    public List<AccessRequest> getPendingRequests() {
        return accessRequestRepository.findByStatus("PENDING");
    }

    // talebi onaylar: durumu günceller VE kullanıcının gerçek rolüne ekler
    public AccessRequest approveRequest(Long requestId) {
        AccessRequest request = accessRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Talep bulunamadı"));

        User user = request.getUser();
        List<String> currentRoles = new java.util.ArrayList<>(java.util.Arrays.asList(user.getRole().split(",")));

        if (!currentRoles.contains(request.getRequestedRole())) {
            currentRoles.add(request.getRequestedRole());
        }

        user.setRole(String.join(",", currentRoles));
        userRepository.save(user);

        request.setStatus("APPROVED");
        return accessRequestRepository.save(request);
    }

    // talebi reddeder: sadece durumu günceller, role dokunmaz
    public AccessRequest rejectRequest(Long requestId) {
        AccessRequest request = accessRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Talep bulunamadı"));

        request.setStatus("REJECTED");
        return accessRequestRepository.save(request);
    }
}