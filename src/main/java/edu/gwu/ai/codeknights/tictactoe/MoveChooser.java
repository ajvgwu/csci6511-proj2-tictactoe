package edu.gwu.ai.codeknights.tictactoe;

import java.util.*;
import java.util.stream.Collectors;

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
        final int curPlayer = game.getNextPlayer();
        for (int rowIdx = 0; rowIdx < dim; rowIdx++) {
            for (int colIdx = 0; colIdx < dim; colIdx++) {
                final Integer value = game.getCellValue(rowIdx, colIdx);
                if (value == null) {
                    moves.add(new Game.Move(rowIdx, colIdx, curPlayer, null));
                }
            }
        }

        // rule 1: don't consider the if it is adjacent to none
        moves = moves.stream().filter(move -> hasNeighbors(game, move)).collect
                (Collectors.toList());
        return moves;
    }

    /**
     * determine if a move has occupied neighbors
     */
    private boolean hasNeighbors(final Game game, final Game.Move move) {
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
