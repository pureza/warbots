# WarBots

WarBots is shooter-style simulator driven by Artificial Intelligence.

## Description

In the WarBots world, the bots belong to one of two teams and the main goal is
to kill the bots in the opponent team. In order to do this, they need to find
 the opponents, fire at them and avoid being killed. The bots can choose
 between 3 different weapons, some more appropriate than others depending on
 the target. While they move around the 2D tile-based map, the bots may also
 pickup health and ammunition.

WarBots showcases some common Game and Artificial Intelligence development
techniques, such as:

 - Path finding
 - Collision detection and response
 - Hierarchical goal-based planning
 - Fuzzy Logic
 - Steering Behaviors

![Alt text](warbots.png?raw=true "A typical WarBots fight")

## Building from source

### Requirements

In order to build and run this project, you will need:

* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
or greater
* [Apache Maven](https://maven.apache.org/)


### Compiling

```bash
 $ mvn validate
 $ mvn package
```

This should generate the `target/warbots-1.0.jar` file.


## How to run it

To run WarBots, just do:

```bash
 $ java -jar target/warbots-1.0.jar </path/to/map.map>
```

The single argument is the path to the map file describing the world. You can
find a few sample maps within the `maps/` directory.

### Example

```bash
 $ java -jar target/warbots-1.0.jar maps/default.map
```

## Credits

WarBots was developed by Joel Cordeiro and Luís Pureza.

Credits due to Mat Buckland as well, the author of the excellent "Programming
Game AI by Example" from where WarBots took a lot of inspiration.

## License

This project is released under the MIT license. See the LICENSE file for details.
