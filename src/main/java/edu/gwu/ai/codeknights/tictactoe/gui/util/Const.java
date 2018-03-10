package edu.gwu.ai.codeknights.tictactoe.gui.util;

/**
 * @author zhiyuan
 */
public class Const {
    public final static String UNIVERSAL_STYLESHEET_URL = "static/bootstrap3.css";
    public static final char BLANK_SPACE_CHAR = '-';
    public static final char MASTER_PLAYER_CHAR = 'X';
    public static final char OPPONENT_PLAYER_CHAR = 'O';
    /**
     * if it's zero-based, set to 0, if it's one-based, set to 1
     * */
    public static final int ONLINE_BOARD_OFFSET = 0;

    public static final String API_GAMETYPE_DEFAULT = "TTT";
    public static final String API_BOARDSIZE_DEFAULT = "12";
    public static final String API_TARGET_DEFAULT = "6";

    public static final int CHOOSER_TIME_LIMIT_DEFAULT = 110;
}
