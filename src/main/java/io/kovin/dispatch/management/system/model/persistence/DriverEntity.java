package io.kovin.dispatch.management.system.model.persistence;

import io.kovin.dispatch.management.system.model.persistence.enums.DocumentStatus;
import io.kovin.dispatch.management.system.model.persistence.enums.DriverPosition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "t_drivers")
@Entity
public class DriverEntity extends Auditable {
    @Id
    @SequenceGenerator(name = "driver_sequence_generator", sequenceName = "t_drivers_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "driver_sequence_generator")
    private Long id;

    @Column
    private UUID uuid;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String fullName;

    @Column
    private String phoneNumber;

    @Column
    private String email;

    @Enumerated(EnumType.STRING)
    @Column
    private DocumentStatus documentStatus;

    @Enumerated(EnumType.STRING)
    @Column
    private DriverPosition position;

    @Column
    private String state;

    @Column
    private String city;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private CompanyEntity company;

    @ManyToOne
    @JoinColumn(name = "truck_id")
    private TruckEntity truck;

    @ManyToOne
    @JoinColumn(name = "trailer_id")
    private TrailerEntity trailer;

    @ManyToOne
    @JoinColumn(name = "dispatcher_id")
    private DispatcherEntity dispatcher;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DriverEntity that = (DriverEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
