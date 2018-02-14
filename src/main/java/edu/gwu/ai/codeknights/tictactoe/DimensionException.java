package edu.gwu.ai.codeknights.tictactoe;

public class DimensionException extends GameException {
  private static final long serialVersionUID = -2706358295034033428L;

  public DimensionException(final String msg) {
    super(msg);
  }

  public DimensionException(final String msg, final Throwable cause) {
    super(msg, cause);
  }

  public DimensionException(final Throwable cause) {
    super(cause);
  }
}
