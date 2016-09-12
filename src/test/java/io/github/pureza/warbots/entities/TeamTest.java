package io.github.pureza.warbots.entities;

import io.github.pureza.warbots.Tests;
import io.github.pureza.warbots.game.Game;
import io.github.pureza.warbots.geometry.Point;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static io.github.pureza.warbots.Tests.mockGame;

public class TeamTest {

    /*
     * void spawnBotIfNecessary(Game game)
     */

    @Test
    public void spawnBotIfNecessaryDoesntSpawnTooManyBots() {
        Game game = Tests.mockGame();
        Team team = new Team(1, singletonList(Point.pt(1, 1)), "");

        // The first bot is spawned successfully
        team.spawnBotIfNecessary(game);
        ArgumentCaptor<Bot> spawnedBot = ArgumentCaptor.forClass(Bot.class);
        verify(game).addBot(spawnedBot.capture());

        when(game.getBots()).thenReturn(singletonList(spawnedBot.getValue()));

        // The second bot is not spawned
        team.spawnBotIfNecessary(game);
        verify(game, times(1)).addBot(Matchers.any());
    }


    @Test
    public void spawnBotIfNecessaryTriesNextSpawningPointIfPreviousIsOvercrowded() {
        Game game = Tests.mockGame();
        Team team = new Team(2, asList(Point.pt(1, 1), Point.pt(3, 3)), "");

        // The first bot is spawned successfully
        team.spawnBotIfNecessary(game);
        ArgumentCaptor<Bot> firstBot = ArgumentCaptor.forClass(Bot.class);
        verify(game, atLeastOnce()).addBot(firstBot.capture());
        assertThat(firstBot.getValue().getLocation(), org.hamcrest.Matchers.is(Point.pt(1, 1)));

        when(game.getBots()).thenReturn(singletonList(firstBot.getValue()));

        // The second bot is spawned at a different location
        team.spawnBotIfNecessary(game);

        ArgumentCaptor<Bot> secondBot = ArgumentCaptor.forClass(Bot.class);
        verify(game, atLeastOnce()).addBot(secondBot.capture());
        assertThat(secondBot.getAllValues().get(1).getLocation(), org.hamcrest.Matchers.is(Point.pt(3, 3)));
    }


    @Test
    public void spawnBotIfNecessarySpawnsBotAtSpawningPoint() {
        Game game = Tests.mockGame();
        Team team = new Team(10, singletonList(Point.pt(1, 1)), "");

        team.spawnBotIfNecessary(game);

        ArgumentCaptor<Bot> spawnedBot = ArgumentCaptor.forClass(Bot.class);
        verify(game).addBot(spawnedBot.capture());

        assertThat(spawnedBot.getValue().getLocation(), org.hamcrest.Matchers.is(Point.pt(1, 1)));
    }
}
