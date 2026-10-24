package com.project;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Shape implements Drawable {

    protected static int counter = 0;
    protected final int id;

    protected final Color color;
    protected final double width;

    protected Shape(Color color, double width) {
        this.id = ++counter;
        this.color = color;
        this.width = width;
    }

    public int getId() {
        return id;
    }

    public abstract String getName();

    public abstract void draw(GraphicsContext gc);
}
