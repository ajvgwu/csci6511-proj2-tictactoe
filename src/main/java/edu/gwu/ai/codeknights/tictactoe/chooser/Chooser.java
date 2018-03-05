package edu.gwu.ai.codeknights.tictactoe.chooser;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.gwu.ai.codeknights.tictactoe.filter.PopulatedNeighborFilter;

public enum Chooser {

  ONLINE_MOVE_FETCHER("OnlineMoveFetcher") {

    @Override
    public AbstractCellChooser createChooser() {
      return new OnlineMoveFetcher();
    }
  },
  ONLINE_MOVE_MAKER_90SEC("OnlineMoveMaker90Sec") {

    @Override
    public AbstractCellChooser createChooser() {
      return new OnlineMoveMaker(90);
    }
  },
  ONLINE_MOVE_MAKER_100SEC("OnlineMoveMaker100Sec") {

    @Override
    public AbstractCellChooser createChooser() {
      return new OnlineMoveMaker(100);
    }
  },
  ONLINE_MOVE_MAKER_110SEC("OnlineMoveMaker110Sec") {

    @Override
    public AbstractCellChooser createChooser() {
      return new OnlineMoveMaker(110);
    }
  },
  ALPHA_BETA_PRUNING("AlphaBetaPruning") {

    @Override
    public AbstractCellChooser createChooser() {
      return new AlphaBetaPruningChooser();
    }
  },
  ALPHA_BETA_PRUNING_NEIGHBOR("AlphaBetaPruningNeighbor") {

    @Override
    public AbstractCellChooser createChooser() {
      return new AlphaBetaPruningChooser(new PopulatedNeighborFilter());
    }
  },
  ABP_LIMIT_10SEC("AbpLimit10Sec") {

    @Override
    public AbstractCellChooser createChooser() {
      return new AbpTimeLimitChooser(10);
    }
  },
  ABP_LIMIT_100SEC("AbpLimit100Sec") {

    @Override
    public AbstractCellChooser createChooser() {
      return new AbpTimeLimitChooser(100);
    }
  },
  ABP_LIMIT_120SEC("AbpLimit120Sec") {

    @Override
    public AbstractCellChooser createChooser() {
      return new AbpTimeLimitChooser(120);
    }
  },
  CASE_BY_CASE_ABP_LIMIT_10SEC("CaseByCaseAbpLimit10Sec") {

    @Override
    public AbstractCellChooser createChooser() {
      return new CaseByCaseChooser(new AbpTimeLimitChooser(10));
    }
  },
  CASE_BY_CASE_ABP_LIMIT_100SEC("CaseByCaseAbpLimit100Sec") {

    @Override
    public AbstractCellChooser createChooser() {
      return new CaseByCaseChooser(new AbpTimeLimitChooser(100));
    }
  },
  CASE_BY_CASE_ABP_LIMIT_120SEC("CaseByCaseAbpLimit120Sec") {

    @Override
    public AbstractCellChooser createChooser() {
      return new CaseByCaseChooser(new AbpTimeLimitChooser(120));
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
