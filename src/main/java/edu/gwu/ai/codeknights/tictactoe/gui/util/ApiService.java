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

    @GET("index.php?type=moves")
    Call<Map> getMoves(@Query("gameId") String gameId, @Query("count") Integer count);
}
