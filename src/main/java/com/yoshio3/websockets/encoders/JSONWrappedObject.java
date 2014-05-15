/*
 * Copyright 2014 Yoshio Terada
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
