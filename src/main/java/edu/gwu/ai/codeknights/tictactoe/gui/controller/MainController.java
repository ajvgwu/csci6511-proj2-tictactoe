package edu.gwu.ai.codeknights.tictactoe.gui.controller;

import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Move;
import edu.gwu.ai.codeknights.tictactoe.gui.TicTacToe;
import edu.gwu.ai.codeknights.tictactoe.gui.helpers.MainHelper;
import edu.gwu.ai.codeknights.tictactoe.gui.util.API;
import edu.gwu.ai.codeknights.tictactoe.gui.util.Player;
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
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.Random;

/**
 * @author zhiyuan
 */
public class MainController {

  private MainHelper helper;

  private Game game;
  private BooleanBinding isClickable;
  private int mode;

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
   * @param masterId id of local primary player
   * @param opId     id of opponent player
   * @param mode     a integer represents one of three game mode: PvE EvE EvE
   *                 online
   * @param gameId   invoke api to get this value when gameId is 0 and mode
   *                 is 3, if gameId is not 0, then the game was created by
   *                 other team
   * @param rowLen   number of rows
   * @param colLen   number columns
   * @param winLen   length of a winning line
   */
  public void setup(int mode, int masterId, long gameId, int rowLen, int colLen, int winLen, int opId) {
    if (mode == 3) {
      if (gameId == 0) {
        // TODO create a game on the server, fetch the gameId
//                API.getApiService().post();
//                API.getApiService().getMoves(String.valueOf(gameId), Integer.MAX_VALUE);
      } else {
        // hook on a existing game
      }
    } else {
      // generate all tge ids for non-EvE-online games
      gameId = new Random().nextInt(10000);
      masterId = 10;
      opId = 20;
    }

    mMasterId.setText(String.valueOf(masterId));
    mOpId.setText(String.valueOf(opId));
    mGameId.setText(String.valueOf(gameId));
    mRowLen.setText(String.valueOf(rowLen));
    mColLen.setText(String.valueOf(colLen));
    mWinLen.setText(String.valueOf(winLen));

    this.mode = mode;

    // add matrix to main panel
    buildBoard(rowLen, colLen);
    TicTacToe.getPrimaryStage().sizeToScene();
    helper.createGame(gameId, rowLen, colLen, winLen, mode, masterId, opId);
    game = helper.getGame();
    if (mode == 1) {
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
        return helper.getNextPlayer() == helper.getMaster() && mode == 1
            && !game.isGameOver();
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
    game.setCellValue(row, col, game.getNextPlayer());
    helper.history.set("[" + helper.getMaster().getSymbol()
        + "][" + (row + 1) + ", " + (col + 1) + "]\n" + helper.history.get());
    boardProperties[row][col].set(helper.getMaster().getSymbol());
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
    Move move = player.getMoveChooser().findNextMove(game);
    game.setCellValue(move.rowIdx, move.colIdx, player.getId());
    long endMs = System.currentTimeMillis();
    boardProperties[move.rowIdx][move.colIdx].set(player.getSymbol());
    helper.history.set(String.format("[%s][%d, %d]-AI-%dms\n%s", player.getSymbol(), move.rowIdx + 1, move.colIdx + 1,endMs-startMs, helper.history.get()));
    mState.setText(game.getBoardStatus());
    if (game.isGameOver()) {
      mNext.setDisable(true);
    }
  }

  public Game getGame() {
    return game;
  }
}
