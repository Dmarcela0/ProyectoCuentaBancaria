export interface SavingsTransaction {
  description: string;
  amount: number;
  date: string;
}

export interface SavingsGoal {
  name: string;
  targetAmount: number;
  targetDate: string;
}

export interface AccountSummary {
  childName: string;
  age: number;
  currency: string;
  balance: number;
  transactions: SavingsTransaction[];
  goals: SavingsGoal[];
}
