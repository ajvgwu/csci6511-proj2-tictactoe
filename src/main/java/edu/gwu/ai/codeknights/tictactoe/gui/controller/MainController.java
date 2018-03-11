package edu.gwu.ai.codeknights.tictactoe.gui.controller;

import java.util.concurrent.Executors;

import edu.gwu.ai.codeknights.tictactoe.chooser.AbstractOnlineChooser;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import edu.gwu.ai.codeknights.tictactoe.Spectator;
import edu.gwu.ai.codeknights.tictactoe.gui.helpers.MainHelper;
import edu.gwu.ai.codeknights.tictactoe.gui.util.Const;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * @author zhiyuan
 */
public class MainController {

    private MainHelper helper;

    private Game game;
    private BooleanProperty isGameOver;

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
    private VBox mBoardBox;

    @FXML
    private TextArea mHistory;

    @FXML
    void initialize() {
        helper = new MainHelper();
        isGameOver = new SimpleBooleanProperty(false);
        mHistory.textProperty().bind(helper.history);
    }

    /**
     * the setup method initialize the game for this main panel
     * must be invoked after the main panel being loaded
     *
     * @param gameId   invoke api to get this value when gameId is 0 and mode is 3, if
     *                 gameId is not 0, then the game was created by other team
     * @param dim      number of rows and columns
     * @param winLen   length of a winning line
     * @param masterId id of local primary player
     * @param opId     id of opponent player
     */
    public void setup(long gameId, final int dim, final int winLen, int masterId, int opId, boolean isHome) {
        mPlayerStatus.setText("Spectating");
        helper.createLocalGame(gameId, dim, winLen, masterId, opId, isHome);
        game = helper.getGame();
        // add matrix to main panel
        buildBoard(dim, dim);
        Spectator.getPrimaryStage().sizeToScene();
        refresh();
        // start spectating automatically
        Task task = new Task() {
            @Override
            protected Object call() {
                while (!isGameOver.get()) {
                    makeMove();
                }
                return true;
            }
        };

        Executors.newSingleThreadExecutor().execute(task);
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

        for (int i = 0; i < rowLen; i++) {
            for (int j = 0; j < colLen; j++) {
                final String blank = String.valueOf(Const.BLANK_SPACE_CHAR);
                final StringProperty property = new SimpleStringProperty(blank);
                final Label label = new Label();
                matrix[i][j] = label;
                boardProperties[i][j] = property;
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
        AbstractOnlineChooser.tryFastForward(game);

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
