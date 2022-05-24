package pl.baluch.stickerprinter.data;

import javafx.scene.Cursor;

public enum ElementActionType {
    NONE(Cursor.DEFAULT),
    RESIZE_TOP(Cursor.N_RESIZE),
    RESIZE_LEFT(Cursor.W_RESIZE),
    RESIZE_BOTTOM(Cursor.S_RESIZE),
    RESIZE_RIGHT(Cursor.E_RESIZE),
    RESIZE_TOP_LEFT(Cursor.NW_RESIZE),
    RESIZE_TOP_RIGHT(Cursor.NE_RESIZE),
    RESIZE_BOTTOM_LEFT(Cursor.SW_RESIZE),
    RESIZE_BOTTOM_RIGHT(Cursor.SE_RESIZE),
    DRAG(Cursor.MOVE);

    private final Cursor cursor;

    ElementActionType(Cursor cursor) {
        this.cursor = cursor;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public boolean isResize() {
        return isResizeTop() || isResizeLeft() || isResizeBottom() || isResizeRight();
    }

    public boolean isResizeTop() {
        return this == RESIZE_TOP || this == RESIZE_TOP_LEFT || this == RESIZE_TOP_RIGHT;
    }

    public boolean isResizeLeft() {
        return this == RESIZE_LEFT || this == RESIZE_TOP_LEFT || this == RESIZE_BOTTOM_LEFT;
    }

    public boolean isResizeBottom() {
        return this == RESIZE_BOTTOM || this == RESIZE_BOTTOM_LEFT || this == RESIZE_BOTTOM_RIGHT;
    }

    public boolean isResizeRight() {
        return this == RESIZE_RIGHT || this == RESIZE_TOP_RIGHT || this == RESIZE_BOTTOM_RIGHT;
    }

    public boolean canSetCursor(boolean resizable, boolean draggable) {
        return (isResize() && resizable) || (this == DRAG && draggable);
    }
}
