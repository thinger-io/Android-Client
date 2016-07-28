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

import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import io.thinger.thinger.R;

public abstract class Element {
    public enum Type{
        BOOLEAN,
        NUMBER,
        STRING,
        OBJECT,
        ARRAY
    }

    public interface ElementListener{
        public void onElementChanged();
    }

    private Type type;

    public Type getType()
    {
        return this.type;
    }

    public Element(Type type) {
        this.type = type;
    }

    protected ElementListener listener;

    public static Element createPrimitiveElement(String name, JsonPrimitive primitive, LinearLayout layout, boolean output){
        if(primitive.isBoolean()){
            return new BoolValue(layout, name, primitive.getAsBoolean(), output);
        }else if(primitive.isNumber()){
            return new NumberValue(layout, name, primitive.getAsNumber(), output);
        }else if(primitive.isString()){
            return new StringValue(layout, name, primitive.getAsString(), output);
        }
        return null;
    }

    public void addResourceName(String title, LinearLayout layout){
        LayoutInflater inflater = LayoutInflater.from(layout.getContext());
        TextView categoryLayout = (TextView) inflater.inflate(R.layout.card_resource_param_name, null, false);
        categoryLayout.setText(title);
        layout.addView(categoryLayout);
    }

    public void setListener(ElementListener listener){
        this.listener = listener;
    }

    public static Element createElement(LinearLayout layout, JsonElement element, boolean output){
        if(element.isJsonPrimitive()){
            return createPrimitiveElement("", element.getAsJsonPrimitive(), layout, output);
        } else if (element.isJsonObject()){
            return new ObjectValue(layout, element.getAsJsonObject(), output);
        }
        return null;
    }

    public abstract void refreshContent(JsonElement element);
}