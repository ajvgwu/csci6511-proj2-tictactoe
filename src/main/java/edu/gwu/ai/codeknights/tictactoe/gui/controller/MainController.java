package edu.gwu.ai.codeknights.tictactoe.gui.controller;

import edu.gwu.ai.codeknights.tictactoe.chooser.AbstractOnlineChooser;
import edu.gwu.ai.codeknights.tictactoe.chooser.StupidMoveChooser;
import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import edu.gwu.ai.codeknights.tictactoe.gui.TicTacToe;
import edu.gwu.ai.codeknights.tictactoe.gui.helpers.MainHelper;
import edu.gwu.ai.codeknights.tictactoe.util.Const;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Random;
import java.util.concurrent.Executors;

/**
 * @author zhiyuan
 */
public class MainController {

    private MainHelper helper;

    private Game game;
    private BooleanBinding isClickable;
    private BooleanProperty isPlayerNext;
    private BooleanProperty isGameOver;
    private BooleanProperty isAINext;
    private GameMode mode;

    private StringProperty[][] boardProperties;
    private Label[][] matrix;

    @FXML
    private Text mGameId;

    @FXML
    private Text mRowLen;

    @FXML
    private Text mColLen;

    @FXML
    private Text mWinLen;

    @FXML
    private Text mMasterId;

    @FXML
    private Text mOpId;

    @FXML
    private Text mTurn;

    @FXML
    private Text mState;

    @FXML
    private Text mPlayerStatus;

    @FXML
    private Button mNext;

    @FXML
    private VBox mBoardBox;

    @FXML
    private TextArea mHistory;

    @FXML
    private TextField mRow;

    @FXML
    private TextField mCol;

    @FXML
    private Button mSubmit;

    @FXML
    void initialize() {
        helper = new MainHelper();
        isPlayerNext = new SimpleBooleanProperty(false);
        isAINext = new SimpleBooleanProperty(false);
        isGameOver = new SimpleBooleanProperty(false);
        mHistory.textProperty().bind(helper.history);
        mSubmit.disableProperty().bind(isPlayerNext.not());
        mNext.disableProperty().bind(isAINext.not());
        mSubmit.setOnAction(event -> {
            if (!"".equals(mRow.getText().trim())) {
                if (!"".equals(mCol.getText().trim())) {
                    Integer row = Integer.parseInt(mRow.getText());
                    Integer col = Integer.parseInt(mCol.getText());
                    pve(row, col);
                }
            }
        });
    }

    /**
     * the setup method initialize the game for this main panel
     * must be invoked after the main panel being loaded
     *
     * @param gameId   invoke api to get this value when gameId is 0 and mode is 3, if
     *                 gameId is not 0, then the game was created by other team
     * @param dim      number of rows and columns
     * @param winLen   length of a winning line
     * @param mode     a integer represents one of game modes
     * @param masterId id of local primary player
     * @param opId     id of opponent player
     */
    public void setup(long gameId, final int dim, final int winLen, final
    GameMode mode, int masterId, int opId, boolean isHome, boolean asSpectator) {
        mPlayerStatus.setText(asSpectator ? "Spectating" : "Playing");
        if (!asSpectator) {
            // play game, not as spectator
            if (GameMode.EVE_ONLINE.equals(mode)) {
                if (gameId == 0) {
                    String gameType = Const.API_GAMETYPE_DEFAULT;
                    gameId = helper.createOnelineGame(masterId, opId,
                            gameType, dim, winLen);
                }
            } else {
                // generate all ids for non-EvE-online games
                gameId = new Random().nextInt(1000);
                masterId = 10;
                opId = 20;
            }
        }

        helper.createLocalGame(gameId, dim, winLen, mode, masterId, opId, isHome,
                asSpectator);
        game = helper.getGame();
        this.mode = mode;

        if (mode.equals(GameMode.EVP) || mode.equals(GameMode.EVE_ONLINE) || mode.equals(GameMode.EVE)) {
            isPlayerNext.set(false);
            isAINext.set(true);
        } else {
            isPlayerNext.set(true);
            isAINext.set(false);
        }

        // add matrix to main panel
        buildBoard(dim, dim);
        TicTacToe.getPrimaryStage().sizeToScene();
        refresh();
        if(asSpectator){
            // start spectating automatically
            nextHandler(null);
        }
    }

