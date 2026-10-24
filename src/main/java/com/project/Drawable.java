package com.project;

import javafx.geometry.BoundingBox;
import javafx.geometry.BoundingBox;
import javafx.scene.canvas.GraphicsContext;

public interface Drawable {
    String getName();
    int getId();
    void draw(GraphicsContext gc);
    BoundingBox getBounds();
    void move(double dx, double dy);
}
