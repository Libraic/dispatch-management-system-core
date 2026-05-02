package io.kovin.dispatch.management.system.model.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "t_trailers")
public class TrailerEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trailers_sequence_generator")
    @SequenceGenerator(name = "trailers_sequence_generator", sequenceName = "t_trailers_sequence", allocationSize = 1)
    private Long id;

    @Column
    private UUID uuid;

    @Column
    private String trailerNumber;

    @Column
    private String vinNumber;

    @Column
    private Short trailerYear;

    @Column
    private String trailerMake;

    @Column
    private String equipmentType;

    @Column
    private Integer equipmentSize;

    @Column
    private Integer palletCapacity;

    @Column
    private Integer maxWeight;

    @Column
    private String tireSize;

    @JoinColumn(name = "company_id")
    @ManyToOne
    private CompanyEntity company;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TrailerEntity that = (TrailerEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
