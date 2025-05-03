import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('auth_token');
  
  const isPrivateRoute =
  req.url.includes('/home') ||
  req.url.includes('/user') ||
  req.url.includes('/profile') ||
  req.url.includes('/address');
  

  if (token && isPrivateRoute) {
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(cloned);
  }

  return next(req);
};
