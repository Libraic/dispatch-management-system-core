package io.kovin.dispatch.management.system.mapper;

import java.util.List;
import java.util.UUID;
import io.kovin.dispatch.management.system.model.entity.NoteEntity;
import io.kovin.dispatch.management.system.model.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {
    
    public List<NoteEntity> fromDescriptionsAndUserEntityToNoteEntities(List<String> contents, UserEntity user) {
        return contents.stream()
            .map(content -> NoteEntity.builder()
                .content(content)
                .uuid(UUID.randomUUID().toString())
                .user(user)
                .build()
            ).toList();
    }
}
