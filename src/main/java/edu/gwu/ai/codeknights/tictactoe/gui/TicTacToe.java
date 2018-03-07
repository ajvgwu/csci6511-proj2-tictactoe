package edu.gwu.ai.codeknights.tictactoe.gui;

import edu.gwu.ai.codeknights.tictactoe.gui.util.FXMLLoadResult;
import edu.gwu.ai.codeknights.tictactoe.gui.util.FXMLUtil;
import edu.gwu.ai.codeknights.tictactoe.util.Const;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author zhiyuan
 */
public class TicTacToe extends Application {

    /**
     * primaryStage is the root component of the UI module
     * it will be initialized in the start method
     */
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // load start scene
        FXMLLoadResult result = FXMLUtil.loadAsNode("fxml/start.fxml");
        FXMLUtil.addStylesheets(result.getNode(), Const.UNIVERSAL_STYLESHEET_URL);

        primaryStage.setTitle("The Code Knights");
        primaryStage.setScene(new Scene(result.getNode()));
        primaryStage.setResizable(false);
        primaryStage.show();

        TicTacToe.primaryStage = primaryStage;
    }

    /**
     * fetch primary stage of the simulator
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}