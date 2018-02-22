package edu.gwu.ai.codeknights.tictactoe.core.exception;

public class GameException extends Exception {
  private static final long serialVersionUID = 5872066562459961533L;

  public GameException(final String msg) {
    super(msg);
  }

  public GameException(final String msg, final Throwable cause) {
    super(msg, cause);
  }

  public GameException(final Throwable cause) {
    super(cause);
  }
}
