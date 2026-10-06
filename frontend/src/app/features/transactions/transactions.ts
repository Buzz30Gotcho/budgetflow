import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TransactionService } from '../../core/transaction.service';
import { CategoryService } from '../../core/category.service';
import { Category, Transaction, TransactionType } from '../../core/models';

@Component({
  selector: 'app-transactions',
  imports: [FormsModule, DecimalPipe],
  templateUrl: './transactions.html',
})
export class Transactions implements OnInit {
  private transactionService = inject(TransactionService);
  private categoryService = inject(CategoryService);

  transactions = signal<Transaction[]>([]);
  categories = signal<Category[]>([]);
  error = signal<string | null>(null);

  private incomes = computed(() => this.transactions().filter(t => t.type === 'INCOME'));
  private expenses = computed(() => this.transactions().filter(t => t.type === 'EXPENSE'));

  totalIncome = computed(() => this.incomes().reduce((sum, t) => sum + t.amount, 0));
  totalExpense = computed(() => this.expenses().reduce((sum, t) => sum + t.amount, 0));
  balance = computed(() => this.totalIncome() - this.totalExpense());

  incomeCount = computed(() => this.incomes().length);
  expenseCount = computed(() => this.expenses().length);

  type: TransactionType = 'EXPENSE';
  amount: number | null = null;
  description = '';
  date = new Date().toISOString().substring(0, 10);
  categoryId: number | null = null;

  ngOnInit() {
    this.loadTransactions();
    this.categoryService.list().subscribe(list => this.categories.set(list));
  }

  loadTransactions() {
    this.transactionService.list().subscribe(list => this.transactions.set(list));
  }

  add() {
    if (!this.amount) return;
    this.error.set(null);
    this.transactionService.create({
      type: this.type,
      amount: this.amount,
      description: this.description || null,
      date: this.date,
      categoryId: this.categoryId ? Number(this.categoryId) : null,
    }).subscribe({
      next: () => { this.amount = null; this.description = ''; this.loadTransactions(); },
      error: (e) => this.error.set(e?.error?.message ?? 'Erreur'),
    });
  }

  remove(id: number) {
    this.transactionService.delete(id).subscribe(() => this.loadTransactions());
  }
}
