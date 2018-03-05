package edu.gwu.ai.codeknights.tictactoe.gui.util;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.Map;

/**
 * @author zhiyuan
 */
public interface ApiService {

    @POST("index.php")
    Call<Map> post(@Body RequestBody body);

    @GET("index.php?type=team")
    Call<Map> getTeamMembers(@Query("teamId") String teamId);

    @GET("index.php?type=moves")
    Call<Map> getMoves(@Query("gameId") String gameId, @Query("count") Integer count);
}
