package com.project;

import javafx.geometry.BoundingBox;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// A line defined by its two endpoints
public class Line extends Shape {

    private double sx, sy, fx, fy;

    public Line(Color color, double width) {
        super(color, width);
    }

    public void setStartPosition(double x, double y) {
        this.sx = x;
        this.sy = y;
    }

    public void setFinalPosition(double x, double y) {
        this.fx = x;
        this.fy = y;
    }

    @Override
    public String getName() {
        return "Line";
    }

    @Override
    public void draw(GraphicsContext gc) {

        gc.setStroke(color);
        gc.setLineWidth(width);

        gc.strokeLine(sx, sy, fx, fy);
    }

    @Override
    public BoundingBox getBounds() {
        double minX = Math.min(sx, fx), maxX = Math.max(sx, fx);
        double minY = Math.min(sy, fy), maxY = Math.max(sy, fy);

        double m = width / 2 + 2;
        return new BoundingBox(minX - m, minY - m,
                (maxX - minX) + 2 * m, (maxY - minY) + 2 * m);
    }

    @Override
    public void move(double dx, double dy) {
        sx += dx;
        sy += dy;
        fx += dx;
        fy += dy;
    }
}
