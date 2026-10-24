package com.project;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// A freehand stroke: an ordered list of points with its own style
public class Stroke extends Shape {

    private final List<Point2D> points = new ArrayList<>();

    public Stroke(Color color, double width) {
        super(color, width);
    }

    public void addPoint(double x, double y) {
        points.add(new Point2D(x, y));
    }

    @Override
    public String getName() {
        return "Stroke";
    }

    @Override
    public void draw(GraphicsContext gc) {
        if (points.isEmpty()) return;

        gc.setStroke(color);
        gc.setLineWidth(width);

        // Join each point with the next one
        gc.beginPath();
        gc.moveTo(points.get(0).getX(), points.get(0).getY());
        for (Point2D p : points) {
            gc.lineTo(p.getX(), p.getY());
        }
        gc.stroke();
    }

    @Override
    public BoundingBox getBounds() {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

        for (Point2D p : points) {
            minX = Math.min(minX, p.getX());
            maxX = Math.max(maxX, p.getX());
            minY = Math.min(minY, p.getY());
            maxY = Math.max(maxY, p.getY());
        }

        double m = width / 2 + 2;
        return new BoundingBox(minX - m, minY - m,
                (maxX - minX) + 2 * m, (maxY - minY) + 2 * m);
    }

    @Override
    public void move(double dx, double dy) {
        points.replaceAll(p -> p.add(dx, dy));
    }
}
