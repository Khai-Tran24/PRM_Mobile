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
public class SendMessageRequestModel {
    // Getters and setters
    @SerializedName("conversationId")
    private Long conversationId; // Can be null for new conversation

    @SerializedName("message")
    private String message;
}
