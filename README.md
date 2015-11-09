# Seriously Common Lib for FRC [![Build Status](https://travis-ci.org/Team488/SeriouslyCommonLib.svg?branch=master)](https://travis-ci.org/Team488/SeriouslyCommonLib)

## What is it?

Seriously Common Lib is a collection of common solutions to robot problems that FRC teams face while using WPILib and Java. It attempts to streamline basic functionality, provide the framework for more advanced programs, and promote clean and well-written code. It is mainly a wrapper around WPILib which enables extra custom functionality.

## Who's working on it?
SeriouslyCommonLib started as a separation of the commonly-written sections of code from FRC team 488 Xbot. We wanted to write re-usable code that could significantly decrease the time it took to get started in subsequent years. In doing so, we developed loads of useful utilities and wrappers and kept them separate from our season code so that we could publish it when we had the time.

We plan to continue maintaining this codebase throughout upcoming years as we develop new functionality an find areas to fix and improve.

## What does it do?
### Main features
- *Fully testable* We have wrappers for the robot-specific classes that WPILib provies. This means that every class can be run on a development PC, using mock implementations and factories. Your tests run all the same code that gets deployed to the robot.
- *Persistent, configurable properties* We have utilities and extra interfaces which provide an easily-accessable property framework. When you use our classes to create a property, supplying a flag makes it automatically persist in a database on the robot as well as on the Smart Dashboard. Perfect for configuration and settings.
- *Detailed logging* Every class logs with a specific message and error level when something important happens (using log4j). You can modify the logging priority threshold for the program as a whole or for specific classes.
- TODO

## State of development

We are currently in the process of refactoring this library to make it a good foundation to start developing new functionality. At the moment, the core components have been implemented, but are fractured across multiple disparate packages. Once we finish getting all the existing functionality prepared for broader use, we will start to implement new features and expand upon existing ones.
