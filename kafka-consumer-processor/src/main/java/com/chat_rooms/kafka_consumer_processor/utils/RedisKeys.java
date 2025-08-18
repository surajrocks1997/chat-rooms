package com.chat_rooms.kafka_consumer_processor.utils;

public final class RedisKeys {
    public static final String BASE = "chatRooms:";
    public static final String PRESENCE_SESSION_SESSIONID_TO_USERMETADATA = BASE + "presence:session:sessionId:";
    public static final String PRESENCE_USERID_TO_SESSIONID = BASE + "presence:userId:";
    public static final String PRESENCE_SESSION_TO_ROOM = BASE + "presence:session:";
    public static final String PRESENCE_ROOM_TO_SESSION = BASE + "presence:room:";

    private RedisKeys() {
        // Prevent instantiation
    }
}
