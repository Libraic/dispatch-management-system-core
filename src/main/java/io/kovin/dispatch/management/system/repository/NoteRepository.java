package io.kovin.dispatch.management.system.repository;

import io.kovin.dispatch.management.system.model.entity.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<NoteEntity, Long> {
}
