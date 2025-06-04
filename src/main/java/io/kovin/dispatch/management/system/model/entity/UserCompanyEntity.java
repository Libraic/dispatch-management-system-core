package io.kovin.dispatch.management.system.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "t_usercompanies")
@Entity
public class UserCompanyEntity {

    @EmbeddedId
    private UserCompanyId userCompanyId;

    @Column
    private BigDecimal commission;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @MapsId("companyId")
    @JoinColumn(name = "company_id")
    private CompanyEntity company;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserCompanyEntity that = (UserCompanyEntity) o;
        return Objects.equals(userCompanyId, that.userCompanyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userCompanyId);
    }
}
