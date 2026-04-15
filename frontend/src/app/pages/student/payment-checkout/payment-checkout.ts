import { Component, inject, OnInit,
         AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router,
         RouterLink } from '@angular/router';
import { loadStripe, Stripe,
         StripeElements } from '@stripe/stripe-js';
import { PaymentService } from '../../../services/payment';
import { StudentService } from '../../../services/student';

@Component({
  selector: 'app-payment-checkout',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './payment-checkout.html',
  styleUrl: './payment-checkout.css'
})
export class PaymentCheckoutComponent implements OnInit {

  private route          = inject(ActivatedRoute);
  private router         = inject(Router);
  private paymentService = inject(PaymentService);
  private studentService = inject(StudentService);

  courseId!: number;
  course: any      = null;
  intentData: any  = null;
  isLoading        = true;
  isPaying         = false;
  isStripeReady    = false; // ✅ track when Stripe is ready
  errorMessage     = '';
  successMessage   = '';

  private stripe!: Stripe | null;
  private elements!: StripeElements;

  ngOnInit(): void {
    this.courseId = Number(
      this.route.snapshot.paramMap.get('courseId'));
    this.loadCourseAndCreateIntent();
  }

  async loadCourseAndCreateIntent(): Promise<void> {
    this.studentService
      .getCourseDetail(this.courseId)
      .subscribe({
        next: async (course) => {
          this.course = course;

          // Free course — enroll directly, skip payment
          if (!course.price || course.price === 0) {
            this.studentService
              .enroll(this.courseId)
              .subscribe({
                next: () => this.router.navigate(
                  ['/courses', this.courseId, 'learn']),
                error: (err) => {
                  this.errorMessage =
                    err.error?.message ||
                    'Enrollment failed';
                  this.isLoading = false;
                }
              });
            return;
          }

          // Paid course — create payment intent
          this.paymentService
            .createIntent(this.courseId)
            .subscribe({
              next: async (data) => {
                this.intentData = data;
                this.isLoading  = false;

                // Wait for DOM to render
                // before mounting Stripe
                setTimeout(async () => {
                  await this.mountStripe(data);
                }, 300);
              },
              error: (err) => {
                this.isLoading    = false;
                this.errorMessage =
                  err.error?.message ||
                  'Could not start payment. ' +
                  'Please try again.';
              }
            });
        },
        error: () => {
          this.isLoading    = false;
          this.errorMessage =
            'Could not load course details.';
        }
      });
  }

  async mountStripe(data: any): Promise<void> {
    try {
      // Load Stripe
      this.stripe = await loadStripe(
        data.publishableKey);

      if (!this.stripe) {
        this.errorMessage =
          'Could not load Stripe. ' +
          'Check your internet connection.';
        return;
      }

      // Create Elements instance with client secret
      this.elements = this.stripe.elements({
        clientSecret: data.clientSecret,
        appearance: {
          theme: 'stripe',
          variables: {
            colorPrimary:      '#667eea',
            colorBackground:   '#ffffff',
            colorText:         '#1e1e2e',
            colorDanger:       '#E74C3C',
            fontFamily:        'Roboto, sans-serif',
            spacingUnit:       '4px',
            borderRadius:      '8px',
          }
        }
      });

      // Create the payment element
      const paymentElement =
        this.elements.create('payment', {
          layout: 'tabs'
        });

      // ✅ Wait for ready event BEFORE allowing payment
      paymentElement.on('ready', () => {
        console.log('✅ Stripe element is ready');
        this.isStripeReady = true;
      });

      // Listen for errors from Stripe element
      paymentElement.on('change', (event: any) => {
        if (event.error) {
          this.errorMessage = event.error.message;
        } else {
          this.errorMessage = '';
        }
      });

      // Mount to the DOM element
      const mountPoint =
        document.getElementById('payment-element');

      if (!mountPoint) {
        console.error(
          'Mount point #payment-element not found!');
        this.errorMessage =
          'Payment form failed to load. ' +
          'Please refresh the page.';
        return;
      }

      paymentElement.mount('#payment-element');
      console.log('Stripe element mounted');

    } catch (err: any) {
      console.error('Stripe mount error:', err);
      this.errorMessage =
        'Payment system error. ' +
        'Please refresh and try again.';
    }
  }

  async pay(): Promise<void> {
    // Safety checks
    if (!this.stripe) {
      this.errorMessage =
        'Payment system not loaded. Please refresh.';
      return;
    }

    if (!this.elements) {
      this.errorMessage =
        'Payment form not ready. Please wait.';
      return;
    }

    if (!this.isStripeReady) {
      this.errorMessage =
        'Payment form is still loading. ' +
        'Please wait a moment.';
      return;
    }

    this.isPaying     = true;
    this.errorMessage = '';

    try {
      // ✅ Submit the elements first
      const { error: submitError } =
        await this.elements.submit();

      if (submitError) {
        this.isPaying     = false;
        this.errorMessage =
          submitError.message ||
          'Please check your card details.';
        return;
      }

      // ✅ Then confirm the payment
      const { error, paymentIntent } =
        await this.stripe.confirmPayment({
          elements:    this.elements,
          clientSecret: this.intentData.clientSecret,
          confirmParams: {
            // No redirect — stay on page
            return_url: window.location.href
          },
          redirect: 'if_required'
        });

      if (error) {
        this.isPaying     = false;
        this.errorMessage =
          error.message ||
          'Payment failed. Please try again.';
        return;
      }

      if (paymentIntent?.status === 'succeeded') {
        // ✅ Tell backend to confirm and enroll
        this.paymentService
          .confirmPayment(paymentIntent.id)
          .subscribe({
            next: (res) => {
              this.isPaying       = false;
              this.successMessage = res.message;
              // Redirect to course after 2 seconds
              setTimeout(() => {
                this.router.navigate([
                  '/courses',
                  this.courseId,
                  'learn'
                ]);
              }, 2000);
            },
            error: (err) => {
              this.isPaying     = false;
              this.errorMessage =
                err.error?.message ||
                'Payment succeeded but enrollment ' +
                'failed. Contact support.';
            }
          });
      } else {
        this.isPaying     = false;
        this.errorMessage =
          'Payment was not completed. Please try again.';
      }

    } catch (err: any) {
      console.error('Payment error:', err);
      this.isPaying     = false;
      this.errorMessage =
        'An unexpected error occurred. ' +
        'Please refresh and try again.';
    }
  }
}