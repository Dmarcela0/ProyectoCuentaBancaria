package com.bankids.savings.account;

import com.bankids.savings.shared.UserAccount;
import com.bankids.savings.shared.UserAccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final UserAccountRepository repository;

    public AccountController(UserAccountRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/summary")
    public ResponseEntity<AccountSummary> summary(@AuthenticationPrincipal UserDetails userDetails) {
        UserAccount account = repository.findByEmail(userDetails.getUsername()).orElseThrow();
        AccountSummary summary = new AccountSummary(
                account.getChildName(),
                account.getAge(),
                account.getBalance(),
                account.getTransactions(),
                account.getGoals()
        );
        return ResponseEntity.ok(summary);
    }
}
