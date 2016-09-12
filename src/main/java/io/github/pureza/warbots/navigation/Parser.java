package io.github.pureza.warbots.navigation;

import io.github.pureza.warbots.config.Config;
import io.github.pureza.warbots.entities.Team;
import io.github.pureza.warbots.entities.TeamBuilder;
import io.github.pureza.warbots.entities.Wall;
import io.github.pureza.warbots.game.Game;
import io.github.pureza.warbots.game.GameBuilder;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.geometry.Size;
import io.github.pureza.warbots.resources.Sprites;
import io.github.pureza.warbots.weaponry.Weapon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The game parser
 *
 * Parses the instructions contained in a .map file and creates the
 * corresponding game.
 */
public class Parser {

    /** Regular expression to validate an argument such as 'AxB' */
    private static final Pattern SIZE_ARG_REGEX = Pattern.compile("^(\\d+)\\s*x\\s*(\\d+)$");

    /** Regular expression used to validate a point such as '(a, b)' */
    private static final Pattern POINT_REGEXP = Pattern.compile("\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)");

    /** Game configuration */
    private Config config;

    /** The logger */
    private Logger logger = LoggerFactory.getLogger(getClass());


    public Parser(Config config) {
        this.config = config;
    }


    /**
     * Parses a .map file and returns the corresponding Game instance
     */
    public Game parse(String fileName) throws IOException {
        return parse(new FileReader(fileName));
    }


    /**
     * Parser helper that receives a Reader instead of a file name
     *
     * More appropriate for testing.
     */
    Game parse(Reader reader) throws IOException {
        GameBuilder builder = new GameBuilder(config);
        MapBuilder mapBuilder = builder.mapBuilder();
        TeamBuilder teamBuilderA = new TeamBuilder().setTeamIconPath(Sprites.SPRITE_TEAM_A_PATH);
        TeamBuilder teamBuilderB = new TeamBuilder().setTeamIconPath(Sprites.SPRITE_TEAM_B_PATH);

        try (BufferedReader in = new BufferedReader(reader)) {
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();

                // Ignore empty lines and comments
                if (line.length() == 0 || line.charAt(0) == '#') {
                    continue;
                }

                // Splits the line in "command arguments"
                Pattern pattern = Pattern.compile("^([\\w-]+)\\s+(.*)$");
                Matcher matcher = pattern.matcher(line);

                if (matcher.find()) {
                    String command = matcher.group(1).toLowerCase();
                    String parameters = matcher.group(2).trim();

                    // Call the method that will handle this specific command
                    switch (command) {
                        case "bots":
                            this.parseBots(parameters, teamBuilderA, teamBuilderB);
                            break;
                        case "spawn":
                            this.parseSpawn(parameters, teamBuilderA, teamBuilderB);
                            break;
                        case "cells":
                            this.parseCells(parameters, mapBuilder);
                            break;
                        case "wall":
                            this.parseWall(parameters, mapBuilder);
                            break;
                        case "first-aid":
                            this.parseFirstAidKit(parameters, mapBuilder);
                            break;
                        case "weapon":
                            this.parseWeapon(parameters, mapBuilder);
                            break;
                        default:
                            logger.warn("Unknown command '{} in line {}", command, line);
                            break;
                    }
                } else {
                    logger.warn("Skipping line {}", line);
                }
            }
        }

        Map map = mapBuilder.build();
        Team teamA = teamBuilderA.build();
        Team teamB = teamBuilderB.build();
        return builder.build(map, teamA, teamB);
    }


    /**
     * Parses the "bots" command inside a .map file
     */
    void parseBots(String arguments, TeamBuilder teamBuilderA, TeamBuilder teamBuilderB) {
        Matcher matcher = SIZE_ARG_REGEX.matcher(arguments);
        if (matcher.find()) {
            int firstTeam = Integer.parseInt(matcher.group(1));
            int secondTeam = Integer.parseInt(matcher.group(2));
            teamBuilderA.setInitialNumberOfBots(firstTeam);
            teamBuilderB.setInitialNumberOfBots(secondTeam);
        } else {
            throw new IllegalArgumentException("Couldn't parse the number of cells.");
        }
    }


    /**
     * Parses the "spawn" command inside a .map file
     */
    void parseSpawn(String arguments, TeamBuilder teamBuilderA, TeamBuilder teamBuilderB) {
        String[] args = arguments.split(" ", 2);
        if (args.length != 2) {
            throw new IllegalArgumentException("Not enough arguments specified");
        }

        String teamStr = args[0].trim().toUpperCase();
        Point spawningPoint = stringToPoint(args[1]).displace(0.5, 0.5);

        switch (teamStr) {
            case "A":
                teamBuilderA.addSpawningPoint(spawningPoint);
                break;
            case "B":
                teamBuilderB.addSpawningPoint(spawningPoint);
                break;
            default:
                throw new IllegalArgumentException("Spawning point must specify the team A or B, but got " + teamStr);
        }
    }


    /**
     * Parses the "cells" command inside a .map file and configures the
     * MapBuilder with the specified size
     */
    void parseCells(String arguments, MapBuilder mapBuilder) {
        Matcher matcher = SIZE_ARG_REGEX.matcher(arguments);
        if (matcher.find()) {
            int width = Integer.parseInt(matcher.group(1));
            int height = Integer.parseInt(matcher.group(2));
            mapBuilder.setDimension(width, height);
        } else {
            throw new IllegalArgumentException("Couldn't parse the number of cells.");
        }
    }


    /**
     * Parses the "wall" command inside a .map file, creating a new Wall
     * at the desired location with the given size
     */
    void parseWall(String arguments, MapBuilder mapBuilder) {
        Matcher matcher = POINT_REGEXP.matcher(arguments);
        if (matcher.find()) {
            Point location = stringToPoint(matcher.group());
            if (matcher.find()) {
                Point sizePoint = stringToPoint(matcher.group());
                Size size = new Size((int) sizePoint.x(), (int) sizePoint.y());
                Wall wall = new Wall(location, size);
                mapBuilder.addWall(wall);
            } else {
                throw new IllegalArgumentException("Unable to parse wall size in " + arguments);
            }
        } else {
            throw new IllegalArgumentException("Unable to parse wall location in " + arguments);
        }
    }


    /**
     * Parses the first-aid command inside a .map file and adds the
     * item to the specified location
     */
    void parseFirstAidKit(String arguments, MapBuilder mapBuilder) {
        Point location = stringToPoint(arguments);
        mapBuilder.addFirstAidItem(location.displace(0.5, 0.5));
    }


    /**
     * Parses the weapon command inside a .map file and adds the
     * weapon to the specified location
     */
    void parseWeapon(String arguments, MapBuilder mapBuilder) {
        String[] args = arguments.split(" ", 2);
        if (args.length != 2) {
            throw new IllegalArgumentException("Not enough arguments specified");
        }

        String weaponStr = args[0].trim().toUpperCase();
        Weapon.WeaponType weaponType = Weapon.WeaponType.valueOf(weaponStr);

        Point location = stringToPoint(args[1]);
        mapBuilder.addWeaponItem(location.displace(0.5, 0.5), weaponType);
    }


    /**
     * Creates a Point from its string representation: "(x, y)"
     */
    Point stringToPoint(String ptStr) {
        Matcher matcher = POINT_REGEXP.matcher(ptStr);
        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            return new Point(x, y);
        }

        throw new IllegalArgumentException("Invalid syntax for point '" + ptStr + "'");
    }
}
