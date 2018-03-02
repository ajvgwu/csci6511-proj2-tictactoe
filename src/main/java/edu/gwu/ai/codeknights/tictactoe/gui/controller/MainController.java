package edu.gwu.ai.codeknights.tictactoe.gui.controller;

import java.util.Random;

import org.pmw.tinylog.Logger;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import edu.gwu.ai.codeknights.tictactoe.gui.TicTacToe;
import edu.gwu.ai.codeknights.tictactoe.gui.helpers.MainHelper;
import edu.gwu.ai.codeknights.tictactoe.util.Const;
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
  private BooleanBinding isClickable;
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
  private Button mNext;

  @FXML
  private VBox mBoardBox;

  @FXML
  private TextArea mHistory;

  @FXML
  void initialize() {
    helper = new MainHelper();

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
   * @param mode     a integer represents one of three game mode: PvE EvE EvE online
   * @param masterId id of local primary player
   * @param opId     id of opponent player
   */
  public void setup(long gameId, int dim, int winLen, GameMode mode, int masterId, int opId) {
    if (GameMode.EVE_ONLINE.equals(mode)) {
      if (gameId == 0) {
        // TODO: create a game on the server, fetch the gameId
        Logger.error("TODO: create a game on the server, fetch the gameId");
//                API.getApiService().post();
//                API.getApiService().getMoves(String.valueOf(gameId), Integer.MAX_VALUE);
      } else {
        // TODO: hook on a existing game
        Logger.error("TODO: hook on a existing game");
      }
    } else {
      // generate all ids for non-EvE-online games
      gameId = new Random().nextInt(10000);
      masterId = 10;
      opId = 20;
    }

    mMasterId.setText(String.valueOf(masterId));
    mOpId.setText(String.valueOf(opId));
    mGameId.setText(String.valueOf(gameId));
    mRowLen.setText(String.valueOf(dim));
    mColLen.setText(String.valueOf(dim));
    mWinLen.setText(String.valueOf(winLen));

    this.mode = mode;

    // add matrix to main panel
    buildBoard(dim, dim);
    TicTacToe.getPrimaryStage().sizeToScene();
    helper.createGame(gameId, dim, winLen, mode, masterId, opId);
    game = helper.getGame();
    if (GameMode.PVE.equals(mode)) {
      mNext.setDisable(true);
    }
  }

  /**
   * build a grid pane based on label matrix
   */
  private void buildBoard(int rowLen, int colLen) {
    // add matrix to main panel
    GridPane boardGrid = new GridPane();
    boardGrid.setAlignment(Pos.CENTER);
    boardGrid.setVgap(20);
    boardGrid.setHgap(20);
    GridPane.setHalignment(boardGrid, HPos.CENTER);
    GridPane.setValignment(boardGrid, VPos.CENTER);
    Label[][] matrix = getLabelMatrix(rowLen, colLen);
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
  private Label[][] getLabelMatrix(int rowLen, int colLen) {

    if (matrix != null) {
      return matrix;
    }

    boardProperties = new StringProperty[rowLen][colLen];
    matrix = new Label[rowLen][colLen];

    isClickable = new BooleanBinding() {
      @Override
      protected boolean computeValue() {
        final Player master = helper.getMaster();
        final Player curPlayer = helper.getNextPlayer();
        return curPlayer.getId() == master.getId() && GameMode.PVE.equals(mode) && !game.isGameOver();
      }
    };

    for (int i = 0; i < rowLen; i++) {
      for (int j = 0; j < colLen; j++) {
        String blank = String.valueOf(Const.BLANK_SPACE_CHAR);
        StringProperty property = new SimpleStringProperty(blank);
        Label label = new Label();
        matrix[i][j] = label;
        boardProperties[i][j] = property;
        label.setOnMouseClicked(event -> {
          // on mouse clicked
          // only works in PvE
          if (blank.equals(label.getText()) && isClickable.get()) {
            int row = (int) label.getProperties().get("row");
            int col = (int) label.getProperties().get("col");
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

  private void pve(int row, int col) {
    // master play
    game.playInCell(row, col, game.getNextPlayer());
    helper.history.set("[" + String.valueOf(helper.getMaster().getMarker())
        + "][" + (row + 1) + ", " + (col + 1) + "]\n" + helper.history.get());
    boardProperties[row][col].set(String.valueOf(helper.getMaster().getMarker()));
    mState.setText(game.getBoardStatus());
    boolean isGameOver = game.isGameOver();
    if (!isGameOver) {
      // ai opponent make a move
      makeMove();
    }
  }

  @FXML
  void nextHandler(ActionEvent event) {
    makeMove();
  }

  private void makeMove() {
    Player player = helper.getNextPlayer();
    long startMs = System.currentTimeMillis();
    Cell cell = player.chooseCell(game);
    game.playInCell(cell, player);
    long endMs = System.currentTimeMillis();
    boardProperties[cell.getRowIdx()][cell.getColIdx()].set(String.valueOf(player.getMarker()));
    helper.history.set(String.format("[%s][%d, %d]-AI-%dms\n%s",
      String.valueOf(player.getMarker()),
      cell.getRowIdx() + 1,
      cell.getColIdx() + 1,
      endMs - startMs,
      helper.history.get()));
    mState.setText(game.getBoardStatus());
    if (game.isGameOver()) {
      mNext.setDisable(true);
    }
  }

  public Game getGame() {
    return game;
  }
}
