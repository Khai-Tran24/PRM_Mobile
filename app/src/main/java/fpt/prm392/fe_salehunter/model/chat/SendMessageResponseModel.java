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
public class SendMessageResponseModel {
    // Getters and setters
    @SerializedName("conversationId")
    private long conversationId;

    @SerializedName("userMessage")
    private ChatMessageModel userMessage;

    @SerializedName("aiResponse")
    private ChatMessageModel aiResponse;

}
