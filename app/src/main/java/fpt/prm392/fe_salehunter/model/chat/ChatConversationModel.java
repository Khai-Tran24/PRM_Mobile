package fpt.prm392.fe_salehunter.model.chat;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatConversationModel {
    @SerializedName("id")
    private long id;

    @SerializedName("title")
    private String title;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("messages")
    private List<ChatMessageModel> messages;

    // Constructors
    public ChatConversationModel() {}

    public ChatConversationModel(long id, String title, String createdAt, String updatedAt, List<ChatMessageModel> messages) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.messages = messages;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ChatMessageModel> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessageModel> messages) {
        this.messages = messages;
    }
}
