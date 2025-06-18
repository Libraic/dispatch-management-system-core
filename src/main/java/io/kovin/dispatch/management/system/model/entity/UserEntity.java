package io.kovin.dispatch.management.system.model.entity;

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
@Table(name = "t_users")
@Entity
public class UserEntity extends Auditable {

    @Id
    @SequenceGenerator(name = "user_sequence_generator", sequenceName = "t_users_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence_generator")
    private Long id;

    @Column
    private String uuid;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column
    private Position position;

    @Column
    private LocalDate birthDate;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String personalEmail;

    @Column
    private LocalDate employmentDate;

    @Column
    private LocalDate dismissalDate;

    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    private UserEntity supervisor;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
