import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class InstructorService {

  private api  = 'http://localhost:8080/api/instructor';
  private http = inject(HttpClient);

  // Courses
  createCourse(data: any): Observable<any> {
    return this.http.post(`${this.api}/courses`, data);
  }
  getMyCourses(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/courses`);
  }
  updateCourse(id: number, data: any): Observable<any> {
    return this.http.put(`${this.api}/courses/${id}`, data);
  }
  deleteCourse(id: number): Observable<any> {
    return this.http.delete(`${this.api}/courses/${id}`);
  }

  // Lessons
  addLesson(courseId: number, data: any): Observable<any> {
    return this.http.post(`${this.api}/courses/${courseId}/lessons`, data);
  }
  getLessons(courseId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/courses/${courseId}/lessons`);
  }
  deleteLesson(lessonId: number): Observable<any> {
    return this.http.delete(`${this.api}/lessons/${lessonId}`);
  }

  // Quizzes
  createQuiz(courseId: number, data: any): Observable<any> {
    return this.http.post(`${this.api}/courses/${courseId}/quizzes`, data);
  }
  getQuizzes(courseId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/courses/${courseId}/quizzes`);
  }

  // Students
  getStudents(courseId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/courses/${courseId}/students`);
  }
}