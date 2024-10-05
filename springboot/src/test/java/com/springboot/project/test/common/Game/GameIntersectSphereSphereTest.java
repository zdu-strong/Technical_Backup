package com.springboot.project.test.common.Game;

import static org.junit.jupiter.api.Assertions.assertTrue;
import javax.media.j3d.BoundingSphere;
import javax.vecmath.Point3d;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class GameIntersectSphereSphereTest extends BaseTest {

    @Test
    public void test() {
        var sphereOne = new BoundingSphere(new Point3d(1, 1, 1), 1);
        var sphereTwo = new BoundingSphere(new Point3d(3, 1, 1), 1);
        var result = sphereOne.intersect(sphereTwo);
        assertTrue(result);
    }

}

