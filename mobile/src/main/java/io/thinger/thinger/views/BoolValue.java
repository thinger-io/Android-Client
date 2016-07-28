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
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.JsonElement;

import io.thinger.thinger.R;

public class BoolValue extends Element {
    private Switch button;

    public BoolValue(LinearLayout layout, String paramName, boolean paramValue, boolean output) {
        super(Type.BOOLEAN);

        // inflate resource to view
        LayoutInflater inflater = LayoutInflater.from(layout.getContext());
        View value = inflater.inflate(R.layout.card_resource_param_switch, null, false);

        // set parameter name
        TextView nameView = (TextView) value.findViewById(R.id.param_name);
        if(paramName.isEmpty()){
            nameView.setVisibility(View.GONE);
        }else{
            nameView.setText(paramName);
        }

        // set button content and listener
        button = (Switch) value.findViewById(R.id.param_value);
        button.setChecked(paramValue);
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (listener != null) {
                    listener.onElementChanged();
                }
            }
        });

        // disable EditText for output
        if(output){
            button.setEnabled(false);
        }

        layout.addView(value);
    }

    @Override
    public String toString(){
        return button.isChecked() ? "true" : "false";
    }

    @Override
    public void refreshContent(JsonElement element) {
        if(element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()){
            button.setChecked(element.getAsBoolean());
        }
    }
}
