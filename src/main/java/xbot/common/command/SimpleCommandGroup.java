package xbot.common.command;

public class SimpleCommandGroup extends BaseCommandGroup {

    public enum ExecutionType {
        Serial,
        Parallel
    }

    public SimpleCommandGroup(String name, Iterable<BaseCommand> commands, ExecutionType executionType) {
        super(name);
        commands.forEach((command) -> {
            switch(executionType) {
                case Serial: {
                    this.addSequential(command);
                    break;
                }
                case Parallel: {
                    this.addParallel(command);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unknown executionType");
                }
            }
        });
    }
}