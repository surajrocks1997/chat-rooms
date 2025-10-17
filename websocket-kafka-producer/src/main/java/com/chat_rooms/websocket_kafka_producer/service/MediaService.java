package com.chat_rooms.websocket_kafka_producer.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MediaService {

    private final GridFsTemplate gridFsTemplate;

    public GridFSFile getMedia(String gridFsID) {
        return gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(gridFsID)));
    }

    public GridFsResource getResource(GridFSFile media) {
        return gridFsTemplate.getResource(media);

    }
}
