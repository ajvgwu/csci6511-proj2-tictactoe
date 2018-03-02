package edu.gwu.ai.codeknights.tictactoe.chooser;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public enum Chooser {

  STUPID_MOVE("StupidMove") {

    @Override
    public AbstractCellChooser createChooser() {
      return new StupidMoveChooser();
    }
  },
  ONLINE_MOVE("OnlineMove") {

    @Override
    public AbstractCellChooser createChooser() {
      return new OnlineMoveChooser();
    }
  },
  ANY_CELL("AnyCell") {

    @Override
    public AbstractCellChooser createChooser() {
      return new AnyCellChooser();
    }
  },
  RULE_BASED("RuleBased") {

    @Override
    public AbstractCellChooser createChooser() {
      return new RuleBasedChooser();
    }
  },
  PAIRING("Pairing") {

    @Override
    public AbstractCellChooser createChooser() {
      return new PairingChooser();
    }
  },
  MAX_UTILITY("MaxUtility") {

    @Override
    public AbstractCellChooser createChooser() {
      return new MaxUtilityChooser();
    }
  },
  ALPHA_BETA_PRUNING_D2("AlphaBetaPruningD2") {

    @Override
    public AbstractCellChooser createChooser() {
      return new AlphaBetaPruningChooser(2);
    }
  },
  ALPHA_BETA_PRUNING_D4("AlphaBetaPruningD4") {

    @Override
    public AbstractCellChooser createChooser() {
      return new AlphaBetaPruningChooser(4);
    }
  },
  ALPHA_BETA_PRUNING_D8("AlphaBetaPruningD8") {

    @Override
    public AbstractCellChooser createChooser() {
      return new AlphaBetaPruningChooser(8);
    }
  },
  ALPHA_BETA_PRUNING_D16("AlphaBetaPruningD16") {

    @Override
    public AbstractCellChooser createChooser() {
      return new AlphaBetaPruningChooser(16);
    }
  },
  ALPHA_BETA_PRUNING_D32("AlphaBetaPruningD32") {

    @Override
    public AbstractCellChooser createChooser() {
      return new AlphaBetaPruningChooser(32);
    }
  },
  ALPHA_BETA_PRUNING_D64("AlphaBetaPruningD64") {

    @Override
    public AbstractCellChooser createChooser() {
      return new AlphaBetaPruningChooser(64);
    }
  },
  BEST_OPTION("BestOption") {

    @Override
    public AbstractCellChooser createChooser() {
      return new BestOptionChooser();
    }
  },
  CASE_BY_CASE("CaseByCase") {

    @Override
    public AbstractCellChooser createChooser() {
      return new CaseByCaseChooser();
    }
  };

  private final String name;

  private Chooser(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public abstract AbstractCellChooser createChooser();

  public static List<String> getNames() {
    return EnumSet.allOf(Chooser.class).stream()
      .map(Chooser::getName)
      .collect(Collectors.toList());
  }

  public static Chooser fromName(final String name) {
    return EnumSet.allOf(Chooser.class).stream()
      .filter(chooser -> Objects.equals(name, chooser.getName()))
      .findAny()
      .orElse(null);
  }
}
