import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenService } from '../services/token';

export const instructorGuard: CanActivateFn = () => {
  const tokenService = inject(TokenService);
  const router       = inject(Router);
  const user         = tokenService.getUser();

  if (user && (user.role === 'INSTRUCTOR' || user.role === 'ADMIN')) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};