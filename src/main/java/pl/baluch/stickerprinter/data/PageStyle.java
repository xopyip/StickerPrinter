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

    private static final int PAGE_WIDTH = 210;
    private static final int PAGE_HEIGHT = 297;

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

    /**
     * Calculate ratio of cell
     *
     * @return ratio always bigger than 1
     */
    public float getCellRatio() {
        float width = PAGE_WIDTH - marginHorizontal * 2;
        float height = PAGE_HEIGHT - marginVertical * 2;
        width /= columns;
        height /= rows;
        float ratio = width / height;
        if (ratio < 1) {
            return 1 / ratio;
        }
        return ratio;
    }

    public static class New extends PageStyle {
        public New() {
            super(Storage.getResourceBundle().getString("print.newpagestyle"));
        }
    }

    public void drawPreview(Pane previewPane) {
        previewPane.getChildren().clear();
        double scaledPageWidth;
        double scaledPageHeight;

        if (previewPane.getWidth() / PAGE_WIDTH < previewPane.getHeight() / PAGE_HEIGHT) {
            scaledPageWidth = previewPane.getWidth();
            scaledPageHeight = previewPane.getWidth() * PAGE_HEIGHT / PAGE_WIDTH;
        } else {
            scaledPageHeight = previewPane.getHeight();
            scaledPageWidth = previewPane.getHeight() * PAGE_WIDTH / PAGE_HEIGHT;
        }

        previewPane.getChildren().add(new Line(0, 0, PAGE_WIDTH, 0));
        previewPane.getChildren().add(new Line(0, PAGE_HEIGHT, PAGE_WIDTH, PAGE_HEIGHT));
        previewPane.getChildren().add(new Line(0, 0, 0, PAGE_HEIGHT));
        previewPane.getChildren().add(new Line(PAGE_WIDTH, 0, PAGE_WIDTH, PAGE_HEIGHT));
        for (int i = 0; i < columns + 1; i++) {
            double x = marginHorizontal + i * (PAGE_WIDTH - marginHorizontal * 2.) / columns;
            previewPane.getChildren().add(new Line(x, marginVertical, x, PAGE_HEIGHT - marginVertical));
        }
        for (int i = 0; i < rows + 1; i++) {
            double y = marginVertical + i * (PAGE_HEIGHT - marginVertical * 2.) / rows;
            previewPane.getChildren().add(new Line(marginHorizontal, y, PAGE_WIDTH - marginHorizontal, y));
        }

        //map values to pane size
        for (Node child : previewPane.getChildren()) {
            if (child instanceof Line) {
                Line line = (Line) child;
                line.setStartX(line.getStartX() * scaledPageWidth / PAGE_WIDTH);
                line.setEndX(line.getEndX() * scaledPageWidth / PAGE_WIDTH);
                line.setStartY(line.getStartY() * scaledPageHeight / PAGE_HEIGHT);
                line.setEndY(line.getEndY() * scaledPageHeight / PAGE_HEIGHT);
            }
        }
        previewPane.applyCss();
        previewPane.layout();
    }
}
