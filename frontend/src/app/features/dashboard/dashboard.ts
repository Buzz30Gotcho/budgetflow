import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { Chart, registerables } from 'chart.js';
import { DashboardService } from '../../core/dashboard.service';
import { DashboardSummary } from '../../core/models';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  imports: [DecimalPipe],
  templateUrl: './dashboard.html',
})
export class Dashboard implements AfterViewInit, OnDestroy {
  private dashboardService = inject(DashboardService);

  @ViewChild('pie') pieRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('trend') trendRef!: ElementRef<HTMLCanvasElement>;

  summary = signal<DashboardSummary | null>(null);

  private pieChart?: Chart;
  private trendChart?: Chart;

  ngAfterViewInit() {
    const now = new Date();
    this.dashboardService.summary(now.getFullYear(), now.getMonth() + 1).subscribe(data => {
      this.summary.set(data);
      this.renderPie(data);
      this.renderTrend(data);
    });
  }

  ngOnDestroy() {
    this.pieChart?.destroy();
    this.trendChart?.destroy();
  }

  private renderPie(data: DashboardSummary) {
    this.pieChart = new Chart(this.pieRef.nativeElement, {
      type: 'doughnut',
      data: {
        labels: data.expenseByCategory.map(c => c.name),
        datasets: [{
          data: data.expenseByCategory.map(c => c.total),
          backgroundColor: data.expenseByCategory.map(c => c.color),
        }],
      },
      options: { plugins: { legend: { position: 'bottom' } } },
    });
  }

  private renderTrend(data: DashboardSummary) {
    this.trendChart = new Chart(this.trendRef.nativeElement, {
      type: 'bar',
      data: {
        labels: data.monthlyTrend.map(m => m.month),
        datasets: [
          { label: 'Revenus', data: data.monthlyTrend.map(m => m.income), backgroundColor: '#22C55E' },
          { label: 'Dépenses', data: data.monthlyTrend.map(m => m.expense), backgroundColor: '#EF4444' },
        ],
      },
      options: { plugins: { legend: { position: 'bottom' } }, scales: { y: { beginAtZero: true } } },
    });
  }
}
