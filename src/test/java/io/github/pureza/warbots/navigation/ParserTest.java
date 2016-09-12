package io.github.pureza.warbots.navigation;

import io.github.pureza.warbots.TestConfig;
import io.github.pureza.warbots.entities.FirstAidItem;
import io.github.pureza.warbots.game.Game;
import io.github.pureza.warbots.geometry.Point;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;
import io.github.pureza.warbots.entities.TeamBuilder;
import io.github.pureza.warbots.entities.WeaponItem;

import java.io.IOException;
import java.io.StringReader;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

public class ParserTest {

    private Parser parser = new Parser(new TestConfig());


    /*
     * Pair<Integer, Integer> parseBots(String arguments)
     */

    @Test(expected=IllegalArgumentException.class)
    public void parseBotsFailsWhenNoArgumentsSupplied() {
        TeamBuilder teamBuilderA = new TeamBuilder();
        TeamBuilder teamBuilderB = new TeamBuilder();
        parser.parseBots("", teamBuilderA, teamBuilderB);
    }


    @Test(expected=IllegalArgumentException.class)
    public void parseBotsFailsOnIllegalArguments() {
        TeamBuilder teamBuilderA = new TeamBuilder();
        TeamBuilder teamBuilderB = new TeamBuilder();
        parser.parseBots("this is illegal", teamBuilderA, teamBuilderB);
    }


    @Test
    public void parseBotsInitializesNumberOfBotsInEachTeam() {
        TeamBuilder teamBuilderA = new TeamBuilder();
        TeamBuilder teamBuilderB = new TeamBuilder();
        parser.parseBots("5x3", teamBuilderA, teamBuilderB);
        assertThat(teamBuilderA.getInitialNumberOfBots(), is(5));
        assertThat(teamBuilderB.getInitialNumberOfBots(), is(3));
    }


    /*
     * parseSpawn(String arguments, TeamBuilder teamBuilderA, TeamBuilder teamBuilderB)
     */

    @Test(expected=IllegalArgumentException.class)
    public void parseSpawnFailsOnNotEnoughArguments() {
        TeamBuilder teamBuilderA = new TeamBuilder();
        TeamBuilder teamBuilderB = new TeamBuilder();
        parser.parseSpawn("A", teamBuilderA, teamBuilderB);
    }


    @Test
    public void parseSpawnIgnoresExcessArguments() {
        TeamBuilder teamBuilderA = new TeamBuilder();
        TeamBuilder teamBuilderB = new TeamBuilder();
        parser.parseSpawn("A (1, 1) (2, 2)", teamBuilderA, teamBuilderB);

        assertThat(teamBuilderA.getSpawningPoints(), Matchers.contains(Point.pt(1.5, 1.5)));
    }


    @Test(expected=IllegalArgumentException.class)
    public void parseSpawnFailsOnIllegalTeamName() {
        TeamBuilder teamBuilderA = new TeamBuilder();
        TeamBuilder teamBuilderB = new TeamBuilder();
        parser.parseSpawn("X (1, 1)", teamBuilderA, teamBuilderB);
    }


    @Test
    public void parseSpawnAddsSpawningPointToTeamA() {
        TeamBuilder teamBuilderA = new TeamBuilder();
        TeamBuilder teamBuilderB = new TeamBuilder();
        parser.parseSpawn("A (1, 1)", teamBuilderA, teamBuilderB);
        parser.parseSpawn("A (2, 2)", teamBuilderA, teamBuilderB);

        assertThat(teamBuilderA.getSpawningPoints(), Matchers.contains(Point.pt(1.5, 1.5), Point.pt(2.5, 2.5)));
    }


    @Test
    public void parseSpawnAddsSpawningPointToTeamB() {
        TeamBuilder teamBuilderA = new TeamBuilder();
        TeamBuilder teamBuilderB = new TeamBuilder();
        parser.parseSpawn("B (1, 1)", teamBuilderA, teamBuilderB);
        parser.parseSpawn("B (2, 2)", teamBuilderA, teamBuilderB);

        assertThat(teamBuilderB.getSpawningPoints(), Matchers.contains(Point.pt(1.5, 1.5), Point.pt(2.5, 2.5)));
    }


    /*
     * Map parseCells(String arguments)
     */

    @Test(expected=IllegalArgumentException.class)
    public void parseCellsFailsWhenNoArgumentsSupplied() {
        parser.parseCells("", new MapBuilder(new TestConfig()));
    }


    @Test(expected=IllegalArgumentException.class)
    public void parseCellsFailsOnIllegalArguments() {
        parser.parseCells("this is illegal", new MapBuilder(new TestConfig()));
    }


