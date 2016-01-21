robot.requireCommands('CounterCommand');

var invokedCommand = robot.invokeCounterCommand(50);
invokedCommand.waitForCompletion();