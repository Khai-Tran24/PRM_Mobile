package fpt.prm392.fe_salehunter.viewmodel.fragment;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import fpt.prm392.fe_salehunter.data.Repository;
import fpt.prm392.fe_salehunter.model.chat.ChatConversationModel;
import fpt.prm392.fe_salehunter.model.chat.ChatMessageModel;
import fpt.prm392.fe_salehunter.model.chat.SendMessageRequestModel;
import fpt.prm392.fe_salehunter.model.chat.SendMessageResponseModel;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.util.UserAccountManager;
import retrofit2.Response;

public class ChatViewModel extends AndroidViewModel {
    private final Repository repository;
    private final MutableLiveData<List<ChatMessageModel>> messages = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Long> currentConversationId = new MutableLiveData<>();
    private final MutableLiveData<List<ChatConversationModel>> conversations = new MutableLiveData<>();

    public ChatViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository();
        isLoading.setValue(false);
    }

    public LiveData<List<ChatMessageModel>> getMessages() {
        return messages;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Long> getCurrentConversationId() {
        return currentConversationId;
    }

    public LiveData<List<ChatConversationModel>> getConversations() {
        return conversations;
    }

    public void sendMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            errorMessage.setValue("Message cannot be empty");
            return;
        }

        isLoading.setValue(true);
        String token = UserAccountManager.getToken(getApplication(), UserAccountManager.TOKEN_TYPE_BEARER);
        
        if (token == null || token.isEmpty()) {
            errorMessage.setValue("Authentication required");
            isLoading.setValue(false);
            return;
        }

        SendMessageRequestModel request = new SendMessageRequestModel();
        request.setMessage(message.trim());
        
        Long currentConvId = currentConversationId.getValue();
        if (currentConvId != null && currentConvId > 0) {
            request.setConversationId(currentConvId);
        }

        LiveData<Response<BaseResponseModel<SendMessageResponseModel>>> responseData = 
            repository.sendMessage(token, request);

        responseData.observeForever(new Observer<>() {
            @Override
            public void onChanged(Response<BaseResponseModel<SendMessageResponseModel>> response) {
                responseData.removeObserver(this);
                isLoading.setValue(false);

                if (response != null && response.isSuccessful() && response.body() != null) {
                    BaseResponseModel<SendMessageResponseModel> baseResponse = response.body();
                    if (baseResponse.isSuccess() && baseResponse.getData() != null) {
                        SendMessageResponseModel data = baseResponse.getData();

                        // Update conversation ID
                        currentConversationId.setValue(data.getConversationId());

                        // Add both messages to the list
                        List<ChatMessageModel> currentMessages = messages.getValue();
                        if (currentMessages != null) {
                            currentMessages.add(data.getUserMessage());
                            currentMessages.add(data.getAiResponse());
                            messages.setValue(currentMessages);
                        }
                    } else {
                        errorMessage.setValue(baseResponse.getMessage());
                    }
                } else if (response != null) {
                    errorMessage.setValue("Failed to send message: " + response.code());
                } else {
                    errorMessage.setValue("Network error occurred");
                }
            }
        });
    }

    public void loadConversations() {
        String token = UserAccountManager.getToken(getApplication(), UserAccountManager.TOKEN_TYPE_BEARER);
        
        if (token == null || token.isEmpty()) {
            errorMessage.setValue("Authentication required");
            return;
        }

        LiveData<Response<BaseResponseModel<List<ChatConversationModel>>>> responseData = 
            repository.getConversations(token);

        responseData.observeForever(new Observer<>() {
            @Override
            public void onChanged(Response<BaseResponseModel<List<ChatConversationModel>>> response) {
                responseData.removeObserver(this);

                if (response != null && response.isSuccessful() && response.body() != null) {
                    BaseResponseModel<List<ChatConversationModel>> baseResponse = response.body();
                    if (baseResponse.isSuccess() && baseResponse.getData() != null) {
                        conversations.setValue(baseResponse.getData());
                    }
                }
            }
        });
    }

    public void loadConversation(long conversationId) {
        isLoading.setValue(true);
        String token = UserAccountManager.getToken(getApplication(), UserAccountManager.TOKEN_TYPE_BEARER);
        
        if (token == null || token.isEmpty()) {
            errorMessage.setValue("Authentication required");
            isLoading.setValue(false);
            return;
        }

        LiveData<Response<BaseResponseModel<ChatConversationModel>>> responseData = 
            repository.getConversation(token, conversationId);

        responseData.observeForever(new Observer<>() {
            @Override
            public void onChanged(Response<BaseResponseModel<ChatConversationModel>> response) {
                responseData.removeObserver(this);
                isLoading.setValue(false);

                if (response != null && response.isSuccessful() && response.body() != null) {
                    BaseResponseModel<ChatConversationModel> baseResponse = response.body();
                    if (baseResponse.isSuccess() && baseResponse.getData() != null) {
                        ChatConversationModel conversation = baseResponse.getData();
                        currentConversationId.setValue(conversation.getId());
                        messages.setValue(conversation.getMessages());
                    } else {
                        errorMessage.setValue(baseResponse.getMessage());
                    }
                } else if (response != null) {
                    errorMessage.setValue("Failed to load conversation: " + response.code());
                } else {
                    errorMessage.setValue("Network error occurred");
                }
            }
        });
    }

    public void startNewConversation() {
        currentConversationId.setValue(null);
        messages.setValue(new ArrayList<>());
    }

    public void clearError() {
        errorMessage.setValue(null);
    }
    
    // Debug method to check if user has existing conversations
    public void debugCheckConversations() {
        String token = UserAccountManager.getToken(getApplication(), UserAccountManager.TOKEN_TYPE_BEARER);
        
        if (token == null || token.isEmpty()) {
            errorMessage.setValue("Debug: No authentication token found");
            return;
        }
        
        loadConversations();
    }
}
