package pl.baluch.stickerprinter.data;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import pl.baluch.stickerprinter.Storage;

public class PageStyle {
    private String name;
    private int columns = 1;
    private int rows = 1;
    private int marginVertical = 0;
    private int marginHorizontal = 0;

    private static final int pageWidth = 210;
    private static final int pageHeight = 297;

    public PageStyle(String name) {
        this.name = name;
    }

    public PageStyle(String name, int columns, int rows, int marginVertical, int marginHorizontal) {
        this.name = name;
        this.columns = columns;
        this.rows = rows;
        this.marginVertical = marginVertical;
        this.marginHorizontal = marginHorizontal;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public int getColumns() {
        return columns;
    }

    public int getMarginHorizontal() {
        return marginHorizontal;
    }

    public int getMarginVertical() {
        return marginVertical;
    }

    public int getRows() {
        return rows;
    }

    public void setName(String text) {
        this.name = text;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public void setMarginHorizontal(int marginHorizontal) {
        this.marginHorizontal = marginHorizontal;
    }

    public void setMarginVertical(int marginVertical) {
        this.marginVertical = marginVertical;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public static class New extends PageStyle {
        public New() {
            super(Storage.getResourceBundle().getString("print.newpagestyle"));
        }
    }

    public void drawPreview(Pane previewPane) {
        previewPane.getChildren().clear();
        previewPane.getChildren().add(new Line(0, 0, pageWidth, 0));
        previewPane.getChildren().add(new Line(0, pageHeight, pageWidth, pageHeight));
        previewPane.getChildren().add(new Line(0, 0, 0, pageHeight));
        previewPane.getChildren().add(new Line(pageWidth, 0, pageWidth, pageHeight));
        for (int i = 0; i < columns + 1; i++) {
            int x = marginHorizontal + i * (pageWidth - marginHorizontal * 2) / columns;
            previewPane.getChildren().add(new Line(x, marginVertical, x, pageHeight - marginVertical));
        }
        for (int i = 0; i < rows + 1; i++) {
            int y = marginVertical + i * (pageHeight - marginVertical * 2) / rows;
            previewPane.getChildren().add(new Line(marginHorizontal, y, pageWidth - marginHorizontal, y));
        }

        //map values to pane size
        for (Node child : previewPane.getChildren()) {
            if(child instanceof Line){
                Line line = (Line) child;
                line.setStartX(line.getStartX() * previewPane.getWidth() / pageWidth);
                line.setEndX(line.getEndX() * previewPane.getWidth() / pageWidth);
                line.setStartY(line.getStartY() * previewPane.getHeight() / pageHeight);
                line.setEndY(line.getEndY() * previewPane.getHeight() / pageHeight);
            }
        }
        previewPane.applyCss();
        previewPane.layout();
    }
}
