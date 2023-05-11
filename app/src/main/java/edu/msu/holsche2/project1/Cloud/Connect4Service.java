package edu.msu.holsche2.project1.Cloud;

import edu.msu.holsche2.project1.Cloud.Models.LeaveResult;
import edu.msu.holsche2.project1.Cloud.Models.LoginResult;
import edu.msu.holsche2.project1.Cloud.Models.MatchResult;
import edu.msu.holsche2.project1.Cloud.Models.NameResult;
import edu.msu.holsche2.project1.Cloud.Models.SaveResult;
import edu.msu.holsche2.project1.Cloud.Models.StateResult;
import edu.msu.holsche2.project1.Cloud.Models.StatusResult;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Connect4Service {
    @FormUrlEncoded
    @POST("connect4-save-user.php")
    Call<SaveResult> saveUserSignUp(
            @Field("xml") String xml
    );
    @FormUrlEncoded
    @POST("connect4-load-user.php")
    Call<LoginResult> loginResult(
            @Field("xml") String xml
    );

    @FormUrlEncoded
    @POST("connect4-save-state.php")
    Call<SaveResult> saveGameState(
            @Field("roomid") String roomid,
            @Field("userid") String userid,
            @Field("gameState") String gameState,
            @Field("status") String status
    );

    @FormUrlEncoded
    @POST("connect4-join.php")
    Call<MatchResult> joinGame(
            @Field("userid") String userid
    );

    @FormUrlEncoded
    @POST("connect4-leave.php")
    Call<LeaveResult> leaveGame(
            @Field("roomid") String roomid,
            @Field("userid") String userid
    );

    /**
     * Get the current room status
     * @param roomid
     * @param userid
     * @return
     */
    @GET("connect4-get-status.php")
    Call<StatusResult> getGameStatus(
            @Query("roomid") String roomid,
            @Query("userid") String userid
    );

    /**
     * Get the entire game state
     * @param roomid
     * @param userid
     * @return
     */
    @GET("connect4-get-state.php")
    Call<StateResult> getGameState(
            @Query("roomid") String roomid,
            @Query("userid") String userid
    );

    @GET("connect4-get-name.php")
    Call<NameResult> getUsername(
            @Query("roomid") String roomid,
            @Query("isHost") String isHost
    );
}
