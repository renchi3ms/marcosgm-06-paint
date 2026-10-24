package com.project;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

// Predefined colors drawn as colored pencils on their own Canvas
public class ColorPalette {

    private static final Color[] COLORS = { Color.BLUE, Color.RED, Color.BLACK, Color.GREEN,
            Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.GRAY };

    private static final double PENCIL_W = 14;
    private static final double SPACING = 15;
    private static final double BODY_H = 26;
    private static final double WOOD_H = 10;
    private static final double LEAD_H = 6;
    private static final double LEAD_W = 6;
    private static final double RAISE = 6;

    private final Canvas canvas;
    private final GraphicsContext gc;
    private Color selectedColor = COLORS[0];

    public ColorPalette(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();

        canvas.setOnMouseClicked(this::onMouseClicked);
        draw();
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    private double getPencilX(int index) {
        return index * SPACING;
    }

    // The selected pencil is raised a few pixels above the others of its row
    private double getPencilBottomY(int index) {
        double raise = COLORS[index].equals(selectedColor) ? RAISE : 0;
        return canvas.getHeight() - 4 - raise;
    }

    // Fills an upward pointing triangle with its base at (x, topY)
    private void fillTriangle(double x, double topY, double w, double h) {
        double[] xPoints = { x, x + w, x + w / 2 };
        double[] yPoints = { topY, topY, topY - h };
        gc.fillPolygon(xPoints, yPoints, 3);
    }

    private void drawPencil(double x, double bottomY, Color color) {
        double bodyTop = bottomY - BODY_H;

        // Body: horizontal gradient to simulate a round pencil
        gc.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, color.darker()),
                new Stop(0.5, color.brighter()),
                new Stop(1, color.darker())));
        gc.fillRect(x, bodyTop, PENCIL_W, BODY_H);

        // Wooden tip
        gc.setFill(Color.web("#D2B48C"));
        fillTriangle(x, bodyTop, PENCIL_W, WOOD_H);

        // Lead
        gc.setFill(Color.web("#363636"));
        fillTriangle(x + (PENCIL_W - LEAD_W) / 2, bodyTop - WOOD_H, LEAD_W, LEAD_H);
    }

    private void onMouseClicked(MouseEvent event) {
        for (int i = 0; i < COLORS.length; i++) {
            double x = getPencilX(i);
            double bottomY = getPencilBottomY(i);
            double topY = bottomY - (BODY_H + WOOD_H + LEAD_H);

            // Inside the pencil if the click is in its column and its height
            if (event.getX() >= x && event.getX() <= x + PENCIL_W
                    && event.getY() >= topY && event.getY() <= bottomY) {
                selectedColor = COLORS[i];
                draw();
                return;
            }
        }
    }

    private void draw() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int i = 0; i < COLORS.length; i++) {
            drawPencil(getPencilX(i), getPencilBottomY(i), COLORS[i]);
        }
    }
}
