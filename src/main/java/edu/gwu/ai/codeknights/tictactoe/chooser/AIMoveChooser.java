package edu.gwu.ai.codeknights.tictactoe.chooser;

import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Move;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AIMoveChooser extends AbstractMoveChooser {

  private boolean randomChoice;
  private final Map<Long, Long> hashScoreMap;

  AIMoveChooser() {
    randomChoice = false;
    hashScoreMap = new HashMap<>();
  }

  private boolean isRandomChoice() {
    return randomChoice;
  }

  public void setRandomChoice(final boolean randomChoice) {
    this.randomChoice = randomChoice;
  }

  Map<Long, Long> getHashScoreMap() {
    return hashScoreMap;
  }

  static List<Move> findEmptyMoves(final Game game) {
    final int dim = game.getDim();
    List<Move> moves = new ArrayList<>();

    for (int rowIdx = 0; rowIdx < dim; rowIdx++) {
      for (int colIdx = 0; colIdx < dim; colIdx++) {
        final Integer value = game.getCellValue(rowIdx, colIdx);
        if (value == null) {
          // This is a possible move (empty cell)
          moves.add(new Move(rowIdx, colIdx, null, null));
        }
      }
    }
    return moves;
  }

  static List<Move> findPossibleMoves(final Game game) {
    final int prevPlayer = game.getPrevPlayer();
    final int curPlayer = game.getNextPlayer();
    List<Move> moves;
    moves = findEmptyMoves(game);
    for (Move move : moves) {
      int rowIdx = move.rowIdx;
      int colIdx = move.colIdx;
      final Move tempMove = new Move(rowIdx, colIdx, curPlayer, null);
      // rule 1: if there's a chance for anyone to win, take it immediately
      game.setCellValue(rowIdx, colIdx, curPlayer);
      final boolean didWin = game.didPlayerWin(curPlayer);
      game.setCellValue(rowIdx, colIdx, null);
      if (didWin) {
        return Collections.singletonList(tempMove);
      }

      // rule 2: if the other player can win, block it immediately
      game.setCellValue(rowIdx, colIdx, prevPlayer);
      final boolean didLose = game.didPlayerWin(prevPlayer);
      game.setCellValue(rowIdx, colIdx, null);
      if (didLose) {
        return Collections.singletonList(tempMove);
      }
    }


/*    moves = moves.stream().filter(move -> hasNeighbors(game, move))
        .collect(Collectors.toList());
    ArrayList<ArrayList<Move>> sequences = getLongestLines(game);
    moves = new ArrayList<>(findMovesAdjacentToWinningLine(game, moves, sequences));*/

    // rule 4: don't consider the move if it is not in a winning lines
    moves = new ArrayList<>(findWinningMoves(game));
    // rule 3: don't consider the move if it is adjacent to none
    moves = moves.stream().filter(move -> hasNeighbors(game, move)).collect(Collectors.toList());
    return moves;
  }

  /**
   * return all moves in winning sequences
   */
  private static Set<Move> findWinningMoves(Game game) {
    Set<Move> filterMoves = new HashSet<>();
    List<List<Move>> sequences = getWinningSequences(game);
    // filter all empty moves
    sequences.forEach(sequence -> filterMoves.addAll(sequence.stream().filter(move -> move.player == null).collect(Collectors.toList())));
    return filterMoves;
  }

  private static boolean isNeighbors(final Game game, final Move first, final
  Move second) {

    if (first == null || second == null) {
      return false;
    }

    int dim = game.getDim();

    boolean flag = false;
    for (int i = first.colIdx - 1; i < first.colIdx + 2; i++) {
      for (int j = first.rowIdx - 1; j < first.rowIdx + 2; j++) {
        // skip non-existent moves
        if (i < dim && j < dim && i > 0 && j > 0) {
          if (second.colIdx == i && second.rowIdx == j) {
            flag = true;
            break;
          }
        }
      }

      if (flag) {
        break;
      }
    }

    return flag;
  }

  /**
   * rule 3
   * determine if a move has occupied neighbors
   */
  private static boolean hasNeighbors(final Game game, final Move move) {
    int dim = game.getDim();
    // available signal
    boolean flag = false;
    for (int i = move.colIdx - 1; i < move.colIdx + 2; i++) {
      for (int j = move.rowIdx - 1; j < move.rowIdx + 2; j++) {
        // skip non-existent moves
        if (i < dim && j < dim && i > 0 && j > 0) {
          if (game.getCellValue(j, i) != null) {
            flag = true;
            break;
          }
        }
      }

      if (flag) {
        break;
      }
    }

    return flag;
  }

  /**
   * find winning lines for both players
   * a winning line can be either closest to win length or have two
   */
  private static List<List<Move>> getWinningSequences(final Game game) {
    final int winLen = game.getWinLength();
    final int opId = game.getFirstPlayerId();
    final List<List<Move>> completeLines = new ArrayList<>();
    List<List<Move>> filteredLines = new ArrayList<>();
    final Map<String, Move[]> lines = game.getAllLinesOfMove(game.getWinLength());
    for (Move[] line : lines.values()) {
      completeLines.addAll(extractCompleteLines(Arrays.asList(line)));
    }

    // complete lines are the line longer than or equal to a winning sequence
    List<List<Move>> sequences = completeLines.stream()
        .filter(wl -> wl.size() >= game.getWinLength()).collect
            (Collectors.toList());
    List<List<Move>> winningLines = new ArrayList<>();
    sequences.forEach(line -> winningLines.addAll(extractWinningLine(line)));

    for (List<Move> line : winningLines) {
      Integer player = getPlayerOfSequence(line);
      // if a opponent sequence is two moves from winning
      // and the two moves are located at head and tail
      // block it
      if (player == opId && getNonEmptyCount(line) == winLen - 2) {
        return Collections.singletonList(line);
      }
    }

    // lines grouped by player
    Map<Integer, List<List<Move>>> groupedByPlayer = winningLines.stream().collect
        (Collectors.groupingByConcurrent(AIMoveChooser::getPlayerOfSequence));

    // find lines with max length, then put them into filterdLines
    groupedByPlayer.values().forEach(seqs -> {
      Map<Integer, List<List<Move>>> groupedByCount = seqs.stream().collect
          (Collectors.groupingByConcurrent(List::size));
      Integer maxSize = groupedByCount.keySet().stream().max(Comparator
          .comparingInt(Integer::intValue)).get();

      filteredLines.addAll(groupedByCount.get(maxSize));
    });
    return filteredLines;
  }

  private static Integer getPlayerOfSequence(List<Move> line) {
    Integer player = null;
    for (Move move : line) {
      if (move.player != null) {
        player = move.player;
        break;
      }
    }

    return player;
  }

  /**
   * get non empty moves count of a line
   */
  private static int getNonEmptyCount(List<Move> line) {
    int count = 0;

    for (Move move : line) {
      if (move.player != null) {
        count++;
      }
    }

    return count;
  }

  /**
   * extract the longest lines from a line
   */
  private static List<List<Move>> extractWinningLine(final List<Move> line) {

    List<List<Move>> lines = extractSequences(line);
    List<List<Move>> newLines = new ArrayList<>();
    for (int i = 0; i < lines.size(); i++) {
      List<Move> newLine = new ArrayList<>();
      List<Move> curLine = lines.get(i);
      // skip empty lines
      if (curLine.get(0).player == null) {
        continue;
      }

      newLines.add(newLine);

      if (i - 1 >= 0) {
        // add the empty move before current line
        List<Move> prev = lines.get(i - 1);
        newLine.add(prev.get(prev.size() - 1));
      }

      newLine.addAll(curLine);

      if (i + 1 < lines.size()) {
        // if there is next line
        // add the first empty move of next line
        List<Move> nextNull = lines.get(i + 1);
        newLine.add(nextNull.get(0));
      }

      if (i + 2 < lines.size()) {
        // another non-empty line exists
        // concatenate it to the previous line
        List<Move> nextNull = lines.get(i + 1);
        List<Move> nextLine = lines.get(i + 2);
        int nextNullLen = nextNull.size();
        if (nextNullLen == 1) {
          // only one empty move exists between two non-empty lines
          newLine.addAll(nextLine);
          if (i + 3 < lines.size()) {
            // another empty line exists
            List<Move> next = lines.get(i + 3);
            newLine.add(next.get(0));
          }
        }
      }
    }

    Map<Integer, List<List<Move>>> groupedByLength = newLines.stream().collect
        (Collectors.groupingByConcurrent(List::size));
    Integer maxSize = groupedByLength.keySet().stream().max(Comparator
        .comparingInt(Integer::intValue)).get();

    newLines = groupedByLength.get(maxSize);
    return newLines;
  }

  /**
   * extract all complete lines in a line
   * <p>
   * complete line means a line including empty moves on both sides
   * if the line is an empty line, it will not be included in
   */
  private static List<List<Move>> extractCompleteLines(final List<Move> line) {
    // player of most recent sequence
    Integer prev = null;
    // player of most recent valid player
    Integer prevPlayer = null;
    List<List<Move>> lines = new ArrayList<>();
    List<Move> prevLine = new ArrayList<>();
    List<List<Move>> extractLines = extractSequences(line);
    List<Move> newLine = new ArrayList<>();
    for (int i = 0; i < extractLines.size(); i++) {
      int j = i;
      while (j < extractLines.size()) {
        List<Move> currentLine = extractLines.get(j);
        Integer current = currentLine.get(0).player;
        if (current == null || current.equals(prevPlayer)) {
          // concatenate to previous line
          newLine.addAll(currentLine);
        } else {
          if (prev == null) {
            // concatenate the previous empty line
            newLine = new ArrayList<>(prevLine);
          } else {
            // create a new line
            newLine = new ArrayList<>();
          }
          lines.add(newLine);
          newLine.addAll(currentLine);
          prevPlayer = newLine.get(0).player;
        }
        prev = current;
        prevLine = currentLine;
        j++;
      }
      i = j - 1;
    }

    return lines;
  }

  /**
   * break the line into lines of same player
   */
  private static List<List<Move>> extractSequences(final List<Move>
                                                       line) {
    Integer prev = null;
    List<Move> newLine = null;
    List<List<Move>> tempLines = new ArrayList<>();
    for (int i = 0; i < line.size(); i++) {
      Move move = line.get(i);
      newLine = new ArrayList<>();
      tempLines.add(newLine);
      int j = i;
      if (move.player == null) {
        while (j < line.size() && line.get(j).player == null) {
          newLine.add(line.get(j));
          j++;
        }
      } else {
        prev = move.player;
        while (j < line.size() && line.get(j).player != null && line.get(j)
            .player.equals(prev)) {
          newLine.add(line.get(j));
          j++;
        }
      }
      i = j - 1;
    }

    return tempLines;
  }

  private static Set<Move> findMovesAdjacentToWinningLine(Game game, List<Move> moves,
                                                          ArrayList<ArrayList<Move>> lines) {
    Set<Move> filterMoves = new HashSet<>();
    for (ArrayList<Move> sequence : lines) {
      Move head = null;
      Move tail = null;
      int size = sequence.size();
      if (size > 0) {
        head = sequence.get(0);
        tail = sequence.get(size - 1);
      }

      for (Move move : moves) {
        if (isNeighbors(game, head, move) || isNeighbors(game, tail, move)) {
          filterMoves.add(move);
        }
      }
    }

    return filterMoves;
  }

  private static ArrayList<ArrayList<Move>> getLongestLines(Game game) {
    ArrayList<ArrayList<Move>> allLines = new ArrayList<>();
    final Map<String, Move[]> lines = game.getAllLinesOfMove(game.getWinLength());
    for (Move[] line : lines.values()) {
      ArrayList<Move> newLine = null;
      Integer prevPlayer = null;
      for (Move move : line) {
        Integer player = move.player;

        // current move is empty
        if (player == null) {
          prevPlayer = null;
          continue;
        }

        if (prevPlayer == null || !prevPlayer.equals(player)) {
          // different player
          newLine = new ArrayList<>();
          newLine.add(move);
          prevPlayer = player;
          // put line into list
          allLines.add(newLine);
          continue;
        }

        // same player
        if (prevPlayer.equals(player)) {
          newLine.add(move);
        }
      }
    }

    ArrayList<ArrayList<Move>> filteredLines = new ArrayList<>();
    int maxOfFirst = 0;
    int maxOfOther = 0;
    int masterId = game.getFirstPlayerId();
    int opId = game.getOtherPlayerId();

    // find the max line length for each player
    for (ArrayList<Move> line : allLines) {
      int size = line.size();
      if (size > 0) {
        if (line.get(0).player == masterId) {
          if (size > maxOfFirst) {
            maxOfFirst = size;
          }
        } else {
          if (line.get(0).player == opId) {
            if (size > maxOfOther) {
              maxOfOther = size;
            }
          }
        }
      }
    }
    int finalMaxOfFirst = maxOfFirst;
    int finalMaxOfOther = maxOfOther;
    // filter max length lines
    allLines.forEach(line -> {
      int size = line.size();
      if (size > 0) {
        if (line.get(0).player == masterId) {
          if (size >= finalMaxOfFirst) {
            filteredLines.add(line);
          }
        } else {
          if (line.get(0).player == masterId) {
            if (size >= finalMaxOfOther) {
              filteredLines.add(line);
            }
          }
        }
      }
    });

    return filteredLines;
  }

  Move selectMove(final Game game, final List<Move>
      moves) {
    if (moves.size() > 1 && isRandomChoice()) {
      // If many moves scored equally, choose randomly from among them
      return moves.get(new Random().nextInt(moves.size()));
    } else if (moves.size() > 0) {
      // Return best move
      return moves.get(0);
    } else {
      // No winning move found !!!
      List<Move> emptyMoves = findEmptyMoves(game);
      Collections.shuffle(emptyMoves);
      return emptyMoves.get(0);
    }
  }

  @Override
  public abstract Move findNextMove(final Game game);
}
