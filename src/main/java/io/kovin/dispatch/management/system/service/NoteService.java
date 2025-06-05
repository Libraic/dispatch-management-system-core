package io.kovin.dispatch.management.system.service;

import java.util.List;
import io.kovin.dispatch.management.system.mapper.NoteMapper;
import io.kovin.dispatch.management.system.model.entity.NoteEntity;
import io.kovin.dispatch.management.system.model.entity.UserEntity;
import io.kovin.dispatch.management.system.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteMapper noteMapper;
    private final NoteRepository noteRepository;

    public List<NoteEntity> saveNotes(List<String> notes, UserEntity user) {
        if (notes == null) {
            return List.of();
        }

        List<NoteEntity> noteEntities = noteMapper.fromDescriptionsAndUserEntityToNoteEntities(notes, user);
        return noteRepository.saveAll(noteEntities);
    }
}
