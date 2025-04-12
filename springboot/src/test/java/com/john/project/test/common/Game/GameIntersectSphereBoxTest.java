package com.john.project.test.common.Game;

import static org.junit.jupiter.api.Assertions.assertTrue;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.BoundingSphere;
import javax.vecmath.Point3d;
import org.junit.jupiter.api.Test;

import com.john.project.test.common.BaseTest.BaseTest;

public class GameIntersectSphereBoxTest extends BaseTest {

    @Test
    public void test() {
        var sphere = new BoundingSphere(new Point3d(1, 1, 1), 1);
        var box = new BoundingBox(new Point3d(2, 1, 1), new Point3d(4, 3, 3));
        var result = sphere.intersect(box);
        assertTrue(result);
    }

}
