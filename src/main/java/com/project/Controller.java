package com.project;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.BoundingBox;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class Controller implements Initializable {

    @FXML private Canvas canvas;
    @FXML private Canvas colorsCanvas;
    @FXML private Canvas fillColorsCanvas;
    @FXML private VBox fillPaletteBox;
    @FXML private Slider brushSizeSlider;
    @FXML private RadioButton freePaintButton;
    @FXML private RadioButton lineButton;
    @FXML private RadioButton rectangleButton;
    @FXML private VBox objectBox;
    @FXML private Button deleteButton;

    private GraphicsContext gc;
    private ColorPalette strokePalette;
    private ColorPalette fillPalette;
    private final ToggleGroup toolGroup = new ToggleGroup();
    private HBox selectedRow = null;

    // Drawing state
    private final List<Drawable> strokes = new ArrayList<>();
    private Drawable currentStroke = null;

    // Selection and moving
    private Drawable selectedObject = null;
    private Drawable movingObject = null;
    private double lastDragX, lastDragY;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        deleteButton.setDisable(true);
        gc = canvas.getGraphicsContext2D();
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);

        // The palettes handle their own drawing and clicks
        strokePalette = new ColorPalette(colorsCanvas);
        fillPalette = new ColorPalette(fillColorsCanvas);

        // Tool selector: free paint / line / rectangle
        freePaintButton.setToggleGroup(toolGroup);
        lineButton.setToggleGroup(toolGroup);
        rectangleButton.setToggleGroup(toolGroup);
        freePaintButton.setSelected(true);

        // The fill color palette only exists while Rectangle is selected
        toolGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            boolean rectangle = newToggle == rectangleButton;
            fillPaletteBox.setVisible(rectangle);
            fillPaletteBox.setManaged(rectangle);

            // Choosing a drawing tool deselects the object and returns to draw mode
            clearSelection();
        });

        // Drawing canvas events
        canvas.setOnMousePressed(this::onMousePressed);
        canvas.setOnMouseDragged(this::onMouseDragged);
        canvas.setOnMouseReleased(this::onMouseReleased);

        redraw();
    }

    private double getSelectedWidth() {
        return brushSizeSlider.getValue();
    }

    // Drawing canvas

    private void onMousePressed(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) return;

        // Move mode: pressing inside the bounding box of the selected object
        if (selectedObject != null && selectedObject.getBounds().contains(event.getX(), event.getY())) {
            movingObject = selectedObject;
            lastDragX = event.getX();
            lastDragY = event.getY();
            return;
        }

        if (freePaintButton.isSelected()) {
            currentStroke = new Stroke(strokePalette.getSelectedColor(), getSelectedWidth());
            ((Stroke) currentStroke).addPoint(event.getX(), event.getY());
            redraw();

        } else if (rectangleButton.isSelected()) {
            currentStroke = new Rectangle(strokePalette.getSelectedColor(), getSelectedWidth(), fillPalette.getSelectedColor());
            ((Rectangle) currentStroke).setPosition(event.getX(), event.getY());
            redraw();
            

        } else if (lineButton.isSelected()) {
            currentStroke = new Line(strokePalette.getSelectedColor(), getSelectedWidth());
            ((Line) currentStroke).setStartPosition(event.getX(), event.getY());
            ((Line) currentStroke).setFinalPosition(event.getX(), event.getY());
            redraw();
        }

        // Start a new stroke with the current style
        
    }

    private void onMouseDragged(MouseEvent event) {
        // Move the selected object by the mouse displacement, without deforming it
        if (movingObject != null) {
            double dx = event.getX() - lastDragX;
            double dy = event.getY() - lastDragY;
            lastDragX = event.getX();
            lastDragY = event.getY();

            movingObject.move(dx, dy);
            redraw();
            return;
        }

        if (currentStroke == null) return;

        if (freePaintButton.isSelected()) {
            
            ((Stroke) currentStroke).addPoint(event.getX(), event.getY());
            redraw();

        } else if (rectangleButton.isSelected()) {
            
            ((Rectangle) currentStroke).setDimensions(event.getX(), event.getY());
            redraw();

        } else if (lineButton.isSelected()) {
            ((Line) currentStroke).setFinalPosition(event.getX(), event.getY());
            redraw();
        }
    }

    private void onMouseReleased(MouseEvent event) {
        // The moved object keeps its new position and stays selected
        if (movingObject != null) {
            movingObject = null;
            return;
        }

        if (currentStroke == null) return;

        if (freePaintButton.isSelected()) {
            
            ((Stroke) currentStroke).addPoint(event.getX(), event.getY());
            strokes.add(currentStroke);
            redraw();

        } else if (rectangleButton.isSelected()) {
            
            ((Rectangle) currentStroke).setDimensions(event.getX(), event.getY());
            strokes.add(currentStroke);
            redraw();

        } else if (lineButton.isSelected()) {
            ((Line) currentStroke).setFinalPosition(event.getX(), event.getY());
            strokes.add(currentStroke);
            redraw();
        }
        
        creeateObjectLabel(currentStroke);

        currentStroke = null;
    }

    @FXML 
    private void deleteSelectedObject(MouseEvent event) {
        strokes.remove(selectedRow.getUserData());
        objectBox.getChildren().remove(selectedRow);
        selectedRow.setBorder(null);
        selectedRow = null;
        selectedObject = null;
        repaintObjectBox();

        redraw();

        deleteButton.setDisable(true);
        
    }

    private void onRowClicked(MouseEvent event) {
        HBox row = (HBox) event.getSource();

        if (selectedRow == row) {
            clearSelection();                          // deselect: the bounding box disappears
            return;
        }
        
        if (selectedRow != null) {
            selectedRow.setBorder(null);                   // deselecciona el anterior
        }

        selectedRow = row;
        selectedObject = (Drawable) row.getUserData();

        row.setBorder(new Border(new BorderStroke(Color.web("#2a7fff"),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        deleteButton.setDisable(false);

        redraw();
    }

    private void clearSelection() {
        if (selectedRow != null) {
            selectedRow.setBorder(null);
            selectedRow = null;
        }
        selectedObject = null;
        movingObject = null;
        deleteButton.setDisable(true);

        redraw();
    }

    private void creeateObjectLabel(Drawable d) {
        Label labelObject = new Label(d.getName() + " " + d.getId());

        HBox row = new HBox();
        row.getChildren().add(labelObject);
        row.setUserData(d);
        row.setOnMouseClicked(e -> onRowClicked(e));

        objectBox.getChildren().add(row);

        repaintObjectBox();
        
    }

    private void repaintObjectBox() {
        for (int i = 0; i < objectBox.getChildren().size(); i++) {
            if ( i % 2 != 0) {
                objectBox.getChildren().get(i).setStyle("-fx-background-color: #eeeeee;");
            } else {
                objectBox.getChildren().get(i).setStyle("-fx-background-color: #ffffff;");
            }
        }
    }

    private void redraw() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Saved strokes, in creation order
        for (Drawable s : strokes) {
            s.draw(gc);
        }

        // Stroke being drawn right now (preview)
        if (currentStroke != null) {
            currentStroke.draw(gc);
        }

        // Selection bounding box, drawn last with its own style
        if (selectedObject != null) {
            BoundingBox b = selectedObject.getBounds();

            gc.setStroke(Color.DODGERBLUE);
            gc.setLineWidth(1);
            gc.setLineDashes(6, 4);
            gc.strokeRect(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
            gc.setLineDashes();                        // reset: objects keep their own style
        }
    }
}