    @Test
    public void parseCellsCreatesEmptyMapWithTheRightSize() {
        MapBuilder builder = new MapBuilder(new TestConfig());
        parser.parseCells("5x3", builder);
        assertThat(builder.width(), is(5));
        assertThat(builder.height(), is(3));
    }


    /*
     * void parseFirstAidKit(Map map, String arguments)
     */

    @Test(expected=IllegalArgumentException.class)
    public void parseFirstAidKitFailsWhenNoArgumentsSupplied() {
        MapBuilder builder = new MapBuilder(new TestConfig());
        parser.parseCells("5x3", builder);
        parser.parseFirstAidKit("", builder);
    }


    @Test(expected=IllegalArgumentException.class)
    public void parseFirstAidKitFailsOnIllegalArguments() {
        MapBuilder builder = new MapBuilder(new TestConfig());
        parser.parseCells("5x3", builder);
        parser.parseFirstAidKit("this is not a point", builder);
    }


    @Test
    public void parseFirstAidKitAddsFirstAidKitToEmptyCell() {
        MapBuilder builder = new MapBuilder(new TestConfig());
        parser.parseCells("5x3", builder);
        parser.parseFirstAidKit("(1, 1)", builder);
        Map map = builder.build();
        assertThat(map.cellAt(1, 1).getItem(), is(instanceOf(FirstAidItem.class)));
    }


    /*
     * void parseWeapon(Map map, String arguments)
     */

    @Test(expected=IllegalArgumentException.class)
    public void parseWeaponFailsWhenNoArgumentsSupplied() {
        MapBuilder builder = new MapBuilder(new TestConfig());
        parser.parseCells("5x3", builder);
        parser.parseWeapon("", builder);
    }


    @Test(expected=IllegalArgumentException.class)
    public void parseWeaponFailsOnIllegalArguments() {
        MapBuilder builder = new MapBuilder(new TestConfig());
        parser.parseCells("5x3", builder);
        parser.parseWeapon("this is not a point", builder);
    }


    @Test(expected=IllegalArgumentException.class)
    public void parseWeaponFailsOnIllegalWeapon() {
        MapBuilder builder = new MapBuilder(new TestConfig());
        parser.parseCells("5x3", builder);
        parser.parseWeapon("knife (2, 2)", builder);
    }


    @Test(expected=IllegalArgumentException.class)
    public void parseWeaponFailsOnIllegalPoint() {
        MapBuilder builder = new MapBuilder(new TestConfig());
        parser.parseCells("5x3", builder);
        parser.parseWeapon("handgun (a, b)", builder);
    }


    @Test
    public void parseWeaponIgnoresTooManyArguments() {
        MapBuilder builder = new MapBuilder(new TestConfig());
        parser.parseCells("5x3", builder);
        parser.parseWeapon("handgun (1, 2) (2, 0)", builder);
        Map map = builder.build();
        assertThat(map.getItemAt(Point.pt(1, 2)), is(instanceOf(WeaponItem.class)));
        assertThat(map.getItemAt(Point.pt(2, 0)), is(nullValue()));
    }


    @Test
    public void parseWeaponAddsWeaponToEmptyCell() {
        MapBuilder builder = new MapBuilder(new TestConfig());
        parser.parseCells("5x3", builder);
        parser.parseWeapon("laser_gun (1, 1)", builder);
        Map map = builder.build();
        assertThat(map.cellAt(1, 1).getItem(), is(instanceOf(WeaponItem.class)));
    }


    /*
     * void parseWall(Map map, String arguments)
     */

    @Test(expected=IllegalArgumentException.class)
    public void parseWallFailsWhenNoArgumentsSupplied() {
        MapBuilder builder = new MapBuilder(new TestConfig());
        parser.parseCells("5x3", builder);
        parser.parseWall("", builder);
    }


    @Test(expected=IllegalArgumentException.class)
    public void parseWallFailsOnIllegalArguments() {
        MapBuilder builder = new MapBuilder(new TestConfig());
        parser.parseCells("5x3", builder);
        parser.parseWall("this is invalid", builder);
    }


    @Test(expected=IllegalArgumentException.class)
    public void parseWallFailsWhenNoSizeGiven() {
        MapBuilder builder = new MapBuilder(new TestConfig());
        parser.parseCells("5x3", builder);
        parser.parseWall("(1, 2)", builder);
    }


