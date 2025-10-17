package com.chat_rooms.auth_handler.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collation = "media_assets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaAsset {

    @Id
    private String id;

    private String ownerType;
    private Long ownerId;

    private String mimeType;
    private String mediaType;
    private String gridFsId;
    private boolean isPrimary; // Indicates if this media is the primary one for the owner

    private Instant createdAt;
}
