package io.kovin.dispatch.management.system.facade;

import java.util.UUID;
import io.kovin.dispatch.management.system.model.entity.AccountEntity;
import io.kovin.dispatch.management.system.model.entity.enums.EntityType;
import io.kovin.dispatch.management.system.model.entity.enums.SystemRole;
import io.kovin.dispatch.management.system.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountFacade {

    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

    public void createAccount(
        String username,
        String password,
        SystemRole role,
        EntityType entityType,
        Long entityId
    ) {
        log.info("Creating account for a [{}] entity.", entityType);
        AccountEntity accountEntity = AccountEntity.builder()
            .uuid(UUID.randomUUID().toString())
            .username(username)
            .hashedPassword(passwordEncoder.encode(password))
            .role(role)
            .entityType(entityType)
            .entityId(entityId)
            .isActive(true)
            .build();
        accountService.saveAccount(accountEntity);
    }
}
