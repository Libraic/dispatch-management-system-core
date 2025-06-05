package io.kovin.dispatch.management.system.mapper;

import java.util.List;
import java.util.UUID;
import io.kovin.dispatch.management.system.model.entity.NoteEntity;
import io.kovin.dispatch.management.system.model.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {
    
    public List<NoteEntity> fromDescriptionsAndUserEntityToNoteEntities(List<String> descriptions, UserEntity user) {
        return descriptions.stream()
            .map(description -> NoteEntity.builder()
                .description(description)
                .uuid(UUID.randomUUID().toString())
                .user(user)
                .build()
            ).toList();
    }
}
