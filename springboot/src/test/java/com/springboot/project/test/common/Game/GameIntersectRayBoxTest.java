package com.springboot.project.test.common.Game;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.junit.jupiter.api.Test;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class GameIntersectRayBoxTest extends BaseTest {

    @Test
    public void test() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var ray = new Ray(new Vector3(-10, 10, -10), new Vector3(1, -1, 1));
        var box = new BoundingBox(new Vector3(0, 0, 0), new Vector3(2, 2, 2));
        var result = Intersector.intersectRayBounds(ray, box, null);
        assertTrue(result);
    }

}
