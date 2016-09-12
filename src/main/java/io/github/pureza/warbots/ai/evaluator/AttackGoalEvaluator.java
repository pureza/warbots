package io.github.pureza.warbots.ai.evaluator;

import io.github.pureza.warbots.ai.goal.Goal;
import io.github.pureza.warbots.ai.goal.AttackGoal;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.memory.BotMemoryRecord;
import io.github.pureza.warbots.util.Pair;


/**
 * Evaluates the desirability of attacking the enemy
 */
public class AttackGoalEvaluator extends GoalEvaluator {

    public AttackGoalEvaluator(Bot bot) {
        super(bot);
    }


    @Override
    public double desirability() {
        Bot target = closestEnemy();

        // If I don't know where my enemies are, don't attack them
        if (target == null) {
            return 0;
        }

        BotMemoryRecord record = bot.getMemory().getBotRecord(target);
        Point lastKnownLocation = record.getLastKnownLocation();

        // Arbitrary multiplier, chosen empirically
        double k = 10;
        double distance = bot.getLocation().distanceTo(lastKnownLocation);

        // Desirability increases with health and weapon strength, and
        // decreases with distance to target
        return k * bot.getHealth() / 100.0 * bot.getTotalWeaponStrength() / distance;
    }


    @Override
    public AttackGoal makeGoal() {
        return new AttackGoal(bot, closestEnemy());
    }


    @Override
    public boolean shouldReplace(Goal currentGoal, Goal alternativeGoal) {
        assert alternativeGoal instanceof AttackGoal;

        if (currentGoal instanceof AttackGoal) {
            AttackGoal current = (AttackGoal) currentGoal;
            AttackGoal alternative = (AttackGoal) alternativeGoal;

            Bot currentTarget = current.getTarget();
            Bot alternativeTarget = alternative.getTarget();

            if (currentTarget != alternativeTarget) {
                // Attack another enemy if the current one is not visible anymore
                if (!bot.isInFov(currentTarget)) {
                    return true;
                }
            }

            // Otherwise, keep attacking the same bot
            return false;
        }

        return true;
    }


    /**
     * Returns the closest enemy taking into consideration the location where I
     * last saw him and the amount of time that has passed since I've seen him
     */
    private Bot closestEnemy() {
        return bot.getMemory().getBotMemory()
                .getRecords()
                .entrySet()
                .stream()
                .map(entry -> {
                    Bot enemy = entry.getKey();
                    BotMemoryRecord record = entry.getValue();

                    Point lastKnownLocation = record.getLastKnownLocation();
                    long timeSinceLastSeen = record.getTimeSinceLastSeen();

                    double distance = bot.getLocation().distanceTo(lastKnownLocation);

                    // Arbitrary constant so distances and times are more or
                    // less on the same order of magnitude
                    double k = 10;

                    // Put less weight on targets that are far away or that we
                    // haven't seen for a long time
                    double effort = distance * k + timeSinceLastSeen;
                    return Pair.of(enemy, effort);
                })
                .min((a, b) -> Double.compare(a.second(), b.second()))
                .map(Pair::first)
                .orElse(null);
    }
}
