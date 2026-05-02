package io.kovin.dispatch.management.system.model.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "t_vehicle_maintenance_records")
public class VehicleMaintenanceRecordEntity extends SchedulableEntity {

    @Id
    @SequenceGenerator(name = "t_vehicle_maintenance_records_sequence_generator", sequenceName = "t_vehicle_maintenance_records_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "t_vehicle_maintenance_records_sequence_generator")
    private Long id;

    @Column
    private String location;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VehicleMaintenanceRecordEntity that = (VehicleMaintenanceRecordEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
