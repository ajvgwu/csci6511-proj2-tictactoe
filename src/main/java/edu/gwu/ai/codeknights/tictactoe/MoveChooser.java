package edu.gwu.ai.codeknights.tictactoe;

import java.util.*;

public abstract class MoveChooser {

    private boolean randomChoice;
    private final Map<Long, Integer> hashScoreMap;

    public MoveChooser() {
        randomChoice = false;
        hashScoreMap = new HashMap<>();
    }

    public boolean isRandomChoice() {
        return randomChoice;
    }

    public void setRandomChoice(final boolean randomChoice) {
        this.randomChoice = randomChoice;
    }

    protected Map<Long, Integer> getHashScoreMap() {
        return hashScoreMap;
    }

    protected List<Game.Move> findPossibleMoves(final Game game) {
        List<Game.Move> moves = new ArrayList<>();
        final int dim = game.getDim();
        final int prevPlayer = game.getPrevPlayer();
        final int curPlayer = game.getNextPlayer();
        for (int rowIdx = 0; rowIdx < dim; rowIdx++) {
            for (int colIdx = 0; colIdx < dim; colIdx++) {
                final Integer value = game.getCellValue(rowIdx, colIdx);
                if (value == null) {
                  // rule 1: if there's a chance to win, take it immediately
                  game.setCellValue(rowIdx, colIdx, curPlayer);
                  final boolean didWin = game.didPlayerWin(curPlayer);
                  game.setCellValue(rowIdx, colIdx, null);
                  if (didWin) {
                    return Collections.singletonList(new Game.Move(rowIdx, colIdx, curPlayer, null));
                  }

                  // rule 2: if the other player can win, block it immediately
                  game.setCellValue(rowIdx, colIdx, prevPlayer);
                  final boolean didLose = game.didPlayerWin(prevPlayer);
                  game.setCellValue(rowIdx, colIdx, null);
                  if (didLose) {
                    return Collections.singletonList(new Game.Move(rowIdx, colIdx, curPlayer, null));
                  }

                  // rule 3: only consider moves that are adjacent to non-empty cells
                  if (hasNeighbors(game, rowIdx, colIdx)) {
                    moves.add(new Game.Move(rowIdx, colIdx, curPlayer, null));
                  }
                }
            }
        }
        return moves;
    }

    /**
     * determine if a move has occupied neighbors
     */
    private boolean hasNeighbors(final Game game, final int rowIdx, final int colIdx) {
        int dim = game.getDim();
        // available signal
        boolean flag = false;
        for (int i = colIdx - 1; i < colIdx + 2; i++) {
            for (int j = rowIdx - 1; j < rowIdx + 2; j++) {
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

    protected Game.Move selectMove(final List<Game.Move> moves) {
        if (moves.size() > 1 && isRandomChoice()) {
            // If many moves scored equally, choose randomly from among them
            return moves.get(new Random().nextInt(moves.size()));
        } else if (moves.size() > 0) {
            // Return best move
            return moves.get(0);
        } else {
            // No move found !!!
            return null;
        }
    }

    public abstract Game.Move findBestMove(final Game game);
}
