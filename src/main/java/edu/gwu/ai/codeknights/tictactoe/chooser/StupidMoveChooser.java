package edu.gwu.ai.codeknights.tictactoe.chooser;

import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;

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
