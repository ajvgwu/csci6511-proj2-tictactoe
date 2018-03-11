package edu.gwu.ai.codeknights.tictactoe.gui.util;

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

    @GET("index.php?type=moves")
    Call<GetMovesRes> getMoves(@Query("gameId") String gameId, @Query("count") Integer count);
}
