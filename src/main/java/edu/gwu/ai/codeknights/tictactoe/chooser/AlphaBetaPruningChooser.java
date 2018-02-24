package edu.gwu.ai.codeknights.tictactoe.chooser;

import edu.gwu.ai.codeknights.tictactoe.core.Move;
import edu.gwu.ai.codeknights.tictactoe.core.exception.DimensionException;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.exception.StateException;
import org.pmw.tinylog.Logger;

import java.util.List;

public abstract class AlphaBetaPruningChooser extends AIMoveChooser {

    protected long alphabetapruning(final Game game, final int player,
                                    long alpha, long beta, final int level) {
        Long bestScore = null;
        long upperBound = game.getDim()*game.getDim();
        // Check for terminal state
        if (game.isGameOver()) {
            if (game.didPlayerWin(player)) {
                bestScore =  upperBound - level;
            } else if (game.didAnyPlayerWin()) {
                bestScore = -upperBound + level;
            }else{
                bestScore = 0L;
            }
            return bestScore;
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
        List<Move> moves = findPossibleMoves(game);

        try {
            final Game newGame = game.getCopy();
            final int curPlayer = game.getNextPlayer();
            if(moves.size() == 0){
                int leftLevel = findEmptyMoves(game).size()/2;
                if (player == curPlayer) {
                    alpha = 10-level-leftLevel;

                } else {
                    beta = -10 + leftLevel + level;
                }
            }

            for (final Move move : moves) {
                newGame.setCellValue(move.rowIdx, move.colIdx, curPlayer);
                final long curScore = alphabetapruning(newGame, player,
                    alpha, beta, level + 1);
                if (player == curPlayer) {
                    if (curScore > alpha) {
                        alpha = curScore;
                    }
                } else {
                    if (curScore < beta) {
                        beta = curScore;
                    }
                }
                if (alpha >= beta) {
                    break;
                }

                // reset the move
                newGame.setCellValue(move.rowIdx, move.colIdx, null);
            }
        } catch (DimensionException | StateException e) {
            Logger.error(e, "could not copy game state");
        }

        // if the play is next player
        // choose alpha
        if (player == game.getNextPlayer()) {
            bestScore = alpha;
        } else {
            bestScore = beta;
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
    public abstract Move findNextMove(final Game game);
}
