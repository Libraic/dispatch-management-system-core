package io.kovin.dispatch.management.system.model.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDate;
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
@Table(name = "t_companies")
@Entity
public class CompanyEntity extends Auditable {

    @Id
    @SequenceGenerator(name = "company_sequence_generator", sequenceName = "t_companies_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_sequence_generator")
    private Long id;

    @Column
    private String uuid;

    @Column
    private String name;

    @Column
    private String mcNumber;

    @Column
    private String address;

    @Column
    private LocalDate serviceDate;

    @Column
    private LocalDate startDate;

    @Column
    private String timezone;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CompanyEntity that = (CompanyEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
