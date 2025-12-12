import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss']
})
export class MainComponent implements OnInit {
  point = { x: 0, y: 0, r: 1 };

  xOptions = [
    {label: '-5', value: -5}, {label: '-4', value: -4}, {label: '-3', value: -3},
    {label: '-2', value: -2}, {label: '-1', value: -1}, {label: '0', value: 0},
    {label: '1', value: 1}, {label: '2', value: 2}, {label: '3', value: 3}
  ];

  rOptions = [
    {label: '-5', value: -5}, {label: '-4', value: -4}, {label: '-3', value: -3},
    {label: '-2', value: -2}, {label: '-1', value: -1}, {label: '0', value: 0},
    {label: '1', value: 1}, {label: '2', value: 2}, {label: '3', value: 3}
  ];

  history: any[] = [];

  constructor(
    private api: ApiService,
    private auth: AuthService,
    private messageService: MessageService
  ) {}

  ngOnInit() {
    this.loadPoints();
  }

  loadPoints() {
    this.api.getPoints().subscribe({
      next: (data) => this.history = data,
      error: (err) => {
        if (err.status === 401) this.auth.logout();
      }
    });
  }

  submitPoint() {
    if (!this.isValid()) return;

    this.api.addPoint(this.point).subscribe({
      next: (res) => {
        this.history = [res, ...this.history];
        this.messageService.add({severity:'success', summary:'Выстрел', detail: res.hit ? 'Попадание!' : 'Промах'});
      },
      error: (err) => {
        if (err.status === 401 || err.status === 403) {
            this.auth.logout();
        } else {
            this.messageService.add({severity:'error', summary:'Ошибка', detail:'Сбой системы наведения'});
        }
      }
    });
  }

  onMapClick(coords: {x: number, y: number}) {
    if (this.point.r <= 0) {
      this.messageService.add({severity:'warn', summary:'Внимание', detail:'Выберите положительный радиус R'});
      return;
    }

    this.point.x = parseFloat(coords.x.toFixed(2));
    this.point.y = parseFloat(coords.y.toFixed(2));

    this.submitPoint();
  }

  isValid(): boolean {
    const yVal = parseFloat(this.point.y.toString());
    if (isNaN(yVal) || yVal < -3 || yVal > 3) {
      this.messageService.add({severity:'error', summary:'Ошибка', detail:'Y должен быть числом от -3 до 3'});
      return false;
    }
    if (this.point.r <= 0) {
      this.messageService.add({severity:'error', summary:'Ошибка', detail:'R должен быть положительным'});
      return false;
    }
    return true;
  }

  logout() {
    this.auth.logout();
  }
}