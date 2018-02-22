package edu.gwu.ai.codeknights.tictactoe.gui.util;

import edu.gwu.ai.codeknights.tictactoe.chooser.AbstractMoveChooser;

/**
 * @author zhiyuan
 */
public class Player {

    private Integer id;
    private String symbol;
    private AbstractMoveChooser moveChooser;

    public Player(Integer id, String symbol, AbstractMoveChooser moveChooser) {
        this.id = id;
        this.symbol = symbol;
        this.moveChooser = moveChooser;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public AbstractMoveChooser getMoveChooser() {
        return moveChooser;
    }

    public void setMoveChooser(AbstractMoveChooser moveChooser) {
        this.moveChooser = moveChooser;
    }
}
