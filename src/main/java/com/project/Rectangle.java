package com.project;

import javafx.geometry.BoundingBox;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// A rectangle defined by its anchor point and current dimensions
public class Rectangle extends Shape {

    private double anchorX, anchorY;
    private double x, y, rectWidth, rectHeight;
    private Color colorFill;

    public Rectangle(Color color, double width, Color colorFill) {
        super(color, width);
        this.colorFill = colorFill;
    }

    public void setPosition(double x, double y) {
        this.anchorX = x;
        this.anchorY = y;
    }

    public void setDimensions(double x, double y) {
        this.x = Math.min(anchorX, x);
        this.y = Math.min(anchorY, y);
        this.rectWidth  = Math.abs(x - anchorX);
        this.rectHeight = Math.abs(y - anchorY);
    }

    @Override
    public String getName() {
        return "Rectangle";
    }

    @Override
    public void draw(GraphicsContext gc) {

        gc.setStroke(color);
        gc.setFill(colorFill);
        gc.setLineWidth(width);

        gc.fillRect(this.x, this.y, this.rectWidth, this.rectHeight);
        gc.strokeRect(this.x, this.y, this.rectWidth, this.rectHeight);
    }

    @Override
    public BoundingBox getBounds() {
        double m = width / 2 + 2;
        return new BoundingBox(x - m, y - m, rectWidth + 2 * m, rectHeight + 2 * m);
    }

    @Override
    public void move(double dx, double dy) {
        x += dx;
        y += dy;
    }
}
