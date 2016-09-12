package io.github.pureza.warbots.collisions;

import io.github.pureza.warbots.entities.StaticEntity;
import io.github.pureza.warbots.weaponry.Projectile;

import java.util.Objects;

/**
 * A collision between a projectile and a wall
 */
public class ProjectileWallCollision extends Collision {

    public ProjectileWallCollision(Projectile projectile, StaticEntity wall) {
        super(projectile, wall);
    }


    @Override
    public void handle() {
        Projectile projectile = (Projectile) this.first;
        StaticEntity wall = (StaticEntity) this.second;
        projectile.hitStaticEntity(wall);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collision collision = (Collision) o;
        return Objects.equals(first, collision.first) &&
                Objects.equals(second, collision.second);
    }


    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
