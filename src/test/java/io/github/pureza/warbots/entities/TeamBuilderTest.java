package io.github.pureza.warbots.entities;

import io.github.pureza.warbots.geometry.Point;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TeamBuilderTest {

    /*
     * Team build()
     */

    @Test
    public void buildBuildsTeam() {
        Team team = new TeamBuilder()
                .setInitialNumberOfBots(10)
                .setTeamIconPath("/path/to/icon")
                .addSpawningPoint(Point.pt(1, 1))
                .addSpawningPoint(Point.pt(2, 2))
                .build();

        assertThat(team.getTeamIconPath(), is("/path/to/icon"));
        assertThat(team.getInitialNumberOfBots(), is(10));
        assertThat(team.getSpawningPoints(), Matchers.containsInAnyOrder(Point.pt(1, 1), Point.pt(2, 2)));
    }


    @Test(expected=IllegalStateException.class)
    public void buildsFailIfInitialNumberOfBotsIsNotSpecified() {
        new TeamBuilder()
                .setTeamIconPath("/path/to/icon")
                .addSpawningPoint(Point.pt(1, 1))
                .addSpawningPoint(Point.pt(2, 2))
                .build();
    }


    @Test(expected=IllegalStateException.class)
    public void buildsFailIfSpawningPointsAreNotDefined() {
        new TeamBuilder()
                .setInitialNumberOfBots(10)
                .setTeamIconPath("/path/to/icon")
                .build();
    }


    @Test(expected=IllegalStateException.class)
    public void buildsFailIfTeamIconIsNotSpecified() {
        new TeamBuilder()
                .setInitialNumberOfBots(10)
                .addSpawningPoint(Point.pt(1, 1))
                .addSpawningPoint(Point.pt(2, 2))
                .build();
    }
}
