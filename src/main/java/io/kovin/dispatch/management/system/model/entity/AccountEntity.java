package io.kovin.dispatch.management.system.model.entity;

import io.kovin.dispatch.management.system.model.entity.enums.EntityType;
import io.kovin.dispatch.management.system.model.entity.enums.SystemRole;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_accounts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AccountEntity {

    @Id
    @SequenceGenerator(name = "account_sequence_generator", sequenceName = "t_accounts_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_sequence_generator")
    private Long id;

    @Column
    private String uuid;

    @Column
    private String username;

    @Column
    private String hashedPassword;

    @Column
    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    @Column
    @Enumerated(EnumType.STRING)
    private SystemRole role;

    @Column
    private Boolean isActive;

    @Column
    private Long entityId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AccountEntity that = (AccountEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
