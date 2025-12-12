import { Component, Input, Output, EventEmitter, ElementRef, ViewChild, OnChanges, SimpleChanges, AfterViewInit } from '@angular/core';

@Component({
  selector: 'app-graph',
  templateUrl: './graph.component.html',
  styleUrls: ['./graph.component.scss']
})
export class GraphComponent implements OnChanges, AfterViewInit {
  @Input() r: number = 0;
  @Input() points: any[] = [];
  @Output() mapClick = new EventEmitter<{x: number, y: number}>();
  
  @ViewChild('canvas', {static: true}) canvasRef!: ElementRef<HTMLCanvasElement>;
  
  private ctx!: CanvasRenderingContext2D;
  
  private readonly SIZE = 500; 
  private readonly SCALE = 55; 

  ngAfterViewInit() {
    this.ctx = this.canvasRef.nativeElement.getContext('2d')!;
    this.draw();
  }

  ngOnChanges(changes: SimpleChanges) {
    this.draw();
  }

  handleClick(event: MouseEvent) {
    if (this.r <= 0) return;
    
    const rect = this.canvasRef.nativeElement.getBoundingClientRect();
    const x = (event.clientX - rect.left - this.SIZE / 2) / this.SCALE;
    const y = (this.SIZE / 2 - (event.clientY - rect.top)) / this.SCALE;
    
    this.mapClick.emit({x, y});
  }

  draw() {
    if (!this.ctx) return;
    const width = this.SIZE;
    const height = this.SIZE;
    const cx = width / 2;
    const cy = height / 2;

    this.ctx.clearRect(0, 0, width, height);

    if (this.r > 0) {
        this.ctx.fillStyle = 'rgba(78, 111, 230, 0.5)';
        const rPx = this.r * this.SCALE;
        const rHalfPx = (this.r / 2) * this.SCALE;

        this.ctx.beginPath();
        this.ctx.moveTo(cx, cy);
        this.ctx.arc(cx, cy, rPx, Math.PI, 1.5 * Math.PI, false);
        
        this.ctx.moveTo(cx, cy);
        this.ctx.rect(cx - rPx, cy, rPx, rHalfPx);

        this.ctx.moveTo(cx, cy);
        this.ctx.lineTo(cx, cy + rHalfPx);
        this.ctx.lineTo(cx + rHalfPx, cy);
        this.ctx.fill();
    }

    this.ctx.strokeStyle = 'white';
    this.ctx.lineWidth = 2;
    this.ctx.beginPath();
    this.ctx.moveTo(0, cy); this.ctx.lineTo(width, cy); 
    this.ctx.moveTo(cx, height); this.ctx.lineTo(cx, 0);
    this.ctx.stroke();

    this.ctx.beginPath();
    this.ctx.moveTo(width - 10, cy - 5); this.ctx.lineTo(width, cy); this.ctx.lineTo(width - 10, cy + 5);
    this.ctx.moveTo(cx - 5, 10); this.ctx.lineTo(cx, 0); this.ctx.lineTo(cx + 5, 10);
    this.ctx.stroke();

    if (this.r > 0) {
        this.ctx.fillStyle = 'white';
        this.ctx.font = '14px Courier New';
        this.ctx.textAlign = 'center';
        this.ctx.textBaseline = 'middle';

        const rPx = this.r * this.SCALE;
        const rHalfPx = (this.r / 2) * this.SCALE;

        this.drawTick(cx + rPx, cy, 'R');
        this.drawTick(cx + rHalfPx, cy, 'R/2');
        this.drawTick(cx - rHalfPx, cy, '-R/2');
        this.drawTick(cx - rPx, cy, '-R');

        this.drawTick(cx, cy - rPx, 'R');
        this.drawTick(cx, cy - rHalfPx, 'R/2');
        this.drawTick(cx, cy + rHalfPx, '-R/2');
        this.drawTick(cx, cy + rPx, '-R');
    }

    this.points.forEach(p => {
        const px = cx + p.x * this.SCALE;
        const py = cy - p.y * this.SCALE;

        this.ctx.beginPath();
        this.ctx.arc(px, py, 4, 0, 2 * Math.PI);
        this.ctx.fillStyle = p.hit ? '#00FF00' : '#FF0000';
        this.ctx.fill();
        this.ctx.strokeStyle = 'black';
        this.ctx.lineWidth = 1;
        this.ctx.stroke();
    });
  }

  private drawTick(x: number, y: number, label: string) {
      const TICK_SIZE = 5;
      this.ctx.beginPath();
      if (Math.abs(y - this.SIZE/2) < 1) {
          this.ctx.moveTo(x, y - TICK_SIZE);
          this.ctx.lineTo(x, y + TICK_SIZE);
          this.ctx.fillText(label, x, y + 15);
      } else {
          this.ctx.moveTo(x - TICK_SIZE, y);
          this.ctx.lineTo(x + TICK_SIZE, y);
          this.ctx.textAlign = 'left';
          this.ctx.fillText(label, x + 10, y);
          this.ctx.textAlign = 'center'; 
      }
      this.ctx.stroke();
  }
}