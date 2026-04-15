import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { StudentService } from '../../../services/student';

@Component({
  selector: 'app-certificate',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './certificate.html',
  styleUrl: './certificate.css'
})
export class CertificateComponent implements OnInit {

  private route          = inject(ActivatedRoute);
  private studentService = inject(StudentService);

  courseId: number | null     = null;
  certNumber: string | null   = null;
  isLoading                   = false;
  isDownloading               = false;
  successMessage              = '';
  errorMessage                = '';

  // For verify mode
  verifyMode                  = false;
  verifiedCert: any           = null;
  verifyError                 = '';

  ngOnInit(): void {
    // Check if we are in verify mode
    this.certNumber = this.route.snapshot
      .paramMap.get('certNumber');

    if (this.certNumber) {
      this.verifyMode = true;
      this.verifyCertificate();
      return;
    }

    // Normal download mode
    this.courseId = Number(
      this.route.snapshot.paramMap.get('courseId'));
  }

  downloadCertificate(): void {
    if (!this.courseId) return;

    this.isDownloading = true;
    this.errorMessage  = '';

    this.studentService
      .downloadCertificate(this.courseId)
      .subscribe({
        next: (blob) => {
          this.isDownloading = false;

          // Create download link and click it
          const url = window.URL.createObjectURL(blob);
          const a   = document.createElement('a');
          a.href    = url;
          a.download = 'certificate.pdf';
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
          window.URL.revokeObjectURL(url);

          this.successMessage =
            '✅ Certificate downloaded successfully!';
        },
        error: (err) => {
          this.isDownloading = false;
          this.errorMessage  =
            err.error?.message ||
            'Could not generate certificate. ' +
            'Make sure you passed the quiz first.';
        }
      });
  }

  verifyCertificate(): void {
    if (!this.certNumber) return;
    this.isLoading  = true;
    this.verifyError = '';

    this.studentService
      .verifyCertificate(this.certNumber)
      .subscribe({
        next: (cert) => {
          this.verifiedCert = cert;
          this.isLoading    = false;
        },
        error: () => {
          this.isLoading  = false;
          this.verifyError =
            'This certificate number is invalid ' +
            'or does not exist.';
        }
      });
  }
}