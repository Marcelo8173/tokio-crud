import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ErrorComponent } from '../../components/error/error.component';

@Component({
  imports: [ReactiveFormsModule, CommonModule, ErrorComponent],
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  submitted = false;
  errorMessage = '';
  loading = false;
  error = '';

  constructor(private fb: FormBuilder, private router: Router, private authService: AuthService,
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      senha: ['', Validators.required]
    });
  }

  onSubmit() {
    this.submitted = true;

    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.authService.login(this.loginForm.value).subscribe({
      next: (response) => {
        this.authService.saveToken(response.token);
        this.router.navigate(['/home']); 
      },
      error: (err) => {
        this.errorMessage =
          err.error?.message || 'Erro ao fazer login. Verifique as credenciais.';
      }
    });
  }

  get f() {
    return this.loginForm.controls;
  }

  goToSignup(): void {
    this.router.navigate(['/signup']);
  }
}
