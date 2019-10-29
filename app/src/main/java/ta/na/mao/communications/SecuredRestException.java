package ta.na.mao.communications;

/**
 * Created by alberto on 18/06/15.
 */
public class SecuredRestException extends RuntimeException {

    public SecuredRestException() {
        super();
    }

    public SecuredRestException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecuredRestException(String message) {
        super(message);
    }

    public SecuredRestException(Throwable cause) {
        super(cause);
    }

}
