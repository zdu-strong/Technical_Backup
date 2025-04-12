package com.john.project.test.common.Game;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.john.project.test.common.BaseTest.BaseTest;

public class GameIntersectBoxBoxTest extends BaseTest {

    @Test
    public void test() {
        var boxOne = new BoundingBox(new Vector3(0, 0, 0), new Vector3(2, 2, 2));
        var boxTwo = new BoundingBox(new Vector3(2, 2, 2), new Vector3(4, 4, 4));
        var result = boxOne.intersects(boxTwo);
        assertTrue(result);
    }

}