    @Test(expected=IllegalArgumentException.class)
    public void parseWallFailsOnIllegalLocation() {
        MapBuilder builder = new MapBuilder(new TestConfig());
        parser.parseCells("5x3", builder);
        parser.parseWall("(a, b) (1, 2)", builder);
    }


    @Test(expected=IllegalArgumentException.class)
    public void parseWallFailsOnIllegalSize() {
        MapBuilder builder = new MapBuilder(new TestConfig());
        parser.parseCells("5x3", builder);
        parser.parseWall("(1, 2) (a, b)", builder);
    }


    @Test
    public void parseWallCreatesWall() {
        MapBuilder builder = new MapBuilder(new TestConfig());
        parser.parseCells("5x3", builder);
        parser.parseWall("(1, 2) (1, 1)", builder);

        Map map = builder.build();
        assertThat(map.cellAt(1, 2).isFree(), is(false));
    }


    /*
     * Point stringToPoint(String ptStr)
     */

    @Test
    public void stringToPointIgnoresSuffix() {
        assertThat(parser.stringToPoint("(1, 2) hello"), Is.is(Point.pt(1, 2)));
    }


    @Test
    public void stringToPointParsesPoint() {
        assertThat(parser.stringToPoint("(1, 2)"), Is.is(Point.pt(1, 2)));
    }


    @Test(expected=IllegalArgumentException.class)
    public void stringToPointFailsOnInvalidPoint() {
        parser.stringToPoint("blah");
    }


    /*
     * Map parse(Reader reader)
     */

    @Test
    public void parseIgnoresEmptyLines() throws IOException {
        Game game = parser.parse(new StringReader("bots 1x1\n\n\ncells 3x2\nspawn a (2, 2)\nspawn b (1, 2)\n"));
        Map map = game.getMap();
        assertThat(map.width(), is(3));
        assertThat(map.height(), is(2));

        assertThat(game.getTeamA().getInitialNumberOfBots(), is(1));
        assertThat(game.getTeamA().getSpawningPoints(), Matchers.contains(Point.pt(2.5, 2.5)));

        assertThat(game.getTeamB().getInitialNumberOfBots(), is(1));
        assertThat(game.getTeamB().getSpawningPoints(), Matchers.contains(Point.pt(1.5, 2.5)));
    }


    @Test
    public void parseIgnoresComments() throws IOException {
        Game game = parser.parse(new StringReader("bots 1x1\ncells 3x2\nspawn a (2, 2)\nspawn b (1, 2)\n#this is a comment"));
        Map map = game.getMap();

        assertThat(map.width(), is(3));
        assertThat(map.height(), is(2));

        assertThat(game.getTeamA().getInitialNumberOfBots(), is(1));
        assertThat(game.getTeamA().getSpawningPoints(), Matchers.contains(Point.pt(2.5, 2.5)));

        assertThat(game.getTeamB().getInitialNumberOfBots(), is(1));
        assertThat(game.getTeamB().getSpawningPoints(), Matchers.contains(Point.pt(1.5, 2.5)));
    }


    @Test
    public void parseParsesBotsCommand() throws IOException {
        Game game = parser.parse(new StringReader("bots 1x5\ncells 3x2\nspawn a (2, 2)\nspawn b (1, 2)"));

        assertThat(game.getTeamA().getInitialNumberOfBots(), is(1));
        assertThat(game.getTeamB().getInitialNumberOfBots(), is(5));
    }


    @Test
    public void parseParsesSpawnCommands() throws IOException {
        Game game = parser.parse(new StringReader("bots 1x5\ncells 3x2\nspawn a (2, 2)\nspawn b (1, 2)"));

        assertThat(game.getTeamA().getSpawningPoints(), Matchers.contains(Point.pt(2.5, 2.5)));
        assertThat(game.getTeamB().getSpawningPoints(), Matchers.contains(Point.pt(1.5, 2.5)));
    }


    @Test
    public void parseParsesMultipleSpawnCommands() throws IOException {
        Game game = parser.parse(new StringReader("bots 1x5\ncells 3x2\nspawn a (2, 2)\nspawn b (1, 1)\nspawn b (1, 2)\nspawn a (2, 1)"));

        assertThat(game.getTeamA().getSpawningPoints(), Matchers.containsInAnyOrder(Point.pt(2.5, 2.5), Point.pt(2.5, 1.5)));
        assertThat(game.getTeamB().getSpawningPoints(), Matchers.containsInAnyOrder(Point.pt(1.5, 2.5), Point.pt(1.5, 1.5)));
    }


