import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink],
  templateUrl: './register.html',
})
export class Register {
  private auth = inject(AuthService);
  private router = inject(Router);

  firstName = '';
  email = '';
  password = '';
  error = signal<string | null>(null);
  loading = signal(false);

  submit() {
    this.loading.set(true);
    this.error.set(null);
    this.auth.register(this.firstName, this.email, this.password).subscribe({
      next: () => this.router.navigateByUrl('/dashboard'),
      error: (e) => {
        this.error.set(e?.error?.message ?? 'Inscription impossible');
        this.loading.set(false);
      },
    });
  }
}
