package io.github.pureza.warbots.weaponry;

import io.github.pureza.warbots.Matchers;
import io.github.pureza.warbots.Tests;
import io.github.pureza.warbots.entities.Bot;
import org.junit.Test;
import io.github.pureza.warbots.geometry.Vector;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static io.github.pureza.warbots.Tests.buildRocket;
import static io.github.pureza.warbots.Tests.mockBot;
import static io.github.pureza.warbots.Matchers.closeTo;

public class RocketTest {

    @Test
    public void rocketVelocityPointsCorrectly() {
        Bot bot = Tests.mockBot();

        Rocket rocket = Tests.buildRocket(bot.getGame(), io.github.pureza.warbots.geometry.Point.pt(0, 0), Math.PI);
        assertThat(rocket.getVelocity().normalize(), is(Matchers.closeTo(Vector.vec(-1, 0))));
    }


    @Test
    public void rocketSpeedIsCorrect() {
        Bot bot = Tests.mockBot();

        Rocket rocket = Tests.buildRocket(bot.getGame(), io.github.pureza.warbots.geometry.Point.pt(0, 0), Math.PI);
        assertThat(rocket.getVelocity().norm(), is(3.0));
    }


    @Test
    public void rocketStartsExplodingWhenHitsBot() {
        Bot bot = Tests.mockBot();

        Rocket rocket = Tests.buildRocket(bot.getGame(), io.github.pureza.warbots.geometry.Point.pt(0, 0), Math.PI);
        rocket.initResources();
        rocket.explode();
        assertThat(rocket.isExploding(), is(true));
    }


    @Test
    public void rocketStopsWhenExploding() {
        Bot bot = Tests.mockBot();

        Rocket rocket = Tests.buildRocket(bot.getGame(), io.github.pureza.warbots.geometry.Point.pt(0, 0), Math.PI);
        rocket.initResources();

        rocket.explode();

        assertThat(rocket.getVelocity(), is(Vector.vec(0, 0)));
    }


    @Test
    public void rocketExplosionStartsSmall() {
        Bot bot = Tests.mockBot();

        Rocket rocket = Tests.buildRocket(bot.getGame(), io.github.pureza.warbots.geometry.Point.pt(0, 0), Math.PI);
        rocket.initResources();
        rocket.explode();

        assertThat(rocket.getExplosionRadius(), is(0.1));
    }


    @Test
    public void rocketDamagesNearbyBotsOnExplosion() {
        Bot bot = Tests.mockBot();
        Bot neighbour = Tests.mockBot(bot.getLocation().displace(0.05, 0), bot.getHeadingVector(),
                bot.getBoundingRadius(), bot.getRotation());

        when(bot.getGame().getBotsInRange(anyObject(), anyDouble())).thenReturn(asList(bot, neighbour));

        Rocket rocket = Tests.buildRocket(bot.getGame(), io.github.pureza.warbots.geometry.Point.pt(0, 0), Math.PI);
        rocket.initResources();
        rocket.explode();

        verify(bot).inflictDamage(rocket, rocket.getDamage());

        // The bot is 0.05 away from the rocket, so it inflicts 0.95% of damage
        verify(neighbour).inflictDamage(rocket, (int) (0.95 * rocket.getDamage()));
    }


    @Test
    public void explosionGrowsInitially() {
        Bot bot = Tests.mockBot();

        Rocket rocket = Tests.buildRocket(bot.getGame(), io.github.pureza.warbots.geometry.Point.pt(0, 0), Math.PI);
        rocket.initResources();
        rocket.explode();

        assertThat(rocket.getExplosionRadius(), is(0.1));

        rocket.update(15);

        assertThat(rocket.getExplosionRadius(), is(0.2));
    }


    @Test
    public void explosionFadesEventually() {
        Bot bot = Tests.mockBot();

        Rocket rocket = Tests.buildRocket(bot.getGame(), io.github.pureza.warbots.geometry.Point.pt(0, 0), Math.PI);
        rocket.initResources();
        rocket.explode();

        assertThat(rocket.getExplosionRadius(), is(0.1));

        // Maximum radius
        rocket.update(135);
        assertThat(rocket.getExplosionRadius(), is(1.0));

        // Decrease
        rocket.update(30);
        assertThat(rocket.getExplosionRadius(), is(0.9));
    }


    @Test
    public void rocketIsRemovedFromTheWorldAfterExplosion() {
        Bot bot = Tests.mockBot();

        Rocket rocket = Tests.buildRocket(bot.getGame(), io.github.pureza.warbots.geometry.Point.pt(0, 0), Math.PI);
        rocket.initResources();
        rocket.explode();

        assertThat(rocket.getExplosionRadius(), is(0.1));

        // Maximum radius
        rocket.update(135);
        assertThat(rocket.getExplosionRadius(), is(1.0));

        // Decrease
        rocket.update(1000);
        assertThat(rocket.getExplosionRadius(), is(lessThan(0.1)));
        verify(bot.getGame()).removeProjectile(rocket);
    }
}
