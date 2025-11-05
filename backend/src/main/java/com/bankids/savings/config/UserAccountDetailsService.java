package com.bankids.savings.config;

import com.bankids.savings.shared.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAccountDetailsService implements UserDetailsService {

    private final UserAccountRepository repository;

    public UserAccountDetailsService(UserAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByEmail(username)
                .map(account -> User.withUsername(account.getGuardianEmail())
                        .password(account.getPasswordHash())
                        .authorities("ROLE_GUARDIAN")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("No existe usuario con email " + username));
    }
}