    @Test
    public void parseParsesCellsCommand() throws IOException {
        Game game = parser.parse(new StringReader("bots 1x1\ncells 3x2\nspawn a (2, 2)\nspawn b (1, 2)"));
        Map map = game.getMap();

        assertThat(map.width(), is(3));
        assertThat(map.height(), is(2));
    }


    @Test
    public void parseOverwritesPreviousCellsCommands() throws IOException {
        Game game = parser.parse(new StringReader("bots 1x1\ncells 3x2\ncells 3x2\nspawn a (2, 2)\nspawn b (1, 2)"));
        Map map = game.getMap();

        assertThat(map.width(), is(3));
        assertThat(map.height(), is(2));
    }


    @Test
    public void parseOverwritesPreviousBotsCommands() throws IOException {
        Game game = parser.parse(new StringReader("bots 1x1\nbots 3x2\ncells 3x2\nspawn a (2, 2)\nspawn b (1, 2)"));
        assertThat(game.getTeamA().getInitialNumberOfBots(), is(3));
        assertThat(game.getTeamB().getInitialNumberOfBots(), is(2));
    }


    @Test
    public void parseParsesWallCommand() throws IOException {
        Game game = parser.parse(new StringReader("bots 3x3\ncells 3x2\nspawn a (2, 2)\nspawn b (1, 2)\nwall (1, 1) (1, 1)"));
        Map map = game.getMap();

        assertThat(map.cellAt(1, 1).isFree(), is(false));
    }


    @Test
    public void parseParsesMultipleWallCommands() throws IOException {
        Game game = parser.parse(new StringReader("bots 3x3\ncells 3x2\nspawn a (2, 2)\nspawn b (1, 2)\nwall (1, 1) (1, 1)\nwall (0, 0) (1, 1)"));
        Map map = game.getMap();

        assertThat(map.cellAt(1, 1).isFree(), is(false));
        assertThat(map.cellAt(0, 0).isFree(), is(false));
    }


    @Test
    public void parseParsesFirstAidCommand() throws IOException {
        Game game = parser.parse(new StringReader("bots 3x3\ncells 3x2\nspawn a (2, 2)\nspawn b (1, 2)\nfirst-aid (1, 1)"));
        Map map = game.getMap();

        assertThat(map.cellAt(1, 1).getItem(), is(instanceOf(FirstAidItem.class)));
    }


    @Test
    public void parseParsesMultipleFirstAidCommands() throws IOException {
        Game game = parser.parse(new StringReader("bots 3x3\ncells 3x2\nspawn a (2, 2)\nspawn b (1, 2)\nweapon handgun (1, 1)\nweapon rocket_launcher (0, 0)"));
        Map map = game.getMap();

        assertThat(map.cellAt(1, 1).getItem(), is(instanceOf(WeaponItem.class)));
        assertThat(map.cellAt(1, 1).getItem(), is(instanceOf(WeaponItem.class)));
    }


    @Test
    public void parseSkipsUnknownCommands() throws IOException {
        Game game = parser.parse(new StringReader("hello\nbots 3x3\ncells 3x2\nspawn a (2, 2)\nspawn b (1, 2)\nxpto 1\nweapon handgun (1, 1)\nweapon rocket_launcher (0, 0)"));
        Map map = game.getMap();

        assertThat(map.cellAt(1, 1).getItem(), is(instanceOf(WeaponItem.class)));
        assertThat(map.cellAt(1, 1).getItem(), is(instanceOf(WeaponItem.class)));
    }


    @Test
    public void parseSkipsMalformedLines() throws IOException {
        Game game = parser.parse(new StringReader("1234\nbots 3x3\ncells 3x2\nspawn a (2, 2)\nspawn b (1, 2)\n----123 1\nweapon handgun (1, 1)\nweapon rocket_launcher (0, 0)"));
        Map map = game.getMap();

        assertThat(map.cellAt(1, 1).getItem(), is(instanceOf(WeaponItem.class)));
        assertThat(map.cellAt(1, 1).getItem(), is(instanceOf(WeaponItem.class)));
    }


    @Test
    public void parseParsesTrimsCommands() throws IOException {
        Game game = parser.parse(new StringReader(" cells      3x2\nbots 3x3\nspawn a (2, 2)   \n    spawn b (1, 2)\n    weapon handgun (1, 1)\n  weapon rocket_launcher (0, 0)"));
        Map map = game.getMap();

        assertThat(map.cellAt(1, 1).getItem(), is(instanceOf(WeaponItem.class)));
        assertThat(map.cellAt(1, 1).getItem(), is(instanceOf(WeaponItem.class)));
    }
}
