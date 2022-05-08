package pl.baluch.stickerprinter.data;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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

        if (previewPane.getWidth() / PAGE_WIDTH < previewPane.getHeight() / PAGE_HEIGHT) {
            scaledPageWidth = previewPane.getWidth();
        } else {
            scaledPageWidth = previewPane.getHeight() * PAGE_WIDTH / PAGE_HEIGHT;
        }
        double scaleRatio = scaledPageWidth / PAGE_WIDTH;

        previewPane.getChildren().add(makeRectangle(0, 0, PAGE_WIDTH, PAGE_HEIGHT, false, scaleRatio));
        double columnWidth = (PAGE_WIDTH - marginHorizontal * 2f)/columns;
        double rowHeight = (PAGE_HEIGHT - marginVertical * 2f)/rows;
        for (int i = 0; i < columns ; i++) {
            double x = marginHorizontal + i * columnWidth;
            for (int j = 0; j < rows; j++) {
                double y = marginVertical + j * rowHeight;
                previewPane.getChildren().add(makeRectangle(x, y, columnWidth, rowHeight, true, scaleRatio));
            }
        }

        previewPane.applyCss();
        previewPane.layout();
    }

    private Node makeRectangle(double x, double y, double w, double h, boolean isSticker, double scaleRatio) {
        Rectangle rectangle = new Rectangle(x * scaleRatio, y * scaleRatio, w * scaleRatio, h * scaleRatio);
        if (isSticker) {
            rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.color(0, 0, 0, 0.1f)));
            rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
        }
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.BLACK);
        return rectangle;
    }
}
