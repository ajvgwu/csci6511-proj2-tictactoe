package edu.gwu.ai.codeknights.tictactoe.gui.controller;

import java.io.IOException;

import edu.gwu.ai.codeknights.tictactoe.Spectator;
import edu.gwu.ai.codeknights.tictactoe.gui.util.API;
import edu.gwu.ai.codeknights.tictactoe.gui.util.FXMLLoadResult;
import edu.gwu.ai.codeknights.tictactoe.gui.util.FXMLUtil;
import edu.gwu.ai.codeknights.tictactoe.gui.util.Const;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author zhiyuan
 */
public class StartController {

    @FXML
    private Button stOnline;

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

        stFirstHome.selectedProperty().addListener((observable, oldValue,
                                                    newValue) -> stSecondHome.setSelected(!newValue));

        stSecondHome.selectedProperty().addListener((observable, oldValue,
                                                     newValue) -> stFirstHome.setSelected(!newValue));
        stFirstHome.setSelected(true);
        stSecondHome.setSelected(false);
        stErr.setStyle("-fx-fill: red");
        stDim.textProperty().addListener(observable -> checkDimWinLen());
        stWinLen.textProperty().addListener(observable -> checkDimWinLen());

        stOnline.setDisable(true);
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
        Stage primaryStage = Spectator.getPrimaryStage();
        // load the main scene, then current scene will dismiss
        FXMLLoadResult result = null;
        try {
            Integer dim = Integer.parseInt(stDim.getText());
            Integer winLen = Integer.parseInt(stWinLen.getText());
            result = FXMLUtil.loadAsNode("fxml/main.fxml");
            FXMLUtil.addStylesheets(result.getNode(), Const.UNIVERSAL_STYLESHEET_URL);
            primaryStage.setScene(new Scene(result.getNode()));
            primaryStage.setResizable(true);
            primaryStage.setOnCloseRequest(event -> System.exit(0));
            MainController controller = (MainController) result.getController();
            controller.setup(gameId, dim, winLen, mode, teamId, opponentId, isHome,
                    asSpectator);
        } catch (IOException e) {
            e.printStackTrace();
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
        return GameMode.EVE_ONLINE;
    }
}
