package edu.gwu.ai.codeknights.tictactoe.gui.util;

import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 * @author zhiyuan
 */
public class FXMLLoadResult<T> {

    private Parent node;
    private T controller;
    private Stage stage;

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Parent getNode() {
        return node;
    }

    public void setNode(Parent node) {
        this.node = node;
    }

    public T getController() {
        return controller;
    }

    public void setController(T controller) {
        this.controller = controller;
    }
}
