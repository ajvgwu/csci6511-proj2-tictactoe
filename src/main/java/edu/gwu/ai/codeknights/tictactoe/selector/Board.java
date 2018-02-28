package edu.gwu.ai.codeknights.tictactoe.selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ForwardingList;

public class Board {

  private final List<List<Cell>> rows;
  private final List<List<Cell>> cols;
  // private final Play[][] plays;

  public Board(final int dim) {
    rows = new ArrayList<>(dim);
    cols = new ArrayList<>(dim);
  }

  public List<List<Cell>> getRows() {
    return unmodifiableListOfLists(rows);
  }

  public List<Cell> getRow(final int idx) {
    return Collections.unmodifiableList(rows.get(idx));
  }

  public List<List<Cell>> getCols() {
    return unmodifiableListOfLists(cols);
  }

  public List<Cell> getCol(final int idx) {
    return Collections.unmodifiableList(cols.get(idx));
  }

  public static <T> List<List<T>> unmodifiableListOfLists(final List<List<T>> input) {
    return Collections.unmodifiableList(new ForwardingList<List<T>>() {

      @Override
      protected List<List<T>> delegate() {
        return Collections.unmodifiableList(input);
      }

      @Override
      public List<T> get(final int idx) {
        return Collections.unmodifiableList(delegate().get(idx));
      }
    });
  }
}
