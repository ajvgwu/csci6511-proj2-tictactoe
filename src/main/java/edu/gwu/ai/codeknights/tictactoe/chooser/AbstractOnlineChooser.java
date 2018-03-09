package edu.gwu.ai.codeknights.tictactoe.chooser;

import java.io.IOException;
import java.util.*;

import edu.gwu.ai.codeknights.tictactoe.util.Const;
import org.pmw.tinylog.Logger;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import edu.gwu.ai.codeknights.tictactoe.gui.util.API;
import retrofit2.Call;
import retrofit2.Response;

public abstract class AbstractOnlineChooser extends AbstractCellChooser {

  public static void tryFastForward(final Game game) {
    final long gameId = game.getGameId();
    final int dim = game.getDim();
    final int numCells = dim * dim;
    final Call<Map> call = API.getApiService().getMoves(String.valueOf(gameId), numCells);
    try {
      final Response<Map> response = call.execute();
      Logger.debug("got response from server: {}", response);
      final Map<?, ?> body = response.body();
      Logger.debug("body of response: {}", body);
      if(body == null){
        return;
      }
      Object o = body.get(API.API_RESPONSEKEY_CODE);
      if (o instanceof String) {
        if (o.equals(API.API_CODE_SUCCESS)) {
          o = body.get(API.API_RESPONSEKEY_MOVES);
          if (o instanceof List<?>) {
            final List<?> moves = (List<?>) o;
            List<Cell> cells = parseCells(game, moves);
            Integer prevPlayerId = null;
            for(Cell cell: cells){
              Player player = cell.getPlayer();
              if(player != null){
                if(prevPlayerId != null && player.getId() == prevPlayerId){
                    Logger.warn("moves from server might be out of order");
                }
                prevPlayerId = player.getId();
                game.getBoard().getCell(cell.getRowIdx(), cell.getColIdx())
                        .setPlayer(player);
              }
            }
          }
        }
        else {
          final Object msgObj = body.get(API.API_RESPONSEKEY_MESSAGE);
          Logger.error("got response {} from server with message: {}", o, msgObj);
        }
      }
    }
    catch (final IOException e) {
      Logger.error(e, "error while fetching moves from server to fast-forward game");
    }
  }

  private static List<Cell> parseCells(final Game game, List<?> moves){
    Set<Cell> cellSet = new HashSet<>();
    for (int i = moves.size() - 1; i >= 0; i--) {
      final Object item = moves.get(i);
      if (item instanceof Map<?, ?>) {
        final Map<?, ?> move = (Map<?, ?>) item;
        final Object moveObj = move.get(API.API_MOVEKEY_MOVE);
        final Cell cell = tryGetCellFromCoord((String) moveObj);
        if (cell != null) {
          final Object teamIdObj = move.get(API.API_MOVEKEY_TEAMID);
          Integer teamId = Integer.parseInt((String)teamIdObj);
          Player player = game.getPlayer(teamId);
          cell.setPlayer(player);
          cellSet.add(cell);
        }
      }
    }
    Logger.debug("Fetched "+cellSet.size()+" moves from server");
    return new ArrayList<>(cellSet);
  }

  /**
   * Tries to parse the given {@code coords} string and return a new  {@link Cell}.
   * The expected format is {@code rowIdx,colIdx} (zero-based). For example, {@code 0,0} is the top-left cell.
   *
   * @param coords the coordinate of the cell
   *
   * @return the {@link Cell}
   */
  static Cell tryGetCellFromCoord(final String coords) {
    if (coords != null) {
      final String[] parts = coords.split(",", 2);
      if (parts.length >= 2) {
        final String rowVal = parts[0];
        final String colVal = parts[1];
        try {
          final int rowIdx = Integer.parseInt(rowVal.trim())- Const.ONLINE_BOARD_OFFSET;
          final int colIdx = Integer.parseInt(colVal.trim())-Const.ONLINE_BOARD_OFFSET;
          return new Cell(rowIdx, colIdx);
        }
        catch (final NumberFormatException e) {
          Logger.error(e, "could not parse cell coordinates (expected format \"rowIdx,colIdx\"): " + coords);
        }
      }
    }
    return null;
  }
}
