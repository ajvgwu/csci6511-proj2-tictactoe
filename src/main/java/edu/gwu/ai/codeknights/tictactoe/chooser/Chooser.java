package edu.gwu.ai.codeknights.tictactoe.chooser;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.gwu.ai.codeknights.tictactoe.filter.BestOpenSublineFilter;
import edu.gwu.ai.codeknights.tictactoe.filter.EmptyCellFilter;
import edu.gwu.ai.codeknights.tictactoe.filter.PopulatedNeighborFilter;

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
  ALPHA_BETA_PRUNING_ALL_EMPTY("AlphaBetaPruningAllEmpty") {

    @Override
    public AbstractCellChooser createChooser() {
      return new AlphaBetaPruningChooser(new EmptyCellFilter());
    }
  },
  ALPHA_BETA_PRUNING_ALL_EMPTY_TREE("AlphaBetaPruningAllEmptyTree") {

    @Override
    public AbstractCellChooser createChooser() {
      final AlphaBetaPruningChooser chooser = new AlphaBetaPruningChooser(new EmptyCellFilter());
      chooser.setGraphSearch(false);
      return chooser;
    }
  },
  ALPHA_BETA_PRUNING_NEIGHBOR("AlphaBetaPruningNeighbor") {

    @Override
    public AbstractCellChooser createChooser() {
      return new AlphaBetaPruningChooser(new PopulatedNeighborFilter());
    }
  },
  ALPHA_BETA_PRUNING_BEST_SUBLINE("AlphaBetaPruningBestSubline") {

    @Override
    public AbstractCellChooser createChooser() {
      return new AlphaBetaPruningChooser(new BestOpenSublineFilter());
    }
  },
  BEST_OPTION("BestOption") {

    @Override
    public AbstractCellChooser createChooser() {
      return new BestOptionChooser();
    }
  },
  CASE_BY_CASE_ABP_ALL_EMPTY("CaseByCaseAbpAllEmpty") {

    @Override
    public AbstractCellChooser createChooser() {
      return new CaseByCaseChooser(new EmptyCellFilter());
    }
  },
  CASE_BY_CASE_ABP_ALL_EMPTY_TREE("CaseByCaseAbpAllEmptyTree") {

    @Override
    public AbstractCellChooser createChooser() {
      return new CaseByCaseChooser(new EmptyCellFilter(), false);
    }
  },
  CASE_BY_CASE_ABP_NEIGHBOR("CaseByCaseAbpNeighbor") {

    @Override
    public AbstractCellChooser createChooser() {
      return new CaseByCaseChooser(new PopulatedNeighborFilter());
    }
  },
  CASE_BY_CASE_ABP_BEST_SUBLINE("CaseByCaseAbpBestSubline") {

    @Override
    public AbstractCellChooser createChooser() {
      return new CaseByCaseChooser(new BestOpenSublineFilter());
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
