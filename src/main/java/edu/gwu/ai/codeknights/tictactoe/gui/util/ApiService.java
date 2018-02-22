package edu.gwu.ai.codeknights.tictactoe.gui.util;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.Map;

/**
 * @author zhiyuan
 */
public interface ApiService {

    @POST("/index.php")
    Call<Map> post(@Body RequestBody body);

    @GET("/index.php?type=team&teamId={teamId}")
    Call<Map> getTeamMembers(@Path("teamId") String teamId);

    @GET("/index.php?type=moves&gameId={gameId}&count={}")
    Call<Map> getMoves(@Path("gameId") String gameId, @Path("count") Integer count);

}
