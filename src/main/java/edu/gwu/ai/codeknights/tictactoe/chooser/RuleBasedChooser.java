package edu.gwu.ai.codeknights.tictactoe.chooser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.core.Board;
import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;

public class RuleBasedChooser extends AbstractCellChooser {

  @Override
  public Cell chooseCell(final Stream<Cell> input, final Game game) {
    return applyRules(input.collect(Collectors.toList()), game);
  }

  public static Cell applyRules(final List<Cell> cells, final Game game) {
    // Rule 1: first move goes near the center
    final Board board = game.getBoard();
    final int dim = game.getDim();
    final int center = (int) (dim / 2);
    if (board.isEmpty()) {
      final Cell centerCell = findCellNear(cells, center, center, Math.max(2, (int) (dim / 4)));
      if (centerCell != null) {
        return centerCell;
      }
    }

    // Rule 2: win if immediately possible
    final int winLength = game.getWinLength();
    final Player player = game.getNextPlayer();
    final Cell winningCell = findWinningCell(game, cells, winLength, player);
    if (winningCell != null) {
      return winningCell;
    }

    // Rule 3: block opponent if he can win
    final Player opponent = game.getOtherPlayer(player);
    final Cell losingCell = findWinningCell(game, cells, winLength, opponent);
    if (losingCell != null) {
      return losingCell;
    }

    // Rule 4: try to fork on this move
    final List<Cell> forkCells = findForkCells(game, cells, winLength);
    final Cell forkCell = forkCells.stream().findAny().orElse(null);
    if (forkCell != null) {
      return forkCell;
    }

    /*
    // Rule 5: prevent opponent fork on next move
    final Set<Cell> cellsCausingOppFork = new HashSet<>();
    for (final Cell cell : cells) {
      if (cell.isEmpty()) {
        cell.setPlayer(player);
        final List<Cell> otherCells = new ArrayList<>(cells);
        otherCells.remove(cell);
        final Cell oppCell = applyRules(otherCells, game);
        if (oppCell != null && oppCell.isEmpty()) {
          final int numWinsBefore = countNumberOfWinningCellsFor(game, opponent);
          oppCell.setPlayer(opponent);
          final int numWinsAfter = countNumberOfWinningCellsFor(game, opponent);
          oppCell.setPlayer(null);
          if (numWinsAfter > numWinsBefore + 1) {
            cellsCausingOppFork.add(cell);
          }
        }
        cell.setPlayer(null);
      }
    }
    cells.removeAll(cellsCausingOppFork);
    
    // TODO: any other rules ???
    */

    // Rule 6: try to win in 2 moves
    final Cell winInTwoCell = findWinInTwo(game, cells, winLength, player);
    if (winInTwoCell != null) {
      return winInTwoCell;
    }

    // Rule 7: block opponent early if he can win in 2 moves
    final Cell loseInTwoCell = findWinInTwo(game, cells, winLength, opponent);
    if (loseInTwoCell != null) {
      return loseInTwoCell;
    }

    // No choice made
    return null;
  }

  public static Cell findCellNear(final List<Cell> cells, final int rowIdx, final int colIdx, final int maxRadius) {
    for (int i = 0; i < maxRadius; i++) {
      final int curRadius = i;
      final Cell closeCell = cells.stream()
        .filter(
          cell -> Math.abs(cell.getRowIdx() - rowIdx) <= curRadius && Math.abs(cell.getColIdx() - colIdx) <= curRadius)
        .findAny()
        .orElse(null);
      if (closeCell != null) {
        return closeCell;
      }
    }
    return null;
  }

  public static Cell findWinningCell(final Game game, final List<Cell> cells, final int winLength,
    final Player player) {
    return cells.stream()
      .filter(cell -> {

        // Find all lines through this cell
        final List<List<Cell>> lines = game.getBoard().findLinesThrough(cell, winLength);

        // Find those lines where the player would win by playing on this cell
        return lines.stream()
          .anyMatch(line -> {
            if (cell.isEmpty()) {
              cell.setPlayer(player);
              final boolean didWin = game.didPlayerWinOnLine(player, line);
              cell.setPlayer(null);
              return didWin;
            }
            return false;
          });
      })
      .findAny()
      .orElse(null);
  }

  public static Cell findWinInTwo(final Game game, final List<Cell> cells, final int winLength, final Player player) {
    final Board board = game.getBoard();
    final int dim = game.getDim();
    final int center = (int) (dim / 2);
    for (int i = 0; i < cells.size(); i++) {
      final Cell firstCell = cells.get(i);
      if (firstCell.isEmpty()
        && game.getBoard().getNeighborsOfCell(firstCell).stream().anyMatch(cell -> cell.isPopulatedBy(player))) {
        for (int j = 1; j < cells.size(); j++) {
          if (j != i) {
            final Cell secondCell = cells.get(j);
            if (secondCell.isEmpty()
              && board.getNeighborsOfCell(secondCell).stream().anyMatch(cell -> cell.isPopulatedBy(player))) {
              firstCell.setPlayer(player);
              secondCell.setPlayer(player);
              final boolean didWin = game.didPlayerWin(player);
              firstCell.setPlayer(null);
              secondCell.setPlayer(null);
              if (didWin) {
                final int distFromCenter1 = Math.abs(firstCell.getRowIdx() - center)
                  + Math.abs(firstCell.getColIdx() - center);
                final int distFromCenter2 = Math.abs(secondCell.getRowIdx() - center)
                  + Math.abs(secondCell.getColIdx() - center);
                return distFromCenter2 < distFromCenter1 ? secondCell : firstCell;
              }
            }
          }
        }
      }
    }
    return null;
  }

  public static int countNumberOfWinningCellsFor(final Game game, final Player player, final Set<Cell> ignoreCells) {
    int numWins = 0;
    final Set<Cell> possibleCells = new HashSet<>(game.getBoard().getEmptyCells());
    if (ignoreCells != null) {
      possibleCells.removeAll(ignoreCells);
    }
    for (final Cell cell : possibleCells) {
      if (cell.getPlayer() == null) {
        cell.setPlayer(player);
        if (game.didPlayerWin(player)) {
          numWins++;
        }
        cell.setPlayer(null);
      }
    }
    return numWins;
  }

  public static int countNumberOfWinningCellsFor(final Game game, final Player player) {
    return countNumberOfWinningCellsFor(game, player, new HashSet<>());
  }

  public static List<Cell> findForkCells(final Game game, final List<Cell> cells, final int winLength) {
    final List<Cell> forkCells = new ArrayList<>();
    final Player curPlayer = game.getNextPlayer();
    for (final Cell cell : cells) {
      if (cell.isEmpty()) {
        final int numWinsBefore = countNumberOfWinningCellsFor(game, curPlayer, Collections.singleton(cell));
        cell.setPlayer(curPlayer);
        final int numWinsAfter = countNumberOfWinningCellsFor(game, curPlayer);
        cell.setPlayer(null);
        if (numWinsAfter > numWinsBefore + 1) {
          forkCells.add(cell);
        }
      }
    }
    return forkCells;
  }
}
