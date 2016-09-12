package io.github.pureza.warbots.entities;

import io.github.pureza.warbots.TestConfig;
import io.github.pureza.warbots.Tests;
import io.github.pureza.warbots.config.Config;
import io.github.pureza.warbots.game.Game;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.weaponry.HandGun;
import io.github.pureza.warbots.weaponry.LaserGun;
import io.github.pureza.warbots.weaponry.Weapon;
import org.hamcrest.Matchers;
import org.junit.Test;
import io.github.pureza.warbots.weaponry.RocketLauncher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static io.github.pureza.warbots.Tests.mockGame;

public class BotBuilderTest {

    /*
     * Bot build(Game game, Point location, Team team)
     */

    @Test
    public void builderBuildsBotWithGivenMaxSpeed() {
        Game game = Tests.mockGame();
        Bot bot = new BotBuilder(new TestConfig())
                .setMaxSpeed(9999)
                .build(game, Point.pt(1, 1), Tests.mockTeam());

        assertThat(bot.getMaxSpeed(), is(9999.0));
    }


    @Test
    public void builderBuildsBotWithDefaultMaxSpeed() {
        Game game = Tests.mockGame();
        Config config = new TestConfig();
        Bot bot = new BotBuilder(config)
                .build(game, Point.pt(1, 1), Tests.mockTeam());

        assertThat(bot.getMaxSpeed(), Matchers.is(config.botConfig().maxSpeed()));
    }


    @Test
    public void builderBuildsBotWithGivenMaxTurnRate() {
        Game game = Tests.mockGame();
        Bot bot = new BotBuilder(new TestConfig())
                .setMaxTurnRate(9999)
                .build(game, Point.pt(1, 1), Tests.mockTeam());

        assertThat(bot.getMaxTurnRate(), is(9999.0));
    }


    @Test
    public void builderBuildsBotWithDefaultMaxTurnRate() {
        Game game = Tests.mockGame();
        Config config = new TestConfig();
        Bot bot = new BotBuilder(config)
                .build(game, Point.pt(1, 1), Tests.mockTeam());

        assertThat(bot.getMaxTurnRate(), Matchers.is(config.botConfig().maxTurnRate()));
    }


    @Test
    public void builderBuildsBotWithGivenBoundingRadius() {
        Game game = Tests.mockGame();
        Bot bot = new BotBuilder(new TestConfig())
                .setBoundingRadius(9999)
                .build(game, Point.pt(1, 1), Tests.mockTeam());

        assertThat(bot.getBoundingRadius(), is(9999.0));
    }


    @Test
    public void builderBuildsBotWithDefaultBoundingRadius() {
        Game game = Tests.mockGame();
        Config config = new TestConfig();
        Bot bot = new BotBuilder(config)
                .build(game, Point.pt(1, 1), Tests.mockTeam());

        assertThat(bot.getBoundingRadius(), Matchers.is(config.botConfig().boundingRadius()));
    }


    @Test
    public void builderBuildsBotWithGivenAimNoise() {
        Game game = Tests.mockGame();
        Bot bot = new BotBuilder(new TestConfig())
                .setAimNoise(9999)
                .build(game, Point.pt(1, 1), Tests.mockTeam());

        assertThat(bot.getAimNoise(), is(9999.0));
    }


    @Test
    public void builderBuildsBotWithDefaultAimNoise() {
        Game game = Tests.mockGame();
        Config config = new TestConfig();
        Bot bot = new BotBuilder(config)
                .build(game, Point.pt(1, 1), Tests.mockTeam());

        assertThat(bot.getAimNoise(), Matchers.is(config.botConfig().aimNoise()));
    }


    @Test
    public void builderInitializesBotWithHandGun() {
        Game game = Tests.mockGame();
        Config config = new TestConfig();
        Bot bot = new BotBuilder(config)
                .build(game, Point.pt(1, 1), Tests.mockTeam());

        HandGun handGun = (HandGun) bot.getArtillery().getWeapons().get(Weapon.WeaponType.HANDGUN);
        assertThat(handGun.getRemainingAmmo(), is(config.weapons().get(Weapon.WeaponType.HANDGUN).initialAmmo()));
    }


    @Test
    public void builderInitializesBotWithLaserGun() {
        Game game = Tests.mockGame();
        Config config = new TestConfig();
        Bot bot = new BotBuilder(config)
                .build(game, Point.pt(1, 1), Tests.mockTeam());

        LaserGun laserGun = (LaserGun) bot.getArtillery().getWeapons().get(Weapon.WeaponType.LASER_GUN);
        assertThat(laserGun.getRemainingAmmo(), is(config.weapons().get(Weapon.WeaponType.LASER_GUN).initialAmmo()));
    }


    @Test
    public void builderInitializesBotWithRocketLauncher() {
        Game game = Tests.mockGame();
        Config config = new TestConfig();
        Bot bot = new BotBuilder(config)
                .build(game, Point.pt(1, 1), Tests.mockTeam());

        RocketLauncher rocketLauncher = (RocketLauncher) bot.getArtillery().getWeapons().get(Weapon.WeaponType.ROCKET_LAUNCHER);
        assertThat(rocketLauncher.getRemainingAmmo(), is(config.weapons().get(Weapon.WeaponType.ROCKET_LAUNCHER).initialAmmo()));
    }
}
