import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class StudentService {

  private authApi    = 'http://localhost:8080/api/auth';
  private studentApi = 'http://localhost:8080/api/student';
  private http       = inject(HttpClient);

  // Auth
  getMe(): Observable<any> {
    return this.http.get(`${this.authApi}/me`);
  }

  requestInstructor(): Observable<any> {
    return this.http.post(
      `${this.authApi}/request-instructor`, {});
  }

  // Courses
  browseCourses(search?: string,
                category?: string): Observable<any[]> {
    let url = `${this.studentApi}/courses`;
    const params: string[] = [];
    if (search)   params.push(`search=${search}`);
    if (category) params.push(`category=${category}`);
    if (params.length) url += '?' + params.join('&');
    return this.http.get<any[]>(url);
  }

  getCourseDetail(id: number): Observable<any> {
    return this.http.get(`${this.studentApi}/courses/${id}`);
  }

  getCourseLessons(courseId: number): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.studentApi}/courses/${courseId}/lessons`
    );
  }
  enroll(courseId: number): Observable<any> {
    return this.http.post(
      `${this.studentApi}/courses/${courseId}/enroll`, {});
  }

  getMyCourses(): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.studentApi}/my-courses`);
  }

  isEnrolled(courseId: number): Observable<any> {
    return this.http.get(
      `${this.studentApi}/courses/${courseId}/is-enrolled`);
  }

  getCourseQuizzes(courseId: number): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.studentApi}/courses/${courseId}/quizzes`
    );
  }

  submitQuiz(quizId: number,
            answers: Record<number, string>): Observable<any> {
    return this.http.post(`${this.studentApi}/quiz/submit`, {
      quizId,
      answers
    });
  }
  downloadCertificate(courseId: number): Observable<Blob> {
  return this.http.get(
    `http://localhost:8080/api/certificate/course/${courseId}`,
    { responseType: 'blob' }  // ← important for PDF
  );
}

getMyCertificates(): Observable<any[]> {
  return this.http.get<any[]>(
    'http://localhost:8080/api/certificate/my-certificates');
}

verifyCertificate(certNumber: string): Observable<any> {
  return this.http.get(
    `http://localhost:8080/api/certificate/verify/${certNumber}`);
}
getHomeData(): Observable<any> {
  return this.http.get(
    'http://localhost:8080/api/home/data');
}
}