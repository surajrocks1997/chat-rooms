package com.chat_rooms.websocket_kafka_producer.controller;

import com.chat_rooms.websocket_kafka_producer.global.CustomException;
import com.chat_rooms.websocket_kafka_producer.service.MediaService;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/media")
@Slf4j
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getMedia(@PathVariable("id") String gridFsId) {
        log.info("MediaController: getMedia flow started");

        GridFSFile media = mediaService.getMedia(gridFsId);
        if (media == null)
            throw new CustomException("Media not found", HttpStatus.NOT_FOUND);

        GridFsResource resource = mediaService.getResource(media);
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] imageBytes = inputStream.readAllBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(resource.getContentType()));
            headers.setContentLength(imageBytes.length);
            headers.setContentDisposition(ContentDisposition.inline().filename(resource.getFilename()).build());

            log.info("MediaController: getMedia flow ended");
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error reading image from GridFS", e);
            throw new CustomException("Error reading media", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
