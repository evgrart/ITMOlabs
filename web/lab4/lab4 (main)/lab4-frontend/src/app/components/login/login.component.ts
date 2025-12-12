import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  username = '';
  password = '';

  constructor(
    private auth: AuthService,
    private router: Router,
    private messageService: MessageService
  ) {
    if (this.auth.isLoggedIn()) {
      this.router.navigate(['/main']);
    }
  }

  onLogin() {
    if (!this.isValid()) return;

    this.auth.login({ username: this.username, password: this.password }).subscribe({
      next: () => {
        this.router.navigate(['/main']);
      },
      error: () => {
        this.showError('Ошибка входа', 'Неверное имя пользователя или пароль (Ересь!)');
      }
    });
  }

  onRegister() {
    if (!this.isValid()) return;

    this.auth.register({ username: this.username, password: this.password }).subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Успех', detail: 'Боец зарегистрирован. Теперь войдите.' });
      },
      error: () => {
        this.showError('Ошибка регистрации', 'Пользователь уже существует или данные некорректны');
      }
    });
  }

  isValid(): boolean {
    if (!this.username || !this.password) {
      this.showError('Ошибка', 'Введите имя и пароль');
      return false;
    }
    return true;
  }

  showError(summary: string, detail: string) {
    this.messageService.add({ severity: 'error', summary, detail });
  }
}