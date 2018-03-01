package edu.gwu.ai.codeknights.tictactoe.filtering.chooser;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public enum Chooser {

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
    return EnumSet.allOf(Chooser.class).parallelStream()
      .filter(chooser -> Objects.equals(name, chooser.getName()))
      .findAny()
      .orElse(null);
  }
}
