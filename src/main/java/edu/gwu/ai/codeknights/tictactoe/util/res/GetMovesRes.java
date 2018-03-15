package edu.gwu.ai.codeknights.tictactoe.util.res;

import java.util.List;

/**
 * @author zhiyuan
 */
public class GetMovesRes extends BaseRes{

  private List<Move> moves;

  public List<Move> getMoves() {
    return moves;
  }

  public void setMoves(List<Move> moves) {
    this.moves = moves;
  }

  public static class Move{
    private Long moveId;
    private Long gameId;
    private Integer teamId;
    private String move;
    private String symbol;
    private Integer moveX;
    private Integer moveY;

    public Long getGameId() {
      return gameId;
    }

    public void setGameId(Long gameId) {
      this.gameId = gameId;
    }

    public Long getMoveId() {
      return moveId;
    }

    public void setMoveId(Long moveId) {
      this.moveId = moveId;
    }

    public Integer getTeamId() {
      return teamId;
    }

    public void setTeamId(Integer teamId) {
      this.teamId = teamId;
    }

    public String getMove() {
      return move;
    }

    public void setMove(String move) {
      this.move = move;
    }

    public String getSymbol() {
      return symbol;
    }

    public void setSymbol(String symbol) {
      this.symbol = symbol;
    }

    public Integer getMoveX() {
      return moveX;
    }

    public void setMoveX(Integer moveX) {
      this.moveX = moveX;
    }

    public Integer getMoveY() {
      return moveY;
    }

    public void setMoveY(Integer moveY) {
      this.moveY = moveY;
    }
  }

}
