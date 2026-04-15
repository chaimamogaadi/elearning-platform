package com.elearning.backend.controller;

import com.elearning.backend.model.Certificate;
import com.elearning.backend.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/certificate")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    // GET /api/certificate/course/{courseId}
    // Download PDF certificate
    @GetMapping("/course/{courseId}")
    public ResponseEntity<byte[]> getCertificate(
            @PathVariable Long courseId,
            Authentication auth) {
        try {
            byte[] pdf = certificateService
                    .generateCertificate(
                            courseId,
                            auth.getName());

            // Return as downloadable PDF file
            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=certificate.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // GET /api/certificate/my-certificates
    // Get list of all earned certificates
    @GetMapping("/my-certificates")
    public ResponseEntity<List<Certificate>>
    getMyCertificates(Authentication auth) {
        return ResponseEntity.ok(
                certificateService.getMyCertificates(
                        auth.getName()));
    }

    // GET /api/certificate/verify/{certNumber}
    // Anyone can verify a certificate — PUBLIC
    @GetMapping("/verify/{certNumber}")
    public ResponseEntity<Certificate> verify(
            @PathVariable String certNumber) {
        return ResponseEntity.ok(
                certificateService.verifyCertificate(
                        certNumber));
    }
}