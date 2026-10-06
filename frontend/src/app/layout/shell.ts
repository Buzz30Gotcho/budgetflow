import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './shell.html',
})
export class Shell {
  private auth = inject(AuthService);
  private router = inject(Router);

  firstName = this.auth.firstName;

  logout() {
    this.auth.logout();
    this.router.navigateByUrl('/login');
  }
}
