package fpt.prm392.fe_salehunter.data.remote.interfaces;

import fpt.prm392.fe_salehunter.model.chat.ChatConversationModel;
import fpt.prm392.fe_salehunter.model.chat.SendMessageRequestModel;
import fpt.prm392.fe_salehunter.model.chat.SendMessageResponseModel;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface ChatInterface {

    @Headers({"client: mobile"})
    @POST("api/Chat/send")
    Observable<Response<BaseResponseModel<SendMessageResponseModel>>> sendMessage(
            @Header("Authorization") String authorization,
            @Body SendMessageRequestModel request
    );

    @Headers({"client: mobile"})
    @GET("api/Chat/conversations")
    Observable<Response<BaseResponseModel<List<ChatConversationModel>>>> getConversations(
            @Header("Authorization") String authorization
    );

    @Headers({"client: mobile"})
    @GET("api/Chat/conversations/{conversationId}")
    Observable<Response<BaseResponseModel<ChatConversationModel>>> getConversation(
            @Header("Authorization") String authorization,
            @Path("conversationId") long conversationId
    );
}
