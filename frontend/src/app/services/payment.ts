import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class PaymentService {

  private api  = 'http://localhost:8080/api/payment';
  private http = inject(HttpClient);

  createIntent(courseId: number): Observable<any> {
    return this.http.post(`${this.api}/create-intent`,
      { courseId });
  }

  confirmPayment(paymentIntentId: string): Observable<any> {
    return this.http.post(
      `${this.api}/confirm?paymentIntentId=${paymentIntentId}`,
      {}
    );
  }

  getMyTransactions(): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.api}/my-transactions`);
  }
}