package edu.gwu.ai.codeknights.tictactoe.chooser;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;

import java.util.stream.Stream;

/**
 * chooser a move by person clicking on ui control
 * @author zhiyuan
 */
public class StupidMoveChooser extends AbstractCellChooser {

    @Override
    public Cell chooseCell(Stream<Cell> input, Game game) {
        return null;
    }
}
