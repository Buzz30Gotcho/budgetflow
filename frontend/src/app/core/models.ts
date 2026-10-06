export type TransactionType = 'INCOME' | 'EXPENSE';

export interface AuthResponse {
  token: string;
  userId: number;
  firstName: string;
  email: string;
}

export interface Category {
  id: number;
  name: string;
  color: string;
}

export interface Transaction {
  id: number;
  type: TransactionType;
  amount: number;
  description: string | null;
  date: string;
  categoryId: number | null;
  categoryName: string | null;
  categoryColor: string | null;
}

export interface Budget {
  id: number;
  categoryId: number;
  categoryName: string;
  categoryColor: string;
  amount: number;
  spent: number;
  remaining: number;
  percentage: number;
  year: number;
  month: number;
}

export interface ImportResult {
  imported: number;
  errors: string[];
}

export interface DashboardSummary {
  totalIncome: number;
  totalExpense: number;
  balance: number;
  expenseByCategory: { name: string; color: string; total: number }[];
  monthlyTrend: { month: string; income: number; expense: number }[];
}
