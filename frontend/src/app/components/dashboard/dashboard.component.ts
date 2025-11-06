import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { AccountService, GoalPayload, TransactionPayload } from '../../services/account.service';
import { AccountSummary } from '../../models/account-summary.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  summary?: AccountSummary;
  loading = false;
  error = '';
  transactionError = '';
  goalError = '';
  transactionSaving = false;
  goalSaving = false;

  transactionForm = this.fb.group({
    description: ['', [Validators.required, Validators.maxLength(80)]],
    amount: [null, [Validators.required, Validators.min(1)]],
    type: ['deposit', Validators.required]
  });

  goalForm = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(60)]],
    targetAmount: [null, [Validators.required, Validators.min(1)]],
    targetDate: ['', Validators.required]
  });

  constructor(private accountService: AccountService, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.loadSummary();
  }

  submitTransaction(): void {
    this.transactionError = '';
    if (this.transactionForm.invalid) {
      this.transactionForm.markAllAsTouched();
      return;
    }
    this.transactionSaving = true;
    const { description, amount, type } = this.transactionForm.value;
    const payload: TransactionPayload = {
      description: (description ?? '').trim(),
      amount: Number(amount),
      type: (type ?? 'deposit') as TransactionPayload['type']
    };
    this.accountService.addTransaction(payload).subscribe({
      next: summary => {
        this.summary = summary;
        this.transactionSaving = false;
        this.transactionForm.reset({ description: '', amount: null, type: 'deposit' });
      },
      error: () => {
        this.transactionError = 'No pudimos registrar el movimiento. Intenta de nuevo.';
        this.transactionSaving = false;
      }
    });
  }

  submitGoal(): void {
    this.goalError = '';
    if (this.goalForm.invalid) {
      this.goalForm.markAllAsTouched();
      return;
    }
    this.goalSaving = true;
    const { name, targetAmount, targetDate } = this.goalForm.value;
    const payload: GoalPayload = {
      name: (name ?? '').trim(),
      targetAmount: Number(targetAmount),
      targetDate: targetDate ?? ''
    };
    this.accountService.addGoal(payload).subscribe({
      next: summary => {
        this.summary = summary;
        this.goalSaving = false;
        this.goalForm.reset({ name: '', targetAmount: null, targetDate: '' });
      },
      error: () => {
        this.goalError = 'No pudimos guardar la meta. Intenta de nuevo.';
        this.goalSaving = false;
      }
    });
  }

  private loadSummary(): void {
    this.loading = true;
    this.error = '';
    this.accountService.getSummary().subscribe({
      next: summary => {
        this.summary = summary;
        this.loading = false;
      },
      error: () => {
        this.error = 'No fue posible obtener tu información. Intenta más tarde.';
        this.loading = false;
      }
    });
  }
}
