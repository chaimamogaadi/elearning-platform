import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { StudentService } from '../../../services/student';

@Component({
  selector: 'app-my-certificates',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './my-certificates.html',
  styleUrl: './my-certificates.css'
})
export class MyCertificatesComponent implements OnInit {

  private studentService = inject(StudentService);

  certificates: any[] = [];
  isLoading           = true;
  downloading: number | null = null;

  ngOnInit(): void {
    this.studentService.getMyCertificates().subscribe({
      next: (data) => {
        this.certificates = data;
        this.isLoading    = false;
      },
      error: () => { this.isLoading = false; }
    });
  }

  download(courseId: number, index: number): void {
    this.downloading = index;
    this.studentService
      .downloadCertificate(courseId)
      .subscribe({
        next: (blob) => {
          this.downloading = null;
          const url = window.URL.createObjectURL(blob);
          const a   = document.createElement('a');
          a.href    = url;
          a.download = 'certificate.pdf';
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
          window.URL.revokeObjectURL(url);
        },
        error: () => { this.downloading = null; }
      });
  }
}