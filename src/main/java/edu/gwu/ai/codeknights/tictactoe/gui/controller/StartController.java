package edu.gwu.ai.codeknights.tictactoe.gui.controller;

import edu.gwu.ai.codeknights.tictactoe.gui.Simulator;
import edu.gwu.ai.codeknights.tictactoe.gui.util.Const;
import edu.gwu.ai.codeknights.tictactoe.gui.util.FXMLLoadResult;
import edu.gwu.ai.codeknights.tictactoe.gui.util.FXMLUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author zhiyuan
 */
public class StartController {

    @FXML
    private Button stPVE;

    @FXML
    private Button stEVE;

    @FXML
    private Button stOnline;

    @FXML
    private Button stStart;

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
    private Text stErr;

    @FXML
    void initialize() {

        stErr.setStyle("-fx-fill: red");

        stDim.textProperty().addListener(observable -> checkDimWinLen());
        stWinLen.textProperty().addListener(observable -> checkDimWinLen());

        stPVE.setOnAction(event -> {
            stPVE.disableProperty().set(true);
            stEVE.disableProperty().set(false);
            stOnline.disableProperty().set(false);
        });

        stEVE.setOnAction(event -> {
            stPVE.disableProperty().set(false);
            stEVE.disableProperty().set(true);
            stOnline.disableProperty().set(false);
        });

        stOnline.setOnAction(event -> {
            stPVE.disableProperty().set(false);
            stEVE.disableProperty().set(false);
            stOnline.disableProperty().set(true);
        });

        stPVE.setDisable(true);
    }

    private boolean checkDimWinLen(){
        try {
            Integer winLen = Integer.parseInt(stWinLen.getText());
            Integer dim = Integer.parseInt(stDim.getText());
            if (dim < winLen) {
                throw new NumberFormatException();
            }
            stErr.setText("");
        } catch (NumberFormatException ex) {
            stErr.setText("dimension must be larger than win length");
            return false;
        }
        return true;
    }

    @FXML
    void startHandler(ActionEvent event) {
        Integer dim = Integer.parseInt(stDim.getText());
        Integer winLen = Integer.parseInt(stWinLen.getText());

        int mode = getMode();

        Stage primaryStage = Simulator.getPrimaryStage();
        try {
            Integer teamId = Integer.parseInt(stTeamId.getText());
            Integer opponentId = Integer.parseInt(stOpId.getText());
            Long gameId = Long.parseLong(stGameId.getText());
            // load the main scene, then current scene will dismiss
            FXMLLoadResult result = FXMLUtil.loadAsNode("fxml/main.fxml");
            FXMLUtil.addStylesheets(result.getNode(), Const.UNIVERSAL_STYLESHEET_URL);
            primaryStage.setScene(new Scene(result.getNode()));
            primaryStage.setResizable(true);
            MainController controller = (MainController) result.getController();
            controller.setup(mode, teamId, gameId, dim, dim, winLen, opponentId);
            stStart.setDisable(true);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (NumberFormatException e){
            stErr.setText("Invalid Number");
        }
    }

    private int getMode(){
        if(stPVE.isDisabled()){
            // PVE
            return 1;
        }else{
            if (stEVE.isDisabled()){
                // EVE
                return 2;
            }else{
                // EVE Online
                return 3;
            }
        }
    }
}
