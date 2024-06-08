package com.springboot.project.test.common.Game;

import javax.media.j3d.BoundingBox;
import javax.media.j3d.Transform3D;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.common.BaseTest.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameRotateBoxOfJava3dTest extends BaseTest {

    @Test
    public void test() {
        var boundingBox = new BoundingBox(new Point3d(0, 0, 0), new Point3d(2, 2, 2));
        {
            // Move to the center of the coordinate system
            var firstPoint = new Point3d();
            boundingBox.getLower(firstPoint);
            var secondPoint = new Point3d();
            boundingBox.getUpper(secondPoint);
            var center = new Vector3d((firstPoint.x + secondPoint.x) / 2, (firstPoint.y + secondPoint.y) / 2,
                    (firstPoint.z + secondPoint.z) / 2);
            var reverseCenter = new Vector3d(-center.x, -center.y, -center.z);
            var firstTransform3D = new Transform3D();
            firstTransform3D.setTranslation(reverseCenter);
            boundingBox.transform(firstTransform3D);
            // Rotate, then move back to original position
            var secondTransform3D = new Transform3D();
            secondTransform3D.setRotation(new AxisAngle4d(new Vector3d(1, 1, 1), Math.PI / 2));
            secondTransform3D.setTranslation(center);
            boundingBox.transform(secondTransform3D);
        }
        var minPoint = new Point3d();
        var maxPoint = new Point3d();
        boundingBox.getLower(minPoint);
        boundingBox.getUpper(maxPoint);
        assertEquals(-0.488034, Math.floor(minPoint.x * 1000000) / 1000000);
        assertEquals(-0.488034, Math.floor(minPoint.y * 1000000) / 1000000);
        assertEquals(-0.488034, Math.floor(minPoint.z * 1000000) / 1000000);
        assertEquals(2.488033, Math.floor(maxPoint.x * 1000000) / 1000000);
        assertEquals(2.488033, Math.floor(maxPoint.y * 1000000) / 1000000);
        assertEquals(2.488033, Math.floor(maxPoint.z * 1000000) / 1000000);
    }

}
