# Data Scrambler
Data Scrambler is a project that allows data generation in various forms. 
Project exposes an API to generate numbers, dates, strings, collections, maps, arrays in an incremental, random, combinations, 
or custom generation. It is possible to generate data for almost all generic JDK objects, and based on these options, 
Data Scrambler API offers a way to generate Java classes like beans for example.

#Overview
When a new project is started or a test is developed, often developer struggles with data definition. 
Usually it ends up with some trivial data sets that are limited in number as well as limited in data universe 
(just a few fields are involved). After project evolves, more 'real' datasets/sources became available, but usually
they are 'rigid':
1. Datasets are 'specific' to project and can't be reused.
2. It is not easy to change data if a model is modified/changed.
3. Usually, developed code that generated datasets can't be reused in unit tests

DataScrambler API addresses data generation needs by offering API for basic/generic java objects and an easy 
mechanism to define rules that can be easily changed (in the most of the cases that won't be needed) and can be reused.

* API offers a large variety of data generation classes for basic/generic data types.
* Simple and flexible way to 'link' data type with property using regular expression
* Simple and easy way to define rules using Groovy DSL or using Scrambler' builders

Having simply defined, atomic in concept rules for generation as well as a native/simple way to define them,
it is easy to build more complex types.

[DataScrambler API explained](docs/Design.md)

# Features
All generation capabilities are exposed in `org.prismus.scrambler.*Scrambler` facade classes.

* [Number generation](docs/NumberScrambler.md)
* [java.util.Date generation](docs/DateScrambler.md)
* [java.lang.String generation](docs/StringScrambler.md)
* [Generic object/array](docs/ObjectScrambler.md)
* [java.util.Collection generation](docs/CollectionScrambler.md)
* [java.util.Map generation](docs/MapScrambler.md)
* [DataScrambler DSL](docs/Dsl.md)
* [Instance generation](docs/InstanceScrambler.md)
* DataScrambler Extensions
  * [Definitions Dictionaries](docs/Dictionaries.md)
  * [Box testing (Experimental)](docs/BoxTesting.md)
