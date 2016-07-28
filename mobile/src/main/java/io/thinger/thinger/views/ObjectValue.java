/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 THINK BIG LABS S.L.
 * Author: alvarolb@gmail.com (Alvaro Luis Bustamante)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.thinger.thinger.views;

import android.widget.LinearLayout;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class ObjectValue extends Element {
    Map<String, Element> entries;

    public ObjectValue(LinearLayout layout, JsonObject object, boolean output) {
        super(Type.OBJECT);
        this.entries = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            //addResourceName(entry.getKey(), layout);
            if(entry.getValue().isJsonPrimitive()){
                entries.put(entry.getKey(), createPrimitiveElement(entry.getKey(), entry.getValue().getAsJsonPrimitive(), layout, output));
            }else if(entry.getValue().isJsonObject()){
                entries.put(entry.getKey(), new ObjectValue(layout, entry.getValue().getAsJsonObject(), output));
            }
        }
    }

    @Override
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        int count = 0;
        buffer.append("{");
        for(Map.Entry<String, Element> entry : entries.entrySet()){
            if(count>0) buffer.append(',');
            buffer.append("\"");
            buffer.append(entry.getKey());
            buffer.append("\":");
            buffer.append(entry.getValue().toString());
            count++;
        }
        buffer.append("}");
        return buffer.toString();
    }

    @Override
    public void refreshContent(JsonElement element) {
        if(element.isJsonObject()){
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                Element currentElement = entries.get(entry.getKey());
                if(currentElement!=null){
                    currentElement.refreshContent(entry.getValue());
                }
            }
        }
    }
}
