package io.github.pureza.warbots.collisions;

import io.github.pureza.warbots.entities.Wall;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.geometry.Size;
import io.github.pureza.warbots.weaponry.Projectile;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ProjectileWallCollisionTest {

    @Test
    public void projectileHitsWall() {
        Projectile projectile = mock(Projectile.class);
        Wall wall = new Wall(Point.pt(1, 1), new Size(1, 1));

        ProjectileWallCollision collision = new ProjectileWallCollision(projectile, wall);
        collision.handle();

        verify(projectile).hitStaticEntity(wall);
    }
}
