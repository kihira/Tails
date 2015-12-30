package kihira.tails.client.animation;

import org.lwjgl.util.vector.Vector2f;

public class BezierCurve {

    public final Vector2f[] points;

    public BezierCurve(Vector2f ... points) {
        this.points = points;
    }

    /**
     * Gets a point on the curve
     * Based upon the DeCasteljau Algorithm
     * @param points The points defining the curve
     * @param time Time
     * @return The point at the specific time
     * TODO more memory efficient way (or cache)
     */
    private Vector2f getPoint(Vector2f[] points, float time) {
        if (points.length == 1) {
            return points[0];
        }
        else {
            Vector2f[] newPoints = new Vector2f[points.length-1];
            for (int i = 0; i < newPoints.length; i++) {
                newPoints[i] = new Vector2f((1f-time) * points[i].x + time * points[i+1].x, (1f-time) * points[i].y + time * points[i+1].y);
            }
            return getPoint(newPoints, time);
        }
    }

    public Vector2f update(float time) {
        return getPoint(points, time);
    }
}
