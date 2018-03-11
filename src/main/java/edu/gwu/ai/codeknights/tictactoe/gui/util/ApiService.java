package edu.gwu.ai.codeknights.tictactoe.gui.util;

import edu.gwu.ai.codeknights.tictactoe.gui.util.res.GetBoardRes;
import edu.gwu.ai.codeknights.tictactoe.gui.util.res.GetMovesRes;
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
      @Field("teamId2") String teamId2,
      @Field("gameType") String gameType,
      @Field("boardSize") String boardSize,
      @Field("target") String target
  );

  @GET("index.php?type=team")
  Call<Map> getTeamMembers(@Query("teamId") String teamId);

  @GET("index.php?type=moves")
  Call<GetMovesRes> getMoves(@Query("gameId") String gameId, @Query("count") Integer count);

  @GET("index.php?type=boardString")
  Call<GetBoardRes> getBoard(@Query("gameId") String gameId);


}
