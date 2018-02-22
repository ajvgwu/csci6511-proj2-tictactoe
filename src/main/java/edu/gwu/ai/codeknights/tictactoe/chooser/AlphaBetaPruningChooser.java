package edu.gwu.ai.codeknights.tictactoe.chooser;

import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Move;

import java.util.ArrayList;
import java.util.List;

public class AlphaBetaPruningChooser extends AIMoveChooser {

    protected long alphabetapruning(final Game game, final int player,
                                    double alpha, double beta, final int
                                           level) {
        Long bestScore = null;
        // Check for terminal state
        if (game.isGameOver()) {
            // return game.getScore(player);
            if (game.didPlayerWin(player)) {
                return 10 - level;
            } else if (game.didAnyPlayerWin()) {
                return -10 + level;
            }
            return 0;
        }

        // Check if we've already solved this state
        synchronized (getHashScoreMap()) {
            final long hash = game.getBoardHash();
            final Long precomputedScore = getHashScoreMap().get(hash);
            if (precomputedScore != null) {
                return precomputedScore;
            }
        }

        // Find move with the best score
        final List<Move> moves = findPossibleMoves(game);
        for (final Move move : moves) {
            final Game newGame = game.getCopy();
            newGame.setCellValue(move.rowIdx, move.colIdx, move.player);
            final long curScore = alphabetapruning(newGame, player,
                    alpha, beta, level + 1);
            if (player == move.player) {
                alpha = curScore;
            } else {
                beta = curScore;
            }

            if (alpha >= beta) {
                break;
            }
        }

        // if the play is next player
        // choose alpha
        if (player == game.getNextPlayer()) {
            bestScore = (long) alpha;
        } else {
            bestScore = (long) beta;
        }

        // Update hashScoreMap
        synchronized (getHashScoreMap()) {
            final long hash = game.getBoardHash();
            getHashScoreMap().put(hash, bestScore);
        }

        // Return best score
        return bestScore;
    }

    @Override
    public Move findNextMove(final Game game) {
        if (game.isGameOver()) {
            return null;
        }
        final int numFirst = game.countFirstPlayer();
        final int numOther = game.countOtherPlayer();
        final int curPlayer = game.getNextPlayer();
        if (numFirst + numOther == 0) {
            final int dim = game.getRowLen();
            final int center = (int) (dim / 2);
            return new Move(center, center, curPlayer, null);
        }
        final List<Move> moves = findPossibleMoves(game);
        Long bestScore = null;
        final List<Move> bestMoves = new ArrayList<>();
        for (final Move move : moves) {
            final Game newGame = game.getCopy();
            newGame.setCellValue(move.rowIdx, move.colIdx, curPlayer);
            final long curScore = alphabetapruning(newGame, curPlayer,
                    Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                    0);
            move.setScore(curScore);
            if (bestScore == null || curScore > bestScore) {
                bestScore = curScore;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (curScore == bestScore) {
                bestMoves.add(move);
            }
        }

        // Return result
        return selectMove(game, bestMoves);
    }
}
