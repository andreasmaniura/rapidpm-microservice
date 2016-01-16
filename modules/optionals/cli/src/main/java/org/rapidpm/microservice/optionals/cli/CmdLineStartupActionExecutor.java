package org.rapidpm.microservice.optionals.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.rapidpm.ddi.DI;
import org.rapidpm.microservice.Main.MainStartupAction;
import org.rapidpm.microservice.optionals.cli.helper.ExitHelper;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Sven Ruppert on 18.11.2015.
 */
public class CmdLineStartupActionExecutor implements MainStartupAction {
  public static final String CMD_HELP = "h";
  private final CmdLineParser cmdLineParser = new CmdLineParser();
  @Inject
  private ExitHelper exitHelper;

  @Override
  public void execute(Optional<String[]> args) {
    addHelpOption();
    List<CmdLineStartupAction> startupActionInstances = getCmdLineStartupActions();
    addOptionsToCmdLineSingleton(startupActionInstances);
    checkCommands();
    executeActions(startupActionInstances);
  }

  private void addHelpOption() {
    cmdLineParser.addCmdLineOption(new Option(CMD_HELP, "help", false, "Print this page"));
  }

  private List<CmdLineStartupAction> getCmdLineStartupActions() {
    final Set<Class<? extends CmdLineStartupAction>> startupActions = DI.getSubTypesOf(CmdLineStartupAction.class);
    return createInstances(startupActions);
  }

  private void addOptionsToCmdLineSingleton(final List<CmdLineStartupAction> startupActionInstances) {
    startupActionInstances.stream()
        .map(CmdLineStartupAction::getOptions)
        .flatMap(Collection::stream)
        .forEach(opt -> cmdLineParser.addCmdLineOption(opt));
  }

  private void checkCommands() {
    List<String> argList = cmdLineParser.getCommandLine().get().getArgList();
    if (!argList.isEmpty()) {
      String unrecognizedCommands = argList.stream()
              .map(s -> String.format("<%s>", s))
              .reduce((s1, s2) -> s1 + ", " + s2)
              .get();
      String message = "Unrecognized commands give :" +
          unrecognizedCommands +
          "\n" +
          cmdLineParser.getHelpText();
      System.out.println(message);
      exitHelper.exit(1);
    }
  }

  private void executeActions(final List<CmdLineStartupAction> startupActionInstances) {
    final Optional<CommandLine> commandLine = cmdLineParser.getCommandLine();

    if (commandLine.isPresent()) {
      final CommandLine cmdLine = commandLine.get();
      if (cmdLine.hasOption(CMD_HELP)) {
        System.out.println(cmdLineParser.getHelpText());
        exitHelper.exit(0);
      }
      startupActionInstances.stream().forEach(cmdLineStartupAction -> cmdLineStartupAction.execute(cmdLine));
    }
  }

  private static <T> List<T> createInstances(final Set<Class<? extends T>> classes) {
    return classes
            .stream()
            .map(c -> {
              try {
                return Optional.of(c.newInstance());
              } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
              }
              return Optional.<T>empty();
            })
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(DI::activateDI)
            .collect(Collectors.<T>toList());
  }
}
