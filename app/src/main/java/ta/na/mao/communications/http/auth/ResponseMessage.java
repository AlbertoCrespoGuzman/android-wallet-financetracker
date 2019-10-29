package ta.na.mao.communications.http.auth;

/**
 * Created by alberto on 5/17/18.
 */

public class ResponseMessage {

    String timestamp;
    String status;
    String error;
    String message;
    String  path;

    public ResponseMessage(){}


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "ResponseMessage{" +
                "timestamp='" + timestamp + '\'' +
                ", status='" + status + '\'' +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
