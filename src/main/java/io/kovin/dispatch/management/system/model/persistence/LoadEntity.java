package io.kovin.dispatch.management.system.model.persistence;

import io.kovin.dispatch.management.system.model.persistence.enums.LoadStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "t_loads")
public class LoadEntity extends PlannableEntity {

    @Id
    @SequenceGenerator(name = "loads_sequence_generator", sequenceName = "t_loads_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loads_sequence_generator")
    private Long id;

    @Column
    private BigDecimal revenue;

    @Column
    private BigDecimal miles;

    @Column
    private String broker;

    @Column
    private String representative;

    @Column
    private String representativeContactNumber;

    @Column
    @Enumerated(EnumType.STRING)
    private LoadStatus loadStatus;

    @OneToMany(mappedBy = "load", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoadLocationEntity> locations;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LoadEntity that = (LoadEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
