package fpt.prm392.fe_salehunter.model.chat;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageModel {
    // Getters and setters
    @SerializedName("id")
    private long id;

    @SerializedName("content")
    private String content;

    @SerializedName("isUserMessage")
    private boolean isUserMessage;

    @SerializedName("createdAt")
    private String createdAt;
}
