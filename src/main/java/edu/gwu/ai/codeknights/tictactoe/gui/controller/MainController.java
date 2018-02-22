package edu.gwu.ai.codeknights.tictactoe.gui.controller;

import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Move;
import edu.gwu.ai.codeknights.tictactoe.gui.Simulator;
import edu.gwu.ai.codeknights.tictactoe.gui.helpers.MainHelper;
import edu.gwu.ai.codeknights.tictactoe.gui.util.API;
import edu.gwu.ai.codeknights.tictactoe.gui.util.Const;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
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
    private BooleanBinding isMasterTurn;

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
                // create a game on the server, fetch the gameId
//                API.getApiService().post();
                API.getApiService().getMoves(String.valueOf(gameId), Integer.MAX_VALUE);
            }
        } else {
            // generate all tge ids for non-EvE-online games
            gameId = new Random().nextInt(10000);
            masterId = new Random().nextInt(10000);
            opId = new Random().nextInt(10000);
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
        Simulator.getPrimaryStage().sizeToScene();
        helper.createGame(gameId, rowLen, colLen, winLen, mode, masterId, opId);
        game = helper.getGame();

        isMasterTurn = new BooleanBinding() {
            @Override
            protected boolean computeValue() {
                return game.getNextPlayer() == helper.getMaster().getId();
            }
        };

        isMasterTurn.addListener((observable, oldValue, newValue) -> {
            if(newValue){
                mTurn.setText(Const.PLAYER_SYMBOL_MASTER);
            }else{
                mTurn.setText(Const.PLAYER_SYMBOL_OPPONENT);
            }
        });
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
                return isMasterTurn.get() && mode == 1;
            }
        };

        for (int i = 0; i < rowLen; i++) {
            for (int j = 0; j < colLen; j++) {
                StringProperty property = new SimpleStringProperty("-");
                Label label = new Label();
                matrix[i][j] = label;
                boardProperties[i][j] = property;
                label.setOnMouseClicked(event -> {
                    // on mouse clicked
                    // only works in PvE
                    if ("-".equals(label.getText()) && isClickable.get()) {
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
        game.setCellValue(row, col, helper.getMaster().getId());
        helper.history.set("["+helper.getMaster().getSymbol()
                +"]["+(row+1)+", "+(col+1)+"]\n"+helper.history.get());
        boardProperties[row][col].set(helper.getMaster().getSymbol());
        boolean isGameOver = game.isGameOver();
        if (!isGameOver) {
            // ai play
            Move aiMove = helper.getOpponent().getMoveChooser
                    ().findNextMove(game);
            game.setCellValue(aiMove.rowIdx, aiMove.colIdx,
                    aiMove.player);
            boardProperties[aiMove.rowIdx][aiMove.colIdx].set(helper
                    .getOpponent()
                    .getSymbol());
            helper.history.set("["+helper.getOpponent().getSymbol()
                    +"]["+(aiMove.rowIdx+1)+", "+(aiMove.colIdx+1)+"]\n"+helper
                    .history.get
                    ());
            isGameOver = game.isGameOver();
            if (isGameOver) {
                // game is over
                mState.setText(game.getBoardStatus());
            }
        } else {
            // game is over
            mState.setText(game.getBoardStatus());
        }
    }

    public Game getGame() {
        return game;
    }
}
