package edu.gwu.ai.codeknights.tictactoe.gui.controller;

import edu.gwu.ai.codeknights.tictactoe.gui.TicTacToe;
import edu.gwu.ai.codeknights.tictactoe.util.API;
import edu.gwu.ai.codeknights.tictactoe.gui.util.FXMLLoadResult;
import edu.gwu.ai.codeknights.tictactoe.gui.util.FXMLUtil;
import edu.gwu.ai.codeknights.tictactoe.util.Const;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author zhiyuan
 */
public class StartController {

    @FXML
    private Button stPVP;

    @FXML
    private Button stPVE;

    @FXML
    private Button stEVP;

    @FXML
    private Button stEVE;

    @FXML
    private Button stOnline;

    @FXML
    private Button stStart;

    @FXML
    private Button stSpectate;

    @FXML
    private TextField stDim;

    @FXML
    private TextField stWinLen;

    @FXML
    private TextField stTeamId;

    @FXML
    private TextField stOpId;

    @FXML
    private TextField stGameId;

    @FXML
    private RadioButton stFirstHome;

    @FXML
    private RadioButton stSecondHome;

    @FXML
    private TextField stUserId;

    @FXML
    private TextField stKey;

    @FXML
    private Text stErr;

    @FXML
    void initialize() {

        stDim.setText("12");
        stWinLen.setText("6");

        stFirstHome.selectedProperty().addListener((observable, oldValue,
                                                    newValue) -> stSecondHome.setSelected(!newValue));

        stSecondHome.selectedProperty().addListener((observable, oldValue,
                                                     newValue) -> stFirstHome.setSelected(!newValue));

        stFirstHome.setSelected(true);
        stSecondHome.setSelected(false);

        stErr.setStyle("-fx-fill: red");

        stDim.textProperty().addListener(observable -> checkDimWinLen());
        stWinLen.textProperty().addListener(observable -> checkDimWinLen());

        stPVP.setOnAction(event -> {
            stPVP.disableProperty().set(true);
            stPVE.disableProperty().set(false);
            stEVP.disableProperty().set(false);
            stEVE.disableProperty().set(false);
            stOnline.disableProperty().set(false);
        });

        stPVE.setOnAction(event -> {
            stPVP.disableProperty().set(false);
            stPVE.disableProperty().set(true);
            stEVP.disableProperty().set(false);
            stEVE.disableProperty().set(false);
            stOnline.disableProperty().set(false);
        });

        stEVP.setOnAction(event -> {
            stPVP.disableProperty().set(false);
            stPVE.disableProperty().set(false);
            stEVP.disableProperty().set(true);
            stEVE.disableProperty().set(false);
            stOnline.disableProperty().set(false);
        });

        stEVE.setOnAction(event -> {
            stPVP.disableProperty().set(false);
            stPVE.disableProperty().set(false);
            stEVP.disableProperty().set(false);
            stEVE.disableProperty().set(true);
            stOnline.disableProperty().set(false);
        });

        stOnline.setOnAction(event -> {
            stPVP.disableProperty().set(false);
            stPVE.disableProperty().set(false);
            stEVP.disableProperty().set(false);
            stEVE.disableProperty().set(false);
            stOnline.disableProperty().set(true);
        });

        stPVE.setDisable(true);
    }

    private boolean checkDimWinLen() {
        try {
            Integer winLen = Integer.parseInt(stWinLen.getText());
            Integer dim = Integer.parseInt(stDim.getText());
            if (dim < winLen) {
                throw new NumberFormatException();
            }
            stErr.setText("");
        } catch (NumberFormatException ex) {
            stErr.setText("dimension must be equal to or larger than win length");
            return false;
        }
        return true;
    }

    @FXML
    void startHandler(ActionEvent event) {
        GameMode mode = getMode();
        if (mode.equals(GameMode.EVE_ONLINE)) {
            toOnline(false);
        } else {
            toMain(0,0,0,true,false);
        }
    }

    @FXML
    void spectateHandler(ActionEvent event) {
        GameMode mode = getMode();
        if (mode.equals(GameMode.EVE_ONLINE)) {
            String gameIdStr = stGameId.getText().trim();
            if("0".equals(gameIdStr)){
                return;
            }
            toOnline(true);
        }
    }

    private void toMain(long gameId, int teamId, int opponentId,boolean isHome,
                        boolean asSpectator) {
        GameMode mode = getMode();
        Stage primaryStage = TicTacToe.getPrimaryStage();
        // load the main scene, then current scene will dismiss
        FXMLLoadResult result = null;
        try {
            Integer dim = Integer.parseInt(stDim.getText());
            Integer winLen = Integer.parseInt(stWinLen.getText());
            if(dim == 0 || winLen == 0){
                return;
            }
            result = FXMLUtil.loadAsNode("fxml/main.fxml");
            FXMLUtil.addStylesheets(result.getNode(), Const.UNIVERSAL_STYLESHEET_URL);
            primaryStage.setScene(new Scene(result.getNode()));
            primaryStage.setResizable(true);
            primaryStage.setOnCloseRequest(event -> System.exit(0));
            MainController controller = (MainController) result.getController();
            controller.setup(gameId, dim, winLen, mode, teamId, opponentId, isHome,
                    asSpectator);
            stStart.setDisable(true);
        } catch (IOException e) {
            e.printStackTrace();
            stErr.setText("Invalid Arguments");
        }
    }

    private void toOnline(boolean asSpectator) {
        String key = stKey.getText().trim();
        String userId = stUserId.getText().trim();
        String teamIdStr = stTeamId.getText().trim();
        String opIdStr = stOpId.getText().trim();
        String gameIdStr = stGameId.getText().trim();

        if(key.isEmpty() || userId.isEmpty()){
            stErr.setText("Invalid Arguments");
            return;
        }
        if(gameIdStr.isEmpty() || teamIdStr.isEmpty() || opIdStr.isEmpty()){
            stErr.setText("Invalid Arguments");
            return;
        }
        API.HEADER_API_KEY_VALUE = key;
        API.HEADER_USER_ID_VALUE = userId;

        try {
            Integer teamId = Integer.parseInt(teamIdStr);
            Integer opponentId = Integer.parseInt(opIdStr);
            Long gameId = Long.parseLong(gameIdStr);
            boolean isHome = getIsHome();
            toMain(gameId, teamId, opponentId, isHome, asSpectator);
        }catch (NumberFormatException ex){
            ex.printStackTrace();
            stErr.setText("Invalid Arguments");
        }
    }

    private boolean getIsHome() {
        return stFirstHome.isSelected();
    }

    private GameMode getMode() {
        if (stPVP.isDisabled()) {
            return GameMode.PVP;
        } else if (stPVE.isDisabled()) {
            return GameMode.PVE;
        } else if (stEVP.isDisabled()) {
            return GameMode.EVP;
        } else if (stEVE.isDisabled()) {
            return GameMode.EVE;
        } else {
            return GameMode.EVE_ONLINE;
        }
    }
}
