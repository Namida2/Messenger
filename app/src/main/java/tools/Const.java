package tools;

public class Const {
    public static final String TAG = "MyLogTag";
    public static final String EXTRA_TAG_POSITION = "position";
    public static final String TIME_DELIMITER = ":";
    public static final String USER_FIELDS_DELIMITER = ", ";

    public static class CollectionUsers {
        public static final String COLLECTION_USERS = "users";
        public static final String COLLECTION_MESSENGER = "messenger";
        public static final String DOCUMENT_CHATS = "chats";
        public static final String FIELD_CHATS_IDS = "chats_ids";
    }
    public static class CollectionChats {
        public static final String TYPE_DIALOG = "dialog";
        public static final String COLLECTION_CHATS = "chats";
        public static final String FIELD_TYPE = "type";
        public static final String FIELD_USERS = "users";
        public static final String FIELD_CHAT_NAME = "chat_name";
        public static final String FIELD_LAST_MESSAGE_AT = "last_message_at";
        public static final String FIELD_MESSAGES_IN_CHAT = "messages_in_chat";
        public static final String CHAT_ID_DELIMITER = " | ";
    }

    public static class CollectionMessages {
        public static final String COLLECTION_MESSAGES = "messages";
        public static final String FIELD_TIME = "time";
        public static final String FIELD_MESSAGE = "message";
        public static final String FIELD_AUTHOR_EMAIL = "chat_name";
    }

}

