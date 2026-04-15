import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AdminService {

  private api = 'http://localhost:8080/api/admin';
  private http = inject(HttpClient);

  getStats(): Observable<any> {
    return this.http.get(`${this.api}/stats`);
  }

  getUsers(search?: string): Observable<any[]> {
    const url = search
      ? `${this.api}/users?search=${search}`
      : `${this.api}/users`;
    return this.http.get<any[]>(url);
  }

  getPendingInstructors(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/users/pending-instructors`);
  }

  approveInstructor(id: number): Observable<any> {
    return this.http.put(`${this.api}/users/${id}/approve-instructor`, {});
  }

  rejectInstructor(id: number): Observable<any> {
    return this.http.put(`${this.api}/users/${id}/reject-instructor`, {});
  }

  toggleUserStatus(id: number): Observable<any> {
    return this.http.put(`${this.api}/users/${id}/toggle-status`, {});
  }

  deleteUser(id: number): Observable<any> {
    return this.http.delete(`${this.api}/users/${id}`);
  }

  getCourses(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/courses`);
  }

  approveCourse(id: number): Observable<any> {
    return this.http.put(`${this.api}/courses/${id}/approve`, {});
  }

  rejectCourse(id: number): Observable<any> {
    return this.http.put(`${this.api}/courses/${id}/reject`, {});
  }

  deleteCourse(id: number): Observable<any> {
    return this.http.delete(`${this.api}/courses/${id}`);
  }
}