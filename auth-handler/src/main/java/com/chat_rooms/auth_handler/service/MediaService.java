package com.chat_rooms.auth_handler.service;

import com.chat_rooms.auth_handler.dto.MediaDownloadResult;
import com.chat_rooms.auth_handler.entity.MediaAsset;
import com.chat_rooms.auth_handler.repository.mongo.MediaAssetRepository;
import com.chat_rooms.auth_handler.utils.MediaOperationsUtils;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaOperationsUtils mediaOperationsUtils;
    private final MediaAssetRepository mediaAssetRepository;

    public ObjectId storeMedia(Long userId, MediaDownloadResult mediaAndMetadata, String ownerType, String mediaType) throws Exception {
        ObjectId gridFsObjectId = mediaOperationsUtils.storeImage(
                mediaAndMetadata.getContent(),
                mediaAndMetadata.getFileName(),
                ownerType,
                userId,
                mediaType,
                mediaAndMetadata.getMimeType()
        );

        MediaAsset mediaAsset = MediaAsset.builder()
                .ownerType(ownerType)
                .ownerId(userId)
                .mediaType(mediaType)
                .mimeType(mediaAndMetadata.getMimeType())
                .gridFsId(gridFsObjectId.toHexString())
                .isPrimary(true)
                .build();

        mediaAssetRepository.save(mediaAsset);

        return gridFsObjectId;
    }
}
