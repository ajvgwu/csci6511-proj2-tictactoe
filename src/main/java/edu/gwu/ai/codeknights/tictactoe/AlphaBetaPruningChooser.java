package edu.gwu.ai.codeknights.tictactoe;

import org.pmw.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

public class AlphaBetaPruningChooser extends MoveChooser {

        protected int alphabetapruning(final Game game, final int player,
                                       double alpha, double beta, final int
                                               level) {

            Integer bestScore = null;
            // Check for terminal state
            if (game.isGameOver()) {
                // return game.getScore(player);
                if (game.didPlayerWin(player)) {
                    return 10 - level;
                }
                else if (game.didAnyPlayerWin()) {
                    return -10 + level;
                }
                return 0;
            }

            // Check if we've already solved this state
            synchronized (getHashScoreMap()) {
                final long hash = game.getBoardHash();
                final Integer precomputedScore = getHashScoreMap().get(hash);
                if (precomputedScore != null) {
                    return precomputedScore;
                }
            }

            // Find move with the best score
            final List<Game.Move> moves = findPossibleMoves(game);
            for (final Game.Move move : moves) {
                try {
                    final Game newGame = game.getCopy();
                    newGame.setCellValue(move.rowIdx, move.colIdx, move.player);
                    final int curScore = alphabetapruning(newGame, player,
                            alpha,beta, level + 1);
                    if (player == move.player) {
                        if (bestScore == null || curScore > alpha) {
                            alpha = curScore;
                            bestScore = (int)alpha;
                        }
                    }
                    else {
                        if (bestScore == null || curScore < beta) {
                            beta = curScore;
                            bestScore = (int)beta;
                        }
                    }

                    if(alpha >= beta){
                        break;
                    }
                }
                catch (DimensionException | StateException e) {
                    Logger.error(e, "could not copy game state");
                }
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
        public Game.Move findBestMove(final Game game) {
            if (game.isGameOver()) {
                return null;
            }
            final int numFirst = game.countFirstPlayer();
            final int numOther = game.countOtherPlayer();
            final int curPlayer = game.getNextPlayer();
            if (numFirst + numOther == 0) {
                final int dim = game.getDim();
                final int center = (int) (dim / 2);
                //return new Game.Move(center, center, curPlayer, null);
            }
            final List<Game.Move> moves = findPossibleMoves(game);
            Integer bestScore = null;
            final List<Game.Move> bestMoves = new ArrayList<>();
            for (final Game.Move move : moves) {
                try {
                    final Game newGame = game.getCopy();
                    newGame.setCellValue(move.rowIdx, move.colIdx, curPlayer);
                    final int curScore = alphabetapruning(newGame, curPlayer,
                            Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                            0);
                    move.setScore(curScore);
                    if (bestScore == null || curScore > bestScore) {
                        bestScore = curScore;
                        bestMoves.clear();
                        bestMoves.add(move);
                    }
                    else if (bestScore != null && curScore == bestScore) {
                        bestMoves.add(move);
                    }
                }
                catch (DimensionException | StateException e) {
                    Logger.error(e, "could not copy game state");
                }
            }

            // Return result
            return selectMove(bestMoves);
        }
    }
