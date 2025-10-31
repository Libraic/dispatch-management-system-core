package io.kovin.dispatch.management.system.service;

import java.util.Optional;
import io.kovin.dispatch.management.system.model.entity.AccountEntity;
import io.kovin.dispatch.management.system.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public void saveAccount(AccountEntity accountEntity) {
        log.trace("Saving account with UUID=[{}].", accountEntity.getUuid());
        accountRepository.save(accountEntity);
    }

    public Optional<AccountEntity> findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }
}
