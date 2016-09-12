package io.github.pureza.warbots;

import io.github.pureza.warbots.entities.*;
import io.github.pureza.warbots.game.Game;
import io.github.pureza.warbots.geometry.Vector;
import io.github.pureza.warbots.memory.Memory;
import io.github.pureza.warbots.weaponry.*;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.navigation.Map;

import java.awt.image.BufferedImage;
import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static io.github.pureza.warbots.geometry.Point.pt;

public class Tests {

    public static Bot mockBot() {
        return mockBot(pt(0, 0));
    }


    public static Bot mockBot(Point location) {
        return mockBot(location, Vector.vec(1, 0), 0.3, 0.0);
    }


    public static Bot mockBot(Point location, Vector headingVector, double boundingRadius, double rotation) {
        Bot bot = mock(Bot.class);
        Game game = mockGame();
        when(bot.getGame()).thenReturn(game);
        when(bot.getRotation()).thenReturn(rotation);
        when(bot.getAimNoise()).thenReturn(1.0);
        when(bot.getBoundingRadius()).thenReturn(boundingRadius);
        when(bot.getHeadingVector()).thenReturn(headingVector);
        when(bot.getLocation()).thenReturn(location);
        when(bot.isDead()).thenReturn(false);
        when(bot.getMaxSpeed()).thenReturn(2.0);

        Memory memory = new Memory(bot);
        when(bot.getMemory()).thenReturn(memory);
        return bot;
    }


    public static Game mockGame() {
        return mockGame(mockMap());
    }


    public static Game mockGame(Map map) {
        Game game = mock(Game.class);
        when(game.getImage(any())).thenReturn(mock(BufferedImage.class));
        when(game.getMap()).thenReturn(map);
        when(game.getConfig()).thenReturn(new TestConfig());

        return game;
    }


    public static Map mockMap() {
        Map map = mock(Map.class);
        when (map.getItems()).thenReturn(Collections.emptyList());
        when (map.getWeaponItems()).thenReturn(Collections.emptyList());
        when (map.getFirstAidItems()).thenReturn(Collections.emptyList());

        return map;
    }


    public static Team mockTeam() {
        return new Team(0, Collections.emptyList(), "");
    }


    public static Bot buildBot(Game game, Point location, Team team) {
        return new BotBuilder(new TestConfig()).build(game, location, team);
    }


    public static FirstAidItem buildFirstAidItem(Point location) {
        return new FirstAidItemBuilder(new TestConfig()).build(location);
    }


    public static WeaponItem buildWeaponItem(Point location, Weapon.WeaponType weaponType) {
        return new WeaponItemBuilder(new TestConfig()).build(location, weaponType);
    }


    public static Bullet buildBullet(Game game, Point location) {
        return buildBullet(game, location, 0);
    }


    public static Bullet buildBullet(Game game, Point location, double rotation) {
        Bot bot = new BotBuilder(new TestConfig())
                .setAimNoise(0)
                .build(game, location, mockTeam());

        bot.setRotation(rotation);
        return (Bullet) new HandGunBuilder(new TestConfig())
                .build(bot)
                .createProjectile();
    }


    public static LaserRay buildLaserRay(Game game, Point location) {
        return buildLaserRay(game, location, 0);
    }


    public static LaserRay buildLaserRay(Game game, Point location, double rotation) {
        Bot bot = new BotBuilder(new TestConfig())
                .setAimNoise(0)
                .build(game, location, mockTeam());

        bot.setRotation(rotation);

        return (LaserRay) new LaserGunBuilder(new TestConfig())
                .build(bot)
                .createProjectile();
    }


    public static Rocket buildRocket(Game game, Point location) {
        return buildRocket(game, location, 0);
    }


    public static Rocket buildRocket(Game game, Point location, double rotation) {
        Bot bot = new BotBuilder(new TestConfig())
                .setAimNoise(0)
                .build(game, location, mockTeam());

        bot.setRotation(rotation);

        return (Rocket) new RocketLauncherBuilder(new TestConfig())
                .build(bot)
                .createProjectile();
    }


    public static HandGun buildHandGun(Bot owner) {
        return (HandGun) new HandGunBuilder(new TestConfig()).build(owner);
    }


    public static LaserGun buildLaserGun(Bot owner) {
        return (LaserGun) new LaserGunBuilder(new TestConfig()).build(owner);
    }


    public static RocketLauncher buildRocketLauncher(Bot owner) {
        return (RocketLauncher) new RocketLauncherBuilder(new TestConfig()).build(owner);
    }
}
