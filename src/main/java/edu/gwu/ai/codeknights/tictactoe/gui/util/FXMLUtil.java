package edu.gwu.ai.codeknights.tictactoe.gui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author zhiyuan
 */
public class FXMLUtil {

  private static final String UI_RESOURCE_PATH_PREFIX = "/";
  private static ArrayList controllers = new ArrayList();

  /**
   * load node from fxml file
   * the provided path is only the file name
   */
  public static <T> FXMLLoadResult loadAsNode(String path) throws
      IOException {
    URL url = loadAsUrl(UI_RESOURCE_PATH_PREFIX + path);
    FXMLLoader loader = new FXMLLoader(url);
    Parent node = loader.load();
    FXMLLoadResult<T> result = new FXMLLoadResult<>();
    result.setNode(node);
    result.setController(loader.getController());
    return result;
  }

  /**
   * load resource from the path as URL
   * the provided path is full path
   */
  public static URL loadAsUrl(String path) {
    URL url = FXMLUtil.class.getResource(path);
    return url;
  }

  /**
   * load stylesheets from the path
   * the provided path is only the file name
   */
  public static void addStylesheets(Parent node, String path) {
    URL url = loadAsUrl(UI_RESOURCE_PATH_PREFIX + path);
    node.getStylesheets().add(url.toExternalForm());
  }

  public static ArrayList getControllers() {
    return controllers;
  }

  public static void replaceTheme(Parent node, String themeUrl) {
    node.getStylesheets().clear();
    addStylesheets(node, themeUrl);
  }
}
