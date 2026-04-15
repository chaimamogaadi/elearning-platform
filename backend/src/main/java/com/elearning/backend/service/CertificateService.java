package com.elearning.backend.service;

import com.elearning.backend.model.*;
import com.elearning.backend.repository.*;
import com.itextpdf.html2pdf.HtmlConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository  certificateRepository;
    private final UserRepository         userRepository;
    private final CourseRepository       courseRepository;
    private final EnrollmentRepository   enrollmentRepository;
    private final QuizResultRepository   quizResultRepository;

    // ===== GENERATE OR GET CERTIFICATE =====
    public byte[] generateCertificate(
            Long courseId,
            String studentEmail) throws Exception {

        User student = userRepository
                .findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        Course course = courseRepository
                .findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));

        // Check enrollment
        if (!enrollmentRepository
                .existsByStudentAndCourse(student, course)) {
            throw new RuntimeException(
                    "You are not enrolled in this course");
        }

        // Check if student passed any quiz for this course
        List<QuizResult> results =
                quizResultRepository.findByStudent(student);

        QuizResult passedResult = results.stream()
                .filter(r ->
                        r.getQuiz().getCourse().getId()
                                .equals(courseId) &&
                                r.getPassed())
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "You must pass the quiz first " +
                                        "to get a certificate"));

        // Check if certificate already exists
        // If yes — regenerate PDF from existing record
        Certificate cert = certificateRepository
                .findByStudentAndCourse(student, course)
                .orElseGet(() -> {
                    // Create new certificate record
                    Certificate newCert = new Certificate();
                    newCert.setStudent(student);
                    newCert.setCourse(course);
                    newCert.setQuizScore(
                            passedResult.getScore());
                    newCert.setQuizPercentage(
                            passedResult.getPercentage());
                    return certificateRepository.save(newCert);
                });

        // Generate and return PDF bytes
        return buildPdf(cert);
    }

    // ===== GET ALL MY CERTIFICATES =====
    public List<Certificate> getMyCertificates(
            String studentEmail) {
        User student = userRepository
                .findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));
        return certificateRepository.findByStudent(student);
    }

    // ===== VERIFY CERTIFICATE (PUBLIC) =====
    public Certificate verifyCertificate(
            String certNumber) {
        return certificateRepository
                .findByCertificateNumber(certNumber)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Certificate not found or invalid"));
    }

    // ===== BUILD THE PDF =====
    private byte[] buildPdf(
            Certificate cert) throws Exception {

        String html = buildHtml(cert);

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        HtmlConverter.convertToPdf(html, out);

        return out.toByteArray();
    }

    // ===== BUILD THE HTML TEMPLATE =====
    private String buildHtml(Certificate cert) {
        String studentName =
                cert.getStudent().getFullName();
        String courseTitle =
                cert.getCourse().getTitle();
        String instructorName =
                cert.getCourse().getInstructor() != null
                        ? cert.getCourse()
                        .getInstructor()
                        .getFullName()
                        : "ELearn Instructor";
        String certNumber =
                cert.getCertificateNumber();
        String issueDate = cert.getIssuedAt()
                .format(DateTimeFormatter
                        .ofPattern("MMMM dd, yyyy"));
        String score =
                cert.getQuizPercentage() + "%";

        return """
            <!DOCTYPE html>
            <html>
            <head>
            <meta charset="UTF-8"/>
            <style>
              @page {
                size: A4 landscape;
                margin: 0;
              }
              * {
                margin: 0; padding: 0;
                box-sizing: border-box;
              }
              body {
                width: 297mm;
                height: 210mm;
                font-family: Georgia, serif;
                background: #fff;
                display: flex;
                align-items: center;
                justify-content: center;
              }
              .certificate {
                width: 277mm;
                height: 190mm;
                margin: 10mm auto;
                border: 3px solid #c9a227;
                padding: 10mm 15mm;
                position: relative;
                background: #fffdf5;
                text-align: center;
              }
              .outer-border {
                position: absolute;
                top: 4mm; left: 4mm;
                right: 4mm; bottom: 4mm;
                border: 1px solid #c9a227;
                pointer-events: none;
              }
              .corner {
                position: absolute;
                width: 15mm; height: 15mm;
                color: #c9a227;
                font-size: 20pt;
              }
              .corner-tl { top: 3mm; left: 3mm; }
              .corner-tr { top: 3mm; right: 3mm; }
              .corner-bl { bottom: 3mm; left: 3mm; }
              .corner-br { bottom: 3mm; right: 3mm; }
              .logo {
                font-size: 28pt;
                margin-bottom: 2mm;
              }
              .platform-name {
                font-size: 13pt;
                color: #667eea;
                font-weight: bold;
                letter-spacing: 4px;
                text-transform: uppercase;
                margin-bottom: 6mm;
                font-family: Arial, sans-serif;
              }
              .cert-title {
                font-size: 30pt;
                color: #c9a227;
                letter-spacing: 2px;
                margin-bottom: 4mm;
                font-style: italic;
              }
              .subtitle {
                font-size: 11pt;
                color: #888;
                letter-spacing: 3px;
                text-transform: uppercase;
                margin-bottom: 6mm;
                font-family: Arial, sans-serif;
              }
              .presented-to {
                font-size: 10pt;
                color: #999;
                font-family: Arial, sans-serif;
                margin-bottom: 3mm;
                letter-spacing: 2px;
                text-transform: uppercase;
              }
              .student-name {
                font-size: 36pt;
                color: #1e1e2e;
                font-style: italic;
                margin-bottom: 5mm;
                border-bottom: 1px solid #c9a227;
                display: inline-block;
                padding-bottom: 2mm;
                min-width: 150mm;
              }
              .completion-text {
                font-size: 10pt;
                color: #666;
                font-family: Arial, sans-serif;
                margin-bottom: 3mm;
                line-height: 1.6;
              }
              .course-name {
                font-size: 20pt;
                color: #667eea;
                font-style: italic;
                margin-bottom: 5mm;
              }
              .score-badge {
                display: inline-block;
                background: #667eea;
                color: white;
                padding: 2mm 8mm;
                border-radius: 20px;
                font-size: 10pt;
                font-family: Arial, sans-serif;
                font-weight: bold;
                margin-bottom: 7mm;
              }
              .footer {
                display: flex;
                justify-content: space-between;
                align-items: flex-end;
                margin-top: 4mm;
              }
              .signature-block {
                text-align: center;
                width: 60mm;
              }
              .signature-line {
                border-top: 1px solid #333;
                margin-bottom: 2mm;
                width: 55mm;
              }
              .signature-label {
                font-size: 8pt;
                color: #888;
                font-family: Arial, sans-serif;
                letter-spacing: 1px;
                text-transform: uppercase;
              }
              .signature-name {
                font-size: 10pt;
                color: #333;
                font-family: Arial, sans-serif;
                font-weight: bold;
              }
              .cert-info {
                text-align: center;
              }
              .cert-number {
                font-size: 8pt;
                color: #aaa;
                font-family: Arial, sans-serif;
                letter-spacing: 1px;
                margin-bottom: 1mm;
              }
              .cert-date {
                font-size: 9pt;
                color: #666;
                font-family: Arial, sans-serif;
              }
              .divider {
                width: 40mm;
                border: none;
                border-top: 1px solid #c9a227;
                margin: 2mm auto 4mm;
              }
            </style>
            </head>
            <body>
            <div class="certificate">
              <div class="outer-border"></div>

              <!-- CORNER DECORATIONS -->
              <div class="corner corner-tl">✦</div>
              <div class="corner corner-tr">✦</div>
              <div class="corner corner-bl">✦</div>
              <div class="corner corner-br">✦</div>

              <!-- HEADER -->
              <div class="logo">🎓</div>
              <div class="platform-name">ELearn Platform</div>

              <div class="cert-title">
                Certificate of Completion
              </div>
              <hr class="divider"/>

              <div class="subtitle">
                This is to proudly certify that
              </div>

              <!-- STUDENT NAME -->
              <div class="presented-to">
                THE FOLLOWING STUDENT
              </div>
              <div class="student-name">
                """ + studentName + """
              </div>

              <!-- COURSE INFO -->
              <div class="completion-text">
                has successfully completed the course
              </div>
              <div class="course-name">
                """ + courseTitle + """
              </div>

              <div class="score-badge">
                Final Score: """ + score + """
              </div>

              <!-- FOOTER -->
              <div class="footer">

                <!-- INSTRUCTOR SIGNATURE -->
                <div class="signature-block">
                  <div class="signature-name"
                       style="font-style:italic;
                              font-size:14pt;
                              margin-bottom:2mm">
                    """ + instructorName + """
                  </div>
                  <div class="signature-line"></div>
                  <div class="signature-label">
                    Course Instructor
                  </div>
                </div>

                <!-- CERT INFO CENTER -->
                <div class="cert-info">
                  <div class="cert-number">
                    Certificate No: """ + certNumber + """
                  </div>
                  <div class="cert-date">
                    Issued on """ + issueDate + """
                  </div>
                </div>

                <!-- PLATFORM SIGNATURE -->
                <div class="signature-block">
                  <div class="signature-name"
                       style="font-style:italic;
                              font-size:14pt;
                              margin-bottom:2mm">
                    ELearn Platform
                  </div>
                  <div class="signature-line"></div>
                  <div class="signature-label">
                    Platform Director
                  </div>
                </div>

              </div>
            </div>
            </body>
            </html>
            """;
    }
}