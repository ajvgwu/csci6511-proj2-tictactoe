package edu.gwu.ai.codeknights.tictactoe;

public class StateException extends GameException {
  private static final long serialVersionUID = 3438706730720706377L;

  public StateException(final String msg) {
    super(msg);
  }

  public StateException(final String msg, final Throwable cause) {
    super(msg, cause);
  }

  public StateException(final Throwable cause) {
    super(cause);
  }
}
