package io.github.pureza.warbots.entities;

import io.github.pureza.warbots.geometry.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for teams
 *
 * Note: This has nothing to do with Team Building!
 */
public class TeamBuilder {

    /** Initial number of bots on this team */
    private Integer initialNumberOfBots;

    /** Spawning points for the bots on this team */
    private final List<Point> spawningPoints = new ArrayList<>();

    /** Path to the team icon */
    private String teamIconPath;


    public Integer getInitialNumberOfBots() {
        return initialNumberOfBots;
    }


    /**
     * Sets the initial number of bots on this team
     */
    public TeamBuilder setInitialNumberOfBots(int initialNumberOfBots) {
        this.initialNumberOfBots = initialNumberOfBots;
        return this;
    }


    public List<Point> getSpawningPoints() {
        return spawningPoints;
    }


    /**
     * Adds a spawning point for the bots on this team
     */
    public TeamBuilder addSpawningPoint(Point spawningPoint) {
        this.spawningPoints.add(spawningPoint);
        return this;
    }


    /**
     * Sets the path to the icon to be used by all bots on this team
     */
    public TeamBuilder setTeamIconPath(String teamIconPath) {
        this.teamIconPath = teamIconPath;
        return this;
    }


    /**
     * Builds the team with the given configuration
     */
    public Team build() {
        if (initialNumberOfBots == null) {
            throw new IllegalStateException("Initial number of bots not set");
        }

        if (spawningPoints.isEmpty()) {
            throw new IllegalStateException("No spawning points defined for this team");
        }

        if (teamIconPath == null) {
            throw new IllegalStateException("Team icon not defined");
        }

        return new Team(initialNumberOfBots, spawningPoints, teamIconPath);
    }
}
