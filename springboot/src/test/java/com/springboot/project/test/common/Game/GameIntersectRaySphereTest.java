package com.springboot.project.test.common.Game;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.math.collision.Sphere;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class GameIntersectRaySphereTest extends BaseTest {

    @Test
    public void test() {
        var ray = new Ray(new Vector3(0, 10, 10), new Vector3(0, -1, -1));
        var sphere = new Sphere(new Vector3(1, 1, 1), 1);
        var result = Intersector.intersectRaySphere(ray, sphere.center, sphere.radius, null);
        assertTrue(result);
    }

}
