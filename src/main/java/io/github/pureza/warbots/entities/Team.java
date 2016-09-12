package io.github.pureza.warbots.entities;

import io.github.pureza.warbots.game.Game;
import io.github.pureza.warbots.geometry.Point;

import java.util.List;

/**
 * A team
 */
public class Team {

    /** Initial number of bots on this team */
    private final int initialNumberOfBots;

    /** The path to the icon to be used by bots on this team */
    private final String teamIconPath;

    /** Spawning points for bots in this team */
    private final List<Point> spawningPoints;

    /** Number of spawned bots */
    private int spawnedBots;


    public Team(int initialNumberOfBots, List<Point> spawningPoints, String teamIconPath) {
        this.initialNumberOfBots = initialNumberOfBots;
        this.spawningPoints = spawningPoints;
        this.teamIconPath = teamIconPath;
    }


    /**
     * Spawns a new bot, if not enough bots have been spawned yet and any of the
     * spawning points is free
     */
    public void spawnBotIfNecessary(Game game) {
        if (spawnedBots >= initialNumberOfBots) {
            return;
        }

        spawningPoints.stream()
                .filter(point ->
                        game.getBots().stream()
                                .allMatch(bot -> bot.getLocation().distanceTo(point) > bot.getBoundingRadius() * 4))
                .findAny()
                .ifPresent(spawnPoint -> {
                    BotBuilder botBuilder = new BotBuilder(game.getConfig());
                    Bot bot = botBuilder.build(game, spawnPoint, this);
                    bot.initResources();
                    game.addBot(bot);
                    spawnedBots++;
                });
    }


    /**
     * Returns the path to the icon to be used by the bots on this team
     */
    public String getTeamIconPath() {
        return this.teamIconPath;
    }


    /**
     * Returns the initial number of bots on this team
     */
    public int getInitialNumberOfBots() {
        return initialNumberOfBots;
    }


    /**
     * Returns the spawning points for bots in this team
     */
    public List<Point> getSpawningPoints() {
        return spawningPoints;
    }
}
