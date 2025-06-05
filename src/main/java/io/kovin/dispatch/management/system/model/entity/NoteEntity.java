package io.kovin.dispatch.management.system.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
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
@Table(name = "t_notes")
@Entity
public class NoteEntity {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notes_sequence_generator")
    @SequenceGenerator(name = "notes_sequence_generator", sequenceName = "t_notes_sequence", allocationSize = 1)
    @Id
    private Long id;

    @Column
    private String uuid;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
