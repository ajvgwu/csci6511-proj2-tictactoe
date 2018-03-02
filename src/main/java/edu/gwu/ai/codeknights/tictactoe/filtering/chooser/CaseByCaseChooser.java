package edu.gwu.ai.codeknights.tictactoe.filtering.chooser;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.filtering.core.Board;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.Player;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.TicTacToeGame;
import edu.gwu.ai.codeknights.tictactoe.filtering.filter.PopulatedNeighborFilter;

public class CaseByCaseChooser extends AbstractCellChooser {

  private final PopulatedNeighborFilter neighborFilter;
  private final AlphaBetaPruningChooser abpChooser;

  public CaseByCaseChooser() {
    neighborFilter = new PopulatedNeighborFilter();
    abpChooser = new AlphaBetaPruningChooser();
  }

  @Override
  public Cell chooseCell(final Stream<Cell> input, final TicTacToeGame game) {
    final List<Cell> cells = input.collect(Collectors.toList());
    final Player player = game.getNextPlayer();

    // Empty board, play near center
    final Board board = game.getBoard();
    final int dim = game.getDim();
    if (board.isEmpty()) {
      final int center = (int) (dim / 2);
      final Cell cell = RuleBasedChooser.findCellNear(cells, center, center, Math.max(2, (int) (dim / 4)));
      if (cell != null) {
        return cell;
      }
    }

    // Try to win
    final int winLength = game.getWinLength();
    final Cell winningCell = RuleBasedChooser.findWinningCell(game, cells, winLength, player);
    if (winningCell != null) {
      return winningCell;
    }

    // Try not to lose
    final Player opponent = game.getOtherPlayer(player);
    final Cell notLoseCell = RuleBasedChooser.findWinningCell(game, cells, winLength, opponent);
    if (notLoseCell != null) {
      return notLoseCell;
    }

    // Try a pairing strategy
    final Cell pairCell = PairingChooser.tryFindPair(game, cells);
    if (pairCell != null) {
      return pairCell;
    }

    // Play smartly
    abpChooser.setMaxDepth(dim);
    return abpChooser.chooseCell(neighborFilter.filterCells(cells.stream(), game), game);
  }
}
