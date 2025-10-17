package com.chat_rooms.auth_handler.utils;

import com.chat_rooms.auth_handler.dto.MediaDownloadResult;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class MediaOperationsUtils {

    private final GridFsTemplate gridFsTemplate;
    private static final String OWNER_TYPE = "ownerType";
    private static final String OWNER_ID = "ownerId";
    private static final String MEDIA_TYPE = "mediaType";
    private static final String MIME_TYPE = "mimeType";

    public MediaDownloadResult getMediaAndMetadata(String imageUrl) throws IOException, URISyntaxException {
        URI uri = new URI(imageUrl);
        URLConnection connection = uri.toURL().openConnection();
        connection.connect();

        String mimeType = connection.getContentType();
        long contentLength = connection.getContentLengthLong();
        String fileName = extractFileNameFromUrl(uri);


        try (InputStream in = connection.getInputStream()) {
            byte[] content = in.readAllBytes();
            return MediaDownloadResult.builder()
                    .content(content)
                    .mimeType(mimeType)
                    .fileName(fileName)
                    .contentLength(contentLength)
                    .build();
        }
    }

    private String extractFileNameFromUrl(URI uri) {
        String path = uri.getPath();
        return path != null ? Paths.get(path).getFileName().toString() : "unknown";
    }


    // accept byte[] of image and store in GridFs
    public ObjectId storeImage(byte[] imageBytes, String fileName, String ownerType, Long ownerId, String mediaType, String mimeType) throws IOException {
        DBObject metaData = new BasicDBObject();
        metaData.put(OWNER_TYPE, ownerType);
        metaData.put(OWNER_ID, ownerId);
        metaData.put(MEDIA_TYPE, mediaType);
        metaData.put(MIME_TYPE, mimeType);

        return gridFsTemplate.store(new ByteArrayInputStream(imageBytes), fileName, mimeType, metaData);
    }


    public ObjectId storeImage(MultipartFile file, String ownerType, Long ownerId, String mediaType) throws IOException {
        DBObject metaData = new BasicDBObject();
        metaData.put(OWNER_TYPE, ownerType);
        metaData.put(OWNER_ID, ownerId);
        metaData.put(MEDIA_TYPE, mediaType);
        metaData.put(MIME_TYPE, file.getContentType());

        return gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), metaData);
    }

    public GridFsResource getImage(ObjectId fileId) {
        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        return gridFsTemplate.getResource(file);
    }
}
