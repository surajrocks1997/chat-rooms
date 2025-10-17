package com.chat_rooms.auth_handler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class MediaDownloadResult {
    private byte[] content;
    private String mimeType;
    private String fileName;
    private long contentLength;
}
