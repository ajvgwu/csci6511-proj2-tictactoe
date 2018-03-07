package edu.gwu.ai.codeknights.tictactoe.gui.util;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.Map;

/**
 * @author zhiyuan
 */
public interface ApiService {

    @FormUrlEncoded
    @POST("index.php")
    Call<Map> post(
      @Field("type") String type,
      @Field("teamId") String teamId,
      @Field("gameId") String gameId,
      @Field("move") String move
    );

    @FormUrlEncoded
    @POST("index.php")
    Call<Map> postGame(
      @Field("type") String type,
      @Field("teamId1") String teamId1,
      @Field("teamId2") String teamId2
    );

    @GET("index.php?type=team")
    Call<Map> getTeamMembers(@Query("teamId") String teamId);

    @GET("index.php?type=moves")
    Call<Map> getMoves(@Query("gameId") String gameId, @Query("count") Integer count);
}
