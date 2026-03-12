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
@Table(name = "t_driver_dispatcher_relations")
public class DriverDispatcherRelationEntity extends Auditable {

    @Id
    @SequenceGenerator(name = "driver_dispatcher_relations_sequence_generator", sequenceName = "t_driver_dispatcher_relations_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "driver_dispatcher_relations_sequence_generator")
    private Long id;

    @Column
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "dispatcher_id")
    private DispatcherEntity dispatcher;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private DriverEntity driver;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private CompanyEntity company;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DriverDispatcherRelationEntity that = (DriverDispatcherRelationEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
