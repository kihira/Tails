package uk.kihira.gltf;

public class GltfException extends RuntimeException {
    public GltfException(String message) {
        super(message);
    }

    public GltfException(String message, Throwable cause) {
        super(message, cause);
    }

    public GltfException(Throwable cause) {
        super(cause);
    }

    public GltfException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
