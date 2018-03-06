package edu.gwu.ai.codeknights.tictactoe.cli;

import edu.gwu.ai.codeknights.tictactoe.gui.util.API;
import picocli.CommandLine.Option;

public abstract class AbstractOnlineSubcommand extends AbstractSubcommand {

  @Option(
    names = {"--user-id"}, required = true,
    description = "userId for online game server")
  private String userId = null;

  @Option(
    names = {"--api-key"}, required = true,
    description = "apiKey for online game server")
  private String apiKey = null;

  @Override
  protected void validateArgs() throws Exception {
    super.validateArgs();
    if (getPlayer1Id() < 1000) {
      throw new IllegalArgumentException("player1Id must be >= 1000");
    }
    if (getPlayer2Id() < 1000) {
      throw new IllegalArgumentException("player2Id must be >= 1000");
    }
    userId = userId != null ? userId.trim() : new String();
    if (userId.length() < 1) {
      throw new IllegalArgumentException("userId must be provided");
    }
    apiKey = apiKey != null ? apiKey.trim() : new String();
    if (apiKey.length() < 1) {
      throw new IllegalArgumentException("apiKey must be provided");
    }

    // Populate global constants
    API.HEADER_USER_ID_VALUE = getUserId();
    API.HEADER_API_KEY_VALUE = getApiKey();
  }

  protected String getUserId() {
    return userId;
  }

  protected String getApiKey() {
    return apiKey;
  }
}
