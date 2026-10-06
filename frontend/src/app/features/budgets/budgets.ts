import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Chart, registerables } from 'chart.js';
import { BudgetService } from '../../core/budget.service';
import { CategoryService } from '../../core/category.service';
import { Budget, Category } from '../../core/models';

Chart.register(...registerables);

@Component({
  selector: 'app-budgets',
  imports: [FormsModule, DecimalPipe],
  templateUrl: './budgets.html',
})
export class Budgets implements AfterViewInit, OnDestroy {
  private budgetService = inject(BudgetService);
  private categoryService = inject(CategoryService);

  @ViewChild('chart') chartRef!: ElementRef<HTMLCanvasElement>;

  private now = new Date();
  year = this.now.getFullYear();
  month = this.now.getMonth() + 1;

  budgets = signal<Budget[]>([]);
  categories = signal<Category[]>([]);
  error = signal<string | null>(null);
  highlightId = signal<number | null>(null);

  categoryId: number | null = null;
  amount: number | null = null;

  private chart?: Chart;

  ngAfterViewInit() {
    this.load();
    this.categoryService.list().subscribe(list => this.categories.set(list));
  }

  ngOnDestroy() {
    this.chart?.destroy();
  }

  load() {
    this.budgetService.list(this.year, this.month).subscribe(list => {
      this.budgets.set(list);
      this.renderChart(list);
    });
  }

  add() {
    if (!this.categoryId || !this.amount) return;
    this.error.set(null);
    this.budgetService.create({
      categoryId: Number(this.categoryId),
      amount: this.amount,
      year: this.year,
      month: this.month,
    }).subscribe({
      next: (created) => {
        this.amount = null;
        this.categoryId = null;
        this.highlight(created.id);
        this.load();
      },
      error: (e) => this.error.set(e?.error?.message ?? 'Erreur'),
    });
  }

  remove(id: number) {
    this.budgetService.delete(id).subscribe(() => this.load());
  }

  barColor(percentage: number): string {
    if (percentage >= 100) return 'bg-red-500';
    if (percentage >= 80) return 'bg-amber-500';
    return 'bg-green-500';
  }

  statusLabel(percentage: number): string {
    if (percentage >= 100) return 'Dépassé';
    if (percentage >= 80) return 'Bientôt dépassé';
    return 'Dans le budget';
  }

  statusClass(percentage: number): string {
    if (percentage >= 100) return 'bg-red-100 text-red-700';
    if (percentage >= 80) return 'bg-amber-100 text-amber-700';
    return 'bg-green-100 text-green-700';
  }

  private highlight(id: number) {
    this.highlightId.set(id);
    setTimeout(() => this.highlightId.set(null), 2500);
  }

  private renderChart(budgets: Budget[]) {
    this.chart?.destroy();
    if (budgets.length === 0) return;
    this.chart = new Chart(this.chartRef.nativeElement, {
      type: 'bar',
      data: {
        labels: budgets.map(b => b.categoryName),
        datasets: [
          { label: 'Budget', data: budgets.map(b => b.amount), backgroundColor: '#C7D2FE' },
          { label: 'Dépensé', data: budgets.map(b => b.spent), backgroundColor: '#6366F1' },
        ],
      },
      options: {
        indexAxis: 'y',
        plugins: { legend: { position: 'bottom' } },
        scales: { x: { beginAtZero: true } },
      },
    });
  }
}
