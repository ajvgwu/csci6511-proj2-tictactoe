package edu.gwu.ai.codeknights.tictactoe.cli;

import java.util.LinkedHashMap;
import java.util.Map;

import org.pmw.tinylog.Logger;

import edu.gwu.ai.codeknights.tictactoe.chooser.AbstractCellChooser;
import edu.gwu.ai.codeknights.tictactoe.chooser.Chooser;
import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
  name = "compare-choosers", sortOptions = false, showDefaultValues = true,
  description = "compare the given choosers on the next game move")
public class CompareChoosers extends AbstractOfflineSubcommand {

  @Option(
    names = {"--choosers"}, required = true, arity = "1..*",
    description = "choosers that will be compared on the next move for the current board state")
  private Chooser[] choosers = null;

  @Override
  protected void validateArgs() throws Exception {
    super.validateArgs();
    choosers = choosers != null ? choosers : new Chooser[0];
    if (choosers.length < 1) {
      throw new IllegalArgumentException("must select at least one chooser");
    }
  }

  protected Chooser[] getChoosers() {
    return choosers;
  }

  @Override
  public Void call() throws Exception {
    validateArgs();

    // Create game
    final Game game = createGame();

    // Compare selected choosers
    final Map<String, AbstractCellChooser> chooserMap = new LinkedHashMap<>();
    for (final Chooser chooser : getChoosers()) {
      chooserMap.put(chooser.getName(), chooser.createChooser());
    }
    System.out.println(
      "will test " + String.valueOf(chooserMap.size()) + " choosers on this board:\n" + String.valueOf(game));
    final Map<String, TestResult> resultMap = new LinkedHashMap<>();
    for (final String name : chooserMap.keySet()) {
      final AbstractCellChooser chooser = chooserMap.get(name);
      System.out.println("testing chooser '" + name + "' (class=" + chooser.getClass().getSimpleName() + ") ...");
      try {
        final Game copy = game.getCopy();
        final Player nextPlayer = copy.getNextPlayer();
        nextPlayer.setChooser(chooser);
        Integer rowIdx = null;
        Integer colIdx = null;
        String playerMark = null;
        Long playerUtility = null;
        final long startTimeMs = System.currentTimeMillis();
        final Cell cell = nextPlayer.chooseCell(copy);
        final long endTimeMs = System.currentTimeMillis();
        final double elapsedSec = (double) ((endTimeMs - startTimeMs) / 1000.0);
        if (cell != null) {
          rowIdx = cell.getRowIdx();
          colIdx = cell.getColIdx();
          copy.playInCell(rowIdx, colIdx, nextPlayer);
          playerUtility = copy.evaluatePlayerUtility(nextPlayer);
          playerMark = String.valueOf(copy.getBoard().getCell(rowIdx, colIdx).getPlayer().getMarker());
        }
        System.out.println("updated board:");
        System.out.println(String.valueOf(copy));
        resultMap.put(name, new TestResult(rowIdx, colIdx, playerMark, playerUtility, elapsedSec));
        System.out.println("time elapsed (sec): " + String.valueOf(elapsedSec));
      }
      catch (final Exception e) {
        Logger.error(e, "error while testing chooser: " + name);
        resultMap.put(name, new TestResult());
      }
      System.out.println();
    }
    for (final String name : resultMap.keySet()) {
      final TestResult result = resultMap.get(name);
      System.out.println(name + ": " + result.toString());
    }

    // Done.
    return null;
  }

  public static class TestResult {

    public final Integer rowIdx;
    public final Integer colIdx;
    public final String playerMark;
    public final Long playerUtility;
    public final Double elapsedSec;

    public TestResult(final Integer rowIdx, final Integer colIdx, final String playerMark, final Long playerUtility,
      final double elapsedSec) {
      this.rowIdx = rowIdx;
      this.colIdx = colIdx;
      this.playerMark = playerMark;
      this.playerUtility = playerUtility;
      this.elapsedSec = elapsedSec;
    }

    public TestResult() {
      rowIdx = null;
      colIdx = null;
      playerMark = null;
      playerUtility = null;
      elapsedSec = null;
    }

    @Override
    public String toString() {
      return new StringBuilder()
        .append("rowIdx=").append(rowIdx).append(", ")
        .append("colIdx=").append(colIdx).append(", ")
        .append("playerMark=").append(playerMark).append(", ")
        .append("playerUtility=").append(playerUtility).append(", ")
        .append("elapsedSec=").append(elapsedSec)
        .toString();
    }
  }
}
