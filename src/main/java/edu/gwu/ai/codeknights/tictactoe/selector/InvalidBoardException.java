package edu.gwu.ai.codeknights.tictactoe.selector;

public class InvalidBoardException extends Exception {
  private static final long serialVersionUID = -2234516928490498360L;

  public InvalidBoardException(final String msg) {
    super(msg);
  }

  public InvalidBoardException(final String msg, final Throwable cause) {
    super(msg, cause);
  }

  public InvalidBoardException(final Throwable cause) {
    super(cause);
  }
}
