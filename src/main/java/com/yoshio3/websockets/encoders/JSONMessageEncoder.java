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

import java.io.StringWriter;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 *
 * @author Yoshio Terada
 */
public class JSONMessageEncoder implements Encoder.Text<JSONWrappedObject> {

    @Override
    public String encode(JSONWrappedObject msg) throws EncodeException {
        //JSon オブジェクトを生成
        JsonObject model = Json.createObjectBuilder()
                .add("status", msg.getStatus())
                .add("result", msg.getResult())
                .add("message", msg.getMessage())
                .add("version", msg.getVersion())
                .build();
        // JSon オブジェクトから JSon 文字列を生成
        StringWriter stWriter = new StringWriter();
        try (JsonWriter jsonWriter = Json.createWriter(stWriter)) {
            jsonWriter.writeObject(model);
        }
        return stWriter.toString();
    }

    @Override
    public void init(EndpointConfig config) {
        ;
    }

    @Override
    public void destroy() {
        ;
    }

}
