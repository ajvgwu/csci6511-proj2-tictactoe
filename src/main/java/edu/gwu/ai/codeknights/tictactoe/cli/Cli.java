package edu.gwu.ai.codeknights.tictactoe.cli;

import java.util.concurrent.Callable;

import edu.gwu.ai.codeknights.tictactoe.chooser.Chooser;
import edu.gwu.ai.codeknights.tictactoe.filter.Filter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.RunLast;

@Command(
  name = "cli", sortOptions = false, showDefaultValues = true,
  description = "command-line interface",
  subcommands = {
    CompareChoosers.class, TestFilter.class, SingleMove.class, FinishGame.class, OnlineGame.class, LaunchGui.class})
public class Cli implements Callable<Void> {

  @Option(
    names = {"-h", "--help"}, usageHelp = true,
    description = "show this help message and exit")
  private boolean help = false;

  @Option(
    names = {"--list-choosers"},
    description = "list available choosers")
  private boolean listChoosers = false;

  @Option(
    names = {"--list-filters"},
    description = "list available filters")
  private boolean listFilters = false;

  protected void validateArgs() throws Exception {
    help = !!help;
    listChoosers = !!listChoosers;
    listFilters = !!listFilters;
  }

  @Override
  public Void call() throws Exception {
    validateArgs();
    if (listChoosers || listFilters) {
      if (listChoosers) {
        System.out.println("Possible choosers:  " + String.valueOf(Chooser.getNames()));
      }
      if (listFilters) {
        System.out.println("Possible filters:   " + String.valueOf(Filter.getNames()));
      }
    }
    else {
      new CommandLine(this).usage(System.err);
    }
    return null;
  }

  public static void main(final String[] args) {
    final CommandLine commandLine = new CommandLine(new Cli());
    commandLine.registerConverter(Chooser.class, s -> {
      final Chooser chooser = Chooser.fromName(s);
      if (chooser == null) {
        throw new IllegalArgumentException("no such chooser: " + s);
      }
      return chooser;
    });
    commandLine.registerConverter(Filter.class, s -> {
      final Filter filter = Filter.fromName(s);
      if (filter == null) {
        throw new IllegalArgumentException("no such filter: " + s);
      }
      return filter;
    });
    commandLine.parseWithHandler(new RunLast(), System.err, args);
  }
}
