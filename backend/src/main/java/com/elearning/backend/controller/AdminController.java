package com.elearning.backend.controller;

import com.elearning.backend.dto.AdminStatsResponse;
import com.elearning.backend.dto.UserResponse;
import com.elearning.backend.model.Course;
import com.elearning.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // GET /api/admin/stats
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    // GET /api/admin/users
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(adminService.searchUsers(search));
        }
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    // GET /api/admin/users/pending-instructors
    @GetMapping("/users/pending-instructors")
    public ResponseEntity<List<UserResponse>> getPending() {
        return ResponseEntity.ok(adminService.getPendingInstructors());
    }

    // PUT /api/admin/users/{id}/approve-instructor
    @PutMapping("/users/{id}/approve-instructor")
    public ResponseEntity<UserResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approveInstructor(id));
    }

    // PUT /api/admin/users/{id}/reject-instructor
    @PutMapping("/users/{id}/reject-instructor")
    public ResponseEntity<UserResponse> reject(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.rejectInstructor(id));
    }

    // PUT /api/admin/users/{id}/toggle-status
    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<UserResponse> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleUserStatus(id));
    }

    // DELETE /api/admin/users/{id}
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    // GET /api/admin/courses
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(adminService.getAllCourses());
    }

    // PUT /api/admin/courses/{id}/approve
    @PutMapping("/courses/{id}/approve")
    public ResponseEntity<Course> approveCourse(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approveCourse(id));
    }

    // PUT /api/admin/courses/{id}/reject
    @PutMapping("/courses/{id}/reject")
    public ResponseEntity<Course> rejectCourse(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.rejectCourse(id));
    }

    // DELETE /api/admin/courses/{id}
    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Map<String, String>> deleteCourse(@PathVariable Long id) {
        adminService.deleteCourse(id);
        return ResponseEntity.ok(Map.of("message", "Course deleted successfully"));
    }
}