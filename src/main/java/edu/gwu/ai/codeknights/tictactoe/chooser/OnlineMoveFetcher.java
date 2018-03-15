package edu.gwu.ai.codeknights.tictactoe.chooser;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import org.pmw.tinylog.Logger;

import java.util.List;
import java.util.stream.Stream;

import static edu.gwu.ai.codeknights.tictactoe.util.res.GetMovesRes.Move;

public class OnlineMoveFetcher extends AbstractOnlineChooser {

  @Override
  public Cell chooseCell(final Stream<Cell> input, final Game game) {
    if (game.isGameOver()) {
      Logger.debug("no more moves, game is over");
      return null;
    }
    final long gameId = game.getGameId();
    final int dim = game.getDim();
    final int numCells = dim * dim;
    final int numEmpty = game.getBoard().countEmpty();
    final int numMoves = numCells - numEmpty;
    final int numMovesExpected = numMoves + 1;
    final int numMovesToFetch = Math.max(numCells, numMovesExpected);
    final Player curPlayer = game.getNextPlayer();
    final int curPlayerId = curPlayer.getId();
    while (true) {
      Logger.debug("fetching up to {} moves from server", numMovesToFetch);
      List<Move> moves = getMoves(game);
      if(moves != null && moves.size() >= numMovesExpected){
        Move move = moves.get(0);
        if(move.getGameId().equals(gameId) && move.getTeamId().equals(curPlayerId)){
          return game.getBoard().getCell(move.getMoveX(), move.getMoveY());
        }
      }
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
