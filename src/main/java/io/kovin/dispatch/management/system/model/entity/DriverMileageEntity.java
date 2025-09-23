package io.kovin.dispatch.management.system.model.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "t_drivers_mileage")
public class DriverMileageEntity extends Auditable {

    @Id
    @SequenceGenerator(name = "drivers_mileage_sequence_generator", sequenceName = "t_drivers_mileage_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "drivers_mileage_sequence_generator")
    private Long id;

    @Column
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "dispatcher_id")
    private UserEntity dispatcher;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private CompanyEntity company;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private DriverEntity driver;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Transient
    private String itemIdentifier;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, MileageData> mileageData;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DriverMileageEntity that = (DriverMileageEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
