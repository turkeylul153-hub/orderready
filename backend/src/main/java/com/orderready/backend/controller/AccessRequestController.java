package com.orderready.backend.controller;

import com.orderready.backend.entity.AccessRequest;
import com.orderready.backend.service.AccessRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/access-requests")
public class AccessRequestController {

    @Autowired
    private AccessRequestService accessRequestService;

    // POST /api/access-requests - yeni talep oluşturur
    @PostMapping
    public AccessRequest createRequest(@RequestBody CreateRequestBody body) {
        return accessRequestService.createRequest(body.getUserId(), body.getRequestedRole());
    }

    // GET /api/access-requests - bekleyen talepleri listeler (admin ekranı için)
    @GetMapping
    public List<AccessRequest> getPendingRequests() {
        return accessRequestService.getPendingRequests();
    }

    // PUT /api/access-requests/{id}/approve - talebi onaylar
    @PutMapping("/{id}/approve")
    public AccessRequest approve(@PathVariable Long id) {
        return accessRequestService.approveRequest(id);
    }

    // PUT /api/access-requests/{id}/reject - talebi reddeder
    @PutMapping("/{id}/reject")
    public AccessRequest reject(@PathVariable Long id) {
        return accessRequestService.rejectRequest(id);
    }

    public static class CreateRequestBody {
        private Long userId;
        private String requestedRole;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getRequestedRole() { return requestedRole; }
        public void setRequestedRole(String requestedRole) { this.requestedRole = requestedRole; }
    }
}