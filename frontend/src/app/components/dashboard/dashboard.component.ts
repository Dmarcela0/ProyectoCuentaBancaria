import { Component, OnInit } from '@angular/core';
import { AccountService } from '../../services/account.service';
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

  constructor(private accountService: AccountService) {}

  ngOnInit(): void {
    this.loading = true;
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
