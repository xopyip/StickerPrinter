package pl.baluch.stickerprinter.exceptions;

public class FulfillItemException extends RuntimeException {
    public FulfillItemException(String canceled) {
        super(canceled);
    }
}
