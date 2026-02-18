# Seriously Common Lib for FRC

[![Build Status](https://dev.azure.com/Team488/Team%20488%20Builds/_apis/build/status%2FTeam488.SeriouslyCommonLib?branchName=main)](https://dev.azure.com/Team488/Team%20488%20Builds/_build/latest?definitionId=1&branchName=main)
[![xbot.common:SeriouslyCommonLib package in XBot feed in Azure Artifacts](https://feeds.dev.azure.com/Team488/2ef6eee3-1df0-441a-9af8-5edc5c3cb203/_apis/public/Packaging/Feeds/XBot/Packages/1dc16f9c-8e55-4f41-ada6-7b73e2161843/Badge)](https://dev.azure.com/Team488/Team%20488%20Builds/_artifacts/feed/XBot/maven/xbot.common%2FSeriouslyCommonLib?preferRelease=true)

## Up-Front Warning

This repository is still very much a work in progress! There are chunks of code and build steps missing here that would probably block you from directly integrating this into your robot. 

It's getting more complete every week, but as it stands, we recommend that you use parts of this repository rather than the whole of it - taking individual classes, or using the design as inspiration, rather than seeing it as a stable platform for robot development.

## What is it?

Seriously Common Lib is a collection of common solutions to robot problems that FRC teams face while using WPILib and Java. It attempts to streamline basic functionality, provide the framework for more advanced programs, and promote clean and well-written code. It functions primarily as a wrapper around WPILib which allows the robot code to run separately from the robot-specific library and HAL, enabling unit tests that can be run on a PC or CI server.

For new teams that are haven't done much advanced programming before, this library has all the core code that is needed to get a robot running with advanced functionality without having to write the core systems from scratch. For more advanced teams, the common lib gives you a solid foundation of all the basics to start building your robot code on top of. You don't need to use the pre-built drive system or any of the other parts of the library if you don't want to; it's all modular.

## Who's working on it?
SeriouslyCommonLib started as a separation of the commonly-written sections of code from FRC team 488 Xbot. We wanted to write re-usable code that could significantly decrease the time it took to get started in subsequent years. We had also been looking for a way to write unit tests for our robot code, which required us to write a fairly large amount of code that we didn't want to re-write each year. In doing so, we developed loads of useful utilities and wrappers and kept them separate from our season code so that we could publish it when we had the time.

We plan to continue maintaining this codebase throughout upcoming years as we develop new functionality and find areas to fix and improve.

## What does it do?
### Main features
- **Fully testable** We have wrappers for the robot-specific classes that WPILib provies. This means that every class can be run on a development PC, using mock implementations and factories. Your tests run all the same code that gets deployed to the robot.
- **Persistent, configurable properties** We have utilities and extra interfaces which provide an easily-accessable property framework. When you use our classes to create a property, supplying a flag makes it automatically persist in a database on the robot as well as on the Smart Dashboard. Perfect for configuration and settings.
- **Detailed logging** Every class logs with a specific message and error level when something important happens (using log4j). You can modify the logging priority threshold for the program as a whole or for specific classes, depending on the verbosity of the desired logging.
- **Re-usable utilities**  Our math utilities, logic helpers, and various number containers (such as the `XYPair`for vectors or points) contain loads of re-usable logic that's useful throughout a robot program.
- **Robots never give up** It's always painful when your robot code crashes in a competition and you lose control of your robot. Although it's always best to write safe code, our scheduler wrapper keeps commands running even after an exception is thrown.
- **Pre-written drive system** We have written a drive system implementation and included it in the commom lib to make it as easy as possible to get started writing code with minimal effort.
- **Tilt detection** The common lib includes drive system helpers to attempt to counteract the acceleration of a tilting robot. If it's enabled and the robot starts to fall over, the tilt detection will kick in and attempt to right itself.
- **Enhanced autonomous capabilities** We're working on the infrastructure required to allow you to write JavaScript code which executes as a WPILib command. This means that you can write small snippets of code that execute synchronously from their point of view while still allowing the scheduler and other robot functionality to run as normal.

## How do I get started?

If you want to jump in, a good place to start is our [robot template](https://github.com/Team488/FRCRobotTemplate). It includes all the configuration, build scripts, and boilerplate needed to start adding functionality. We hope to develop full documentation, but in the meantime, feel free to open issues on this repo to ask us questions and provide feedback. 
You can also take a look at the [generated documentation](https://team488.github.io/SeriouslyCommonLib/).

## How's development going?

We are currently in the process of modifying this library to get it ready as a robot platform. We plan to build on top of it in upcoming seasons to add functionality and fix bugs. Currently, it should be fully functional for testing using the template project, but there isn't much documentation available. More to come soon!

### We'd love some help!

Although we do plan to maintain this library for use in our own robots, we'd love to recieve help with fixing bugs, adding new features, and finding issues that we need to look at. If your team uses the library, we'd love for you to open issues on GitHub as you find them. And if you're feeling adventurous, we welcome PRs too.
