package com.yoshio3.websockets.encoders;

/**
 *
 * @author Yoshio Terada
 */
public class JSONWrappedObject {

    private int result;
    private String status;
    private String message;
    private String version;

    public JSONWrappedObject(String status, int result, String message, String version) {
        this.result = result;
        this.status = status;
        this.message = message;
        this.version = version;
    }

    /**
     * @return the counter
     */
    public int getResult() {
        return result;
    }

    /**
     * @param counter the counter to set
     */
    public void setResult(int counter) {
        this.result = counter;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }
}