    /**
     * build a grid pane based on label matrix
     */
    private void buildBoard(final int rowLen, final int colLen) {
        // add matrix to main panel
        final GridPane boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.CENTER);
        boardGrid.setVgap(10);
        boardGrid.setHgap(10);
        GridPane.setHalignment(boardGrid, HPos.CENTER);
        GridPane.setValignment(boardGrid, VPos.CENTER);
        final Label[][] matrix = getLabelMatrix(rowLen, colLen);
        for (int i = 0; i < rowLen; i++) {
            for (int j = 0; j < colLen; j++) {
                boardGrid.add(matrix[i][j], j, i);
            }
        }
        mBoardBox.getChildren().add(boardGrid);
    }

    /**
     * return a label matrix of the size as board
     */
    private Label[][] getLabelMatrix(final int rowLen, final int colLen) {

        if (matrix != null) {
            return matrix;
        }

        boardProperties = new StringProperty[rowLen][colLen];
        matrix = new Label[rowLen][colLen];

        isClickable = new BooleanBinding() {
            @Override
            protected boolean computeValue() {
                final Player curPlayer = helper.getNextPlayer();
                return curPlayer.getChooser() instanceof StupidMoveChooser && !isGameOver.get();
            }
        };

        for (int i = 0; i < rowLen; i++) {
            for (int j = 0; j < colLen; j++) {
                final String blank = String.valueOf(Const.BLANK_SPACE_CHAR);
                final StringProperty property = new SimpleStringProperty(blank);
                final Label label = new Label();
                matrix[i][j] = label;
                boardProperties[i][j] = property;
                label.setOnMouseClicked(event -> {
                    // on mouse clicked
                    // only works in PvE
                    if (blank.equals(label.getText()) && isClickable.get()) {
                        final int row = (int) label.getProperties().get("row");
                        final int col = (int) label.getProperties().get("col");
                        pve(row, col);
                    }
                });
                label.getProperties().put("row", i);
                label.getProperties().put("col", j);
                label.setPrefHeight(20);
                label.setPrefWidth(20);
                label.setAlignment(Pos.CENTER);
                label.textProperty().bindBidirectional(property);
            }
        }

        return matrix;
    }

    private void pve(final int row, final int col) {
        if (!game.getBoard().getCell(row, col).isEmpty()) {
            return;
        }
        // master play
        final Player nextPlayer = game.getNextPlayer();
        game.playInCell(row, col, nextPlayer);
        helper.history.set("[" + String.valueOf(nextPlayer.getMarker())
                + "][" + row + ", " + col + "]\n" + helper.history.get());
        refresh();
        if (!isGameOver.get()) {
            // ai opponent make a move
            isAINext.set(true);
            isPlayerNext.set(false);
            makeMove();
            isAINext.set(false);
            isPlayerNext.set(true);
        }
    }

    @FXML
    void nextHandler(ActionEvent event) {
        Task task = new Task() {
            @Override
            protected Object call() {
                while (!(game.isGameOver() || isPlayerNext.get() || !isAINext.get())) {
                    isAINext.set(false);
                    makeMove();
                    if (mode.equals(GameMode.EVE_ONLINE) || mode.equals(GameMode.EVE)) {
                        isPlayerNext.set(false);
                        isAINext.set(true);
                    } else {
                        isPlayerNext.set(true);
                        isAINext.set(false);
                    }
                }
                return true;
            }
        };

        Executors.newSingleThreadExecutor().execute(task);
    }

    @FXML
    void refreshHandler(ActionEvent event) {
        refresh();
    }

    private void makeMove() {
        final Player player = helper.getNextPlayer();
        final long startMs = System.currentTimeMillis();
        final Cell cell = player.chooseCell(game);
        game.playInCell(cell, player);
        final long endMs = System.currentTimeMillis();
        helper.history.set(String.format("[%s][%d, %d]-AI-%dms\n%s",
                String.valueOf(player.getMarker()),
                cell.getRowIdx(),
                cell.getColIdx(),
                endMs - startMs,
                helper.history.get()));
        refresh();
    }

    private void refresh() {
        if (mode.equals(GameMode.EVE_ONLINE)) {
            AbstractOnlineChooser.tryFastForward(game);
        }

        Platform.runLater(() -> {
            for (int i = 0; i < game.getDim(); i++) {
                for (int j = 0; j < game.getDim(); j++) {
                    Player player = game.getBoard().getCell(i, j).getPlayer();
                    if (player != null) {
                        String marker = String.valueOf(player.getMarker());
                        boardProperties[i][j].set(marker);
                    }
                }
            }
        });

        mMasterId.setText(String.valueOf(game.getPlayer1().getId()));
        mOpId.setText(String.valueOf(game.getPlayer2().getId()));
        mGameId.setText(String.valueOf(game.getGameId()));
        mRowLen.setText(String.valueOf(game.getDim()));
        mColLen.setText(String.valueOf(game.getDim()));
        mWinLen.setText(String.valueOf(game.getWinLength()));
        mState.setText(game.getGameStatus());
        mTurn.setText(String.valueOf(game.getNextPlayer().getMarker()));
        isGameOver.set(game.isGameOver());
    }

    public Game getGame() {
        return game;
    }
}
