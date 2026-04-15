import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenService } from '../services/token';

// Angular 18+ guards are just functions!
export const authGuard: CanActivateFn = () => {
  const tokenService = inject(TokenService);
  const router       = inject(Router);

  if (tokenService.isLoggedIn()) {
    return true;  // ✅ allow access
  }

  // ❌ not logged in → redirect to login
  router.navigate(['/login']);
  return false;
};