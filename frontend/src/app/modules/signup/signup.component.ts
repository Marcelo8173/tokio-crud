import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { ErrorComponent } from '../../components/error/error.component';

@Component({
  imports: [CommonModule, ReactiveFormsModule, ErrorComponent],
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {
  signupForm!: FormGroup;
  submitted = false;
  loading = false;
  error = '';

  constructor(private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService,
  ) { }

  ngOnInit(): void {
    this.signupForm = this.formBuilder.group({
      nome: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      senha: ['', Validators.required]
    });
  }

  get f() {
    return this.signupForm.controls;
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.signupForm.invalid) {
      return;
    }

    this.loading = true;
    this.authService.register(this.signupForm.value).subscribe(
      {
        next: (response) => {
          this.router.navigate(['/']);
        },
        error: (error) => {
          this.error = "Erro ao cadastar um usúario";
          this.loading = false;
        }
      }
    );
  }
  onBackToLogin(): void {
    this.router.navigate(['/']);
  }
}