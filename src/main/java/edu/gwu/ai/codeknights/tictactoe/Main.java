package edu.gwu.ai.codeknights.tictactoe;

import edu.gwu.ai.codeknights.tictactoe.chooser.MoveChooser;
import edu.gwu.ai.codeknights.tictactoe.chooser.ParallelAlphaBetaPruningChooser;
import org.pmw.tinylog.Logger;

public class Main {

    public static void main(final String[] args) throws DimensionException, StateException{
        // Default values
        int rowLen = 5;
        int winLength = 3;

        // Create the game
        final Integer[][] board = new Integer[rowLen][rowLen];
        final Game game = new Game(rowLen, winLength, board);
        // Play a complete game, starting from the current state
        playGame(game, false);
    }

    public static void playGame(final Game game, final boolean randomize){
        Logger.info("dim={}, winLength={}, hash={}", game.getDim(), game.getWinLength(), game.getBoardHash());
        boolean isGameOver = game.isGameOver();
        while (!isGameOver) {
            final MoveChooser moveChooser = new ParallelAlphaBetaPruningChooser();
            moveChooser.setRandomChoice(randomize);
            final long startMs = System.currentTimeMillis();
            final Move bestMove = moveChooser.findBestMove(game);
            game.setCellValue(bestMove.rowIdx, bestMove.colIdx, bestMove.player);
            final long endMs = System.currentTimeMillis();
            final double timeSec = (double) (endMs - startMs) / 1000.0;
            Logger.info("Found best move in {} sec: {}\n{}\n", timeSec, bestMove.toString(), game.toString());
            isGameOver = game.isGameOver();
            Logger.info("Is game over?   {}", isGameOver);
        }
        Logger.info("Did anyone win? {}", game.didAnyPlayerWin());
        Logger.info("Who won?        {}={}, {}={}", Game.FIRST_PLAYER_CHAR, game.didFirstPlayerWin(), Game.OTHER_PLAYER_CHAR, game.didOtherPlayerWin());
    }
}
