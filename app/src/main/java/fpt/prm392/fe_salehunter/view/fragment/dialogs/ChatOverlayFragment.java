package fpt.prm392.fe_salehunter.view.fragment.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.view.adapter.ChatAdapter;
import fpt.prm392.fe_salehunter.viewmodel.fragment.ChatViewModel;

public class ChatOverlayFragment extends Fragment {
    
    private ChatViewModel chatViewModel;
    private ChatAdapter chatAdapter;
    private RecyclerView recyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ImageButton closeButton;
    private ImageButton newConversationButton;
    private ProgressBar loadingIndicator;
    private View overlayBackground;
    private View chatModal;

    public interface ChatOverlayListener {
        void onChatClosed();
    }

    private ChatOverlayListener chatOverlayListener;

    public void setChatOverlayListener(ChatOverlayListener listener) {
        this.chatOverlayListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_overlay, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupViewModel();
        setupRecyclerView();
        setupClickListeners();
        animateIn();
        
        // Load existing conversations first, then determine if we need a new one
        loadExistingConversationOrStartNew();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rv_chat_messages);
        messageInput = view.findViewById(R.id.et_message_input);
        sendButton = view.findViewById(R.id.btn_send_message);
        closeButton = view.findViewById(R.id.btn_close_chat);
        newConversationButton = view.findViewById(R.id.btn_new_conversation);
        loadingIndicator = view.findViewById(R.id.pb_loading);
        overlayBackground = view.findViewById(R.id.chat_overlay_background);
        chatModal = view.findViewById(R.id.chat_modal);
    }

    private void setupViewModel() {
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        
        // Observe messages
        chatViewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            if (messages != null) {
                chatAdapter.setMessages(messages);
                scrollToBottom();
            }
        });
        
        // Observe loading state
        chatViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                sendButton.setEnabled(!isLoading);
            }
        });
        
        // Observe error messages
        chatViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                chatViewModel.clearError();
            }
        });
        
        // Observe conversations list to load the most recent one
        chatViewModel.getConversations().observe(getViewLifecycleOwner(), conversations -> {
            if (conversations != null && !conversations.isEmpty()) {
                // Load the most recent conversation (first one in the list)
                long mostRecentConversationId = conversations.get(0).getId();
                chatViewModel.loadConversation(mostRecentConversationId);
            } else {
                // No existing conversations, start a new one
                chatViewModel.startNewConversation();
            }
        });
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chatAdapter);
        
        // Initialize with empty list
        chatAdapter.setMessages(new ArrayList<>());
    }

    private void setupClickListeners() {
        sendButton.setOnClickListener(v -> sendMessage());
        closeButton.setOnClickListener(v -> closeChat());
        newConversationButton.setOnClickListener(v -> startNewConversation());
        overlayBackground.setOnClickListener(v -> closeChat());
        
        // Handle enter key in message input
        messageInput.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (!message.isEmpty()) {
            chatViewModel.sendMessage(message);
            messageInput.setText("");
        }
    }
    
    private void startNewConversation() {
        chatViewModel.startNewConversation();
        Toast.makeText(getContext(), "Started new conversation", Toast.LENGTH_SHORT).show();
    }

    private void closeChat() {
        animateOut(() -> {
            if (chatOverlayListener != null) {
                chatOverlayListener.onChatClosed();
            }
        });
    }

    private void scrollToBottom() {
        if (chatAdapter.getItemCount() > 0) {
            recyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

    private void animateIn() {
        if (getContext() != null) {
            Animation fadeIn = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
            Animation slideIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_up);
            
            overlayBackground.startAnimation(fadeIn);
            chatModal.startAnimation(slideIn);
        }
    }

    private void animateOut(Runnable onComplete) {
        if (getContext() != null) {
            Animation fadeOut = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
            Animation slideOut = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_down);
            
            slideOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            
            overlayBackground.startAnimation(fadeOut);
            chatModal.startAnimation(slideOut);
        } else {
            if (onComplete != null) {
                onComplete.run();
            }
        }
    }
    
    private void loadExistingConversationOrStartNew() {
        // First try to load existing conversations
        chatViewModel.loadConversations();
    }
}
