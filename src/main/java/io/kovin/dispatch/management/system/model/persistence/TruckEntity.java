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
@Table(name = "t_trucks")
public class TruckEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trucks_sequence_generator")
    @SequenceGenerator(name = "trucks_sequence_generator", sequenceName = "t_trucks_sequence", allocationSize = 1)
    private Long id;

    @Column
    private UUID uuid;

    @Column
    private String truckNumber;

    @Column
    private String vinNumber;

    @Column
    private String model;

    @Column
    private Short truckYear;

    @Column
    private String truckMake;

    @Column
    private String fuelType;

    @Column
    private String color;

    @Column
    private Integer weight;

    @JoinColumn(name = "company_id")
    @ManyToOne
    private CompanyEntity company;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TruckEntity that = (TruckEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
