import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-auth-form',
  templateUrl: './auth-form.component.html',
  styleUrls: ['./auth-form.component.scss']
})
export class AuthFormComponent {
  mode: 'login' | 'register' = 'login';
  message = '';
  loading = false;

  form = this.fb.group({
    childName: ['', Validators.required],
    age: [10, [Validators.required, Validators.min(7), Validators.max(14)]],
    guardianName: ['', Validators.required],
    guardianEmail: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {
    this.applyValidators();
  }

  get ageControl() {
    return this.form.get('age');
  }

  switchMode(): void {
    this.mode = this.mode === 'login' ? 'register' : 'login';
    this.message = '';
    this.applyValidators();
  }

  submit(): void {
    if (this.mode === 'register' && this.form.invalid) {
      this.message = this.ageControl?.errors
        ? 'La edad debe estar entre 7 y 14 años para crear una cuenta Tuticuenta.'
        : 'Por favor revisa los datos.';
      this.form.markAllAsTouched();
      return;
    }

    if (this.mode === 'login' && this.form.get('guardianEmail')?.invalid) {
      this.message = 'Ingresa un correo válido y tu contraseña.';
      return;
    }

    this.loading = true;
    const value = this.form.value;

    if (this.mode === 'login') {
      this.auth.login({
        email: value.guardianEmail!,
        password: value.password!
      }).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/dashboard']);
        },
        error: err => {
          this.loading = false;
          this.message = err.error || 'No fue posible iniciar sesión.';
        }
      });
    } else {
      this.auth.register({
        childName: value.childName!,
        age: value.age!,
        guardianName: value.guardianName!,
        guardianEmail: value.guardianEmail!,
        password: value.password!
      }).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/dashboard']);
        },
        error: err => {
          this.loading = false;
          this.message = err.error || 'No fue posible registrarse.';
        }
      });
    }
  }

  private applyValidators(): void {
    if (this.mode === 'login') {
      this.form.get('childName')?.clearValidators();
      this.form.get('age')?.clearValidators();
      this.form.get('guardianName')?.clearValidators();
    } else {
      this.form.get('childName')?.setValidators([Validators.required]);
      this.form.get('age')?.setValidators([Validators.required, Validators.min(7), Validators.max(14)]);
      this.form.get('guardianName')?.setValidators([Validators.required]);
    }
    this.form.get('childName')?.updateValueAndValidity({ emitEvent: false });
    this.form.get('age')?.updateValueAndValidity({ emitEvent: false });
    this.form.get('guardianName')?.updateValueAndValidity({ emitEvent: false });
  }
}
