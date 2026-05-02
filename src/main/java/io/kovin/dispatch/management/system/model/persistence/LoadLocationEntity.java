package io.kovin.dispatch.management.system.model.persistence;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;
import io.kovin.dispatch.management.system.model.persistence.enums.LocationType;
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
@Table(name = "t_load_locations")
public class LoadLocationEntity {

    @Id
    @SequenceGenerator(name = "load_locations_sequence_generator", sequenceName = "t_load_locations_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "load_locations_sequence_generator")
    private Long id;

    @Column
    private UUID uuid;

    @Column
    private String location;

    @Column
    private LocalDate date;

    @Column
    private LocalTime time;

    @Column
    private String address;

    @Column
    @Enumerated(EnumType.STRING)
    private LocationType locationType;

    @Column
    private Short locationOrder;

    @Column
    private String timezone;

    @ManyToOne
    @JoinColumn(name = "load_id")
    private LoadEntity load;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LoadLocationEntity that = (LoadLocationEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
