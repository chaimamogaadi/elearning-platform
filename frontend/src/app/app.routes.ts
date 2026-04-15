import { Routes } from '@angular/router';
import { adminGuard }      from './guards/admin.guard';
import { instructorGuard } from './guards/instructor.guard';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [

  // ===== PUBLIC ROUTES =====
  {
    path: '',
    loadComponent: () =>
      import('./pages/home/home')
        .then(m => m.HomeComponent)
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login')
        .then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./pages/register/register')
        .then(m => m.RegisterComponent)
  },
  {
    path: 'forgot-password',
    loadComponent: () =>
      import('./pages/forgot-password/forgot-password')
        .then(m => m.ForgotPasswordComponent)
  },
  {
    path: 'reset-password',
    loadComponent: () =>
      import('./pages/reset-password/reset-password')
        .then(m => m.ResetPasswordComponent)
  },

  // ===== COURSE CATALOG (public) =====
  {
    path: 'courses',
    loadComponent: () =>
      import('./pages/student/course-catalog/course-catalog')
        .then(m => m.CourseCatalogComponent)
  },
  {
    path: 'courses/:id',
    loadComponent: () =>
      import('./pages/student/course-detail/course-detail')
        .then(m => m.CourseDetailComponent)
  },
  {
    path: 'courses/:id/learn',
    loadComponent: () =>
      import('./pages/student/lesson-viewer/lesson-viewer')
        .then(m => m.LessonViewerComponent),
    canActivate: [authGuard]
  },
  {
  path: 'checkout/:courseId',
  loadComponent: () =>
    import('./pages/student/payment-checkout/payment-checkout')
      .then(m => m.PaymentCheckoutComponent),
  canActivate: [authGuard]
},

{
  path: 'courses/:id/quiz/:quizId',
  loadComponent: () =>
    import('./pages/student/quiz-player/quiz-player')
      .then(m => m.QuizPlayerComponent),
  canActivate: [authGuard]
},

  // ===== STUDENT ROUTES =====
  {
    path: 'student/dashboard',
    loadComponent: () =>
      import('./pages/student/student-dashboard/student-dashboard')
        .then(m => m.StudentDashboardComponent),
    canActivate: [authGuard]
  },
  {
    path: 'student/my-courses',
    loadComponent: () =>
      import('./pages/student/my-courses/my-courses')
        .then(m => m.MyCoursesComponent),
    canActivate: [authGuard]
  },

  // ===== ADMIN ROUTES =====
  {
    path: 'admin',
    loadComponent: () =>
      import('./pages/admin/admin-layout/admin-layout')
        .then(m => m.AdminLayoutComponent),
    canActivate: [adminGuard],
    children: [
      {
        path: '',
        redirectTo: 'overview',
        pathMatch: 'full'
      },
      {
        path: 'overview',
        loadComponent: () =>
          import('./pages/admin/admin-overview/admin-overview')
            .then(m => m.AdminOverviewComponent)
      },
      {
        path: 'users',
        loadComponent: () =>
          import('./pages/admin/admin-users/admin-users')
            .then(m => m.AdminUsersComponent)
      },
      {
        path: 'courses',
        loadComponent: () =>
          import('./pages/admin/admin-courses/admin-courses')
            .then(m => m.AdminCoursesComponent)
      },
    ]
  },

  // ===== INSTRUCTOR ROUTES =====
  {
    path: 'instructor',
    loadComponent: () =>
      import('./pages/instructor/instructor-layout/instructor-layout')
        .then(m => m.InstructorLayoutComponent),
    canActivate: [instructorGuard],
    children: [
      {
        path: '',
        redirectTo: 'overview',
        pathMatch: 'full'
      },
      {
        path: 'overview',
        loadComponent: () =>
          import('./pages/instructor/instructor-overview/instructor-overview')
            .then(m => m.InstructorOverviewComponent)
      },
      {
        path: 'courses',
        loadComponent: () =>
          import('./pages/instructor/instructor-courses/instructor-courses')
            .then(m => m.InstructorCoursesComponent)
      },
      {
        path: 'courses/:id/lessons',
        loadComponent: () =>
          import('./pages/instructor/lesson-manager/lesson-manager')
            .then(m => m.LessonManagerComponent)
      },
      {
        path: 'courses/:id/quiz',
        loadComponent: () =>
          import('./pages/instructor/quiz-builder/quiz-builder')
            .then(m => m.QuizBuilderComponent)
      },
    ]
  },
{
  path: 'certificate/:courseId',
  loadComponent: () =>
    import('./pages/student/certificate/certificate')
      .then(m => m.CertificateComponent),
  canActivate: [authGuard]
},
{
  path: 'my-certificates',
  loadComponent: () =>
    import('./pages/student/my-certificates/my-certificates')
      .then(m => m.MyCertificatesComponent),
  canActivate: [authGuard]
},
{
  path: 'verify/:certNumber',
  loadComponent: () =>
    import('./pages/student/certificate/certificate')
      .then(m => m.CertificateComponent)
},
  // ===== REDIRECTS =====
  {
    path: 'dashboard',
    redirectTo: 'student/dashboard',
    pathMatch: 'full'
  },

  // ===== 404 — MUST BE LAST =====
  {
    path: '**',
    redirectTo: ''
  }
];