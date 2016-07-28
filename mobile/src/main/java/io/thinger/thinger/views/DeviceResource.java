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

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import io.thinger.api.ThingerAPI;
import io.thinger.thinger.model.DeviceToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import io.thinger.thinger.R;

public class DeviceResource {
    private String resourceName;
    private DeviceToken token;

    private JsonElement input;
    private Element inputElements;

    private JsonElement output;
    private Element outputElements;

    private ResourceType resourceType;

    public enum ResourceType {
        UNKNOWN(-1), NONE(0), RUN(1), PSON_IN(2), PSON_OUT(3), PSON_IN_OUT(4);

        private final int number;

        private ResourceType(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }

        public static ResourceType get(int value){
            switch (value){
                case -1:
                    return UNKNOWN;
                case 0:
                    return NONE;
                case 1:
                    return RUN;
                case 2:
                    return PSON_IN;
                case 3:
                    return PSON_OUT;
                case 4:
                    return PSON_IN_OUT;
                default:
                    return UNKNOWN;
            }
        }
    }

    public DeviceResource(String resourceName, JsonElement resourceDescription) {
        this.resourceName = resourceName;

        resourceType = ResourceType.NONE;

        if(resourceDescription.isJsonObject()){
            JsonObject object = resourceDescription.getAsJsonObject();
            if(object.has("fn")){
                JsonElement function = object.get("fn");
                if(function.isJsonPrimitive()){
                    JsonPrimitive value = function.getAsJsonPrimitive();
                    if(value.isNumber()){
                        resourceType = ResourceType.get(value.getAsInt());
                    }
                }
            }
        }
    }

    public DeviceResource(String resourceName) {
        this.resourceName = resourceName;
        resourceType = ResourceType.UNKNOWN;
    }

    public String getResourceName() {
        return resourceName;
    }

    public ResourceType getResourceType(){
        return resourceType;
    }

    public void setDeviceToken(DeviceToken token){
        this.token = token;
    }

    public void setJsonObject(JsonObject object){

        if(object.has("in")){
            input = object.get("in");
        }

        if(object.has("out")){
            output = object.get("out");
        }

        // try to infer resource type if it is unknown based on its input
        // useful when whe have obtained resources names from a device token instead of a
        // general api request that specifies all functions
        if(resourceType== ResourceType.UNKNOWN){
            if(input!=null && output==null){
                resourceType = ResourceType.PSON_IN;
            }else if(input==null && output!=null){
                resourceType = ResourceType.PSON_OUT;
            }else if(input!=null && output!=null){
                resourceType = ResourceType.PSON_IN_OUT;
            }else{
                resourceType = ResourceType.RUN;
            }
        }

        Log.d("API", "Resource '" + resourceName + "' function = {" + resourceType + "} input = {" + input + "} output = {" + output + "}" );
    }

    private void run(final ImageButton button){

    }

    public void fillView(final View view){
        // avoid displaying empty resource (like a parent resource that does not implement anything)
        if(resourceType== ResourceType.NONE) return;

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView text = (TextView) view.findViewById(R.id.info_text);
        TextView apiText = (TextView) view.findViewById(R.id.api_text);
        //Button runButton = (Button) view.findViewById(R.id.run_button);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.card_content);
        LinearLayout titleContent = (LinearLayout) view.findViewById(R.id.card_header);
        LinearLayout contentLayout = (LinearLayout) view.findViewById(R.id.content_layout);

        text.setText(getResourceName());

        layout.removeAllViews();
        titleContent.removeAllViews();

        boolean hasInput = input!=null;
        if(hasInput && input.isJsonObject() && input.getAsJsonObject().entrySet().isEmpty()){
            hasInput = false;
        }

        boolean hasOutput = output!=null;
        if(hasOutput && output.isJsonObject() && output.getAsJsonObject().entrySet().isEmpty()){
            hasOutput = false;
        }

        if(hasInput){
            // if we have output also it is necessary to differentiate input/output
            if(hasOutput) {
                LayoutInflater inflater = LayoutInflater.from(layout.getContext());
                TextView categoryLayout = (TextView) inflater.inflate(R.layout.card_resource_io_category, null, false);
                categoryLayout.setText("Resource Input");
                layout.addView(categoryLayout);
            }

            // check if the input is a single boolean (and add it to title or content)
            if(input.isJsonPrimitive() && input.getAsJsonPrimitive().isBoolean()){
                contentLayout.setVisibility(View.GONE);

                float value = 8 * view.getResources().getDisplayMetrics().density;
                titleContent.setPadding(0,0, (int) value,0);

                inputElements = Element.createElement(titleContent, input, false);
                inputElements.setListener(new Element.ElementListener() {
                    @Override
                    public void onElementChanged() {
                        execute(null);
                    }
                });
            }else{
                contentLayout.setVisibility(View.VISIBLE);
                inputElements = Element.createElement(layout, input, false);

                final ImageButton runButton = new ImageButton(view.getContext());
                runButton.setImageResource(R.drawable.ic_action_play);

                final TypedValue outValue = new TypedValue();
                view.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                runButton.setBackgroundResource(outValue.resourceId);

                runButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        runButton.setEnabled(false);
                        runButton.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_action_refreshing));
                        RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate);
                        runButton.setBackground(null);
                        runButton.startAnimation(rotateAnimation);
                        execute(new RunResourceListener(){
                            @Override
                            public void onResourceExecuted(boolean success) {
                                runButton.clearAnimation();
                                runButton.setImageResource(R.drawable.ic_action_play);
                                runButton.setEnabled(true);
                                runButton.setBackgroundResource(outValue.resourceId);
                            }
                        });
                    }
                });

                titleContent.addView(runButton);
            }
        }

        if(hasOutput){

            contentLayout.setVisibility(View.VISIBLE);

            // if we have input also it is necessary to differentiate input/output
            if(hasInput) {
                LayoutInflater inflater = LayoutInflater.from(layout.getContext());
                TextView categoryLayout = (TextView) inflater.inflate(R.layout.card_resource_io_category, null, false);
                categoryLayout.setText("Resource Output");
                layout.addView(categoryLayout);

            // it is a only output resource
            }else{
                final ImageButton refreshButton = new ImageButton(view.getContext());
                refreshButton.setImageResource(R.drawable.ic_action_refresh);
                final TypedValue outValue = new TypedValue();
                view.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                refreshButton.setBackgroundResource(outValue.resourceId);
                titleContent.addView(refreshButton);

                refreshButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshButton.setEnabled(false);
                        refreshButton.setImageResource(R.drawable.ic_action_refreshing);
                        RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate);
                        refreshButton.setBackground(null);
                        refreshButton.startAnimation(rotateAnimation);
                        execute(new RunResourceListener() {
                            @Override
                            public void onResourceExecuted(boolean success) {
                                refreshButton.clearAnimation();
                                refreshButton.setImageResource(R.drawable.ic_action_refresh);
                                refreshButton.setEnabled(true);
                                refreshButton.setBackgroundResource(outValue.resourceId);
                            }
                        });
                    }
                });
            }

            outputElements = Element.createElement(layout, output, true);
        }

        // that is a running exmaple without input or output
        if(!hasInput && !hasOutput){
            contentLayout.setVisibility(View.GONE);

            final ImageButton runButton = new ImageButton(view.getContext());
            runButton.setImageResource(R.drawable.ic_action_play);

            final TypedValue outValue = new TypedValue();
            view.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            runButton.setBackgroundResource(outValue.resourceId);

            runButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runButton.setEnabled(false);
                    runButton.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_action_refreshing));
                    RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate);
                    runButton.setBackground(null);
                    runButton.startAnimation(rotateAnimation);
                    execute(new RunResourceListener(){
                        @Override
                        public void onResourceExecuted(boolean success) {
                            runButton.clearAnimation();
                            runButton.setImageResource(R.drawable.ic_action_play);
                            runButton.setEnabled(true);
                            runButton.setBackgroundResource(outValue.resourceId);
                        }
                    });
                }
            });

            titleContent.addView(runButton);
        }
    }

    private interface RunResourceListener{
        void onResourceExecuted(boolean success);
    }

    private void fillOutput(boolean success, JsonElement element){
        if(outputElements==null || element==null) return;
        if(success && element.isJsonObject()){
            JsonObject object = element.getAsJsonObject();
            if(object.has("out")){
                outputElements.refreshContent(object.get("out"));
            }
        }
    }

    private JsonElement getInput(){
        return new JsonParser().parse("{\"in\":" + inputElements.toString() + "}");
    }

    public void execute(final RunResourceListener listener){
        Call<JsonElement> request;

        if(inputElements!=null){
            Log.v("API", "Sending request content: " + getInput());
            request = ThingerAPI.getInstance().postResource(token.getAuthorizationHeader(), token.getUser(), token.getDevice(), resourceName, getInput());
        }else{
            request = ThingerAPI.getInstance().getResource(token.getAuthorizationHeader(), token.getUser(), token.getDevice(), resourceName);
        }

        request.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                fillOutput(response.isSuccessful(), response.body());
                Log.v("API", "Response " +  response.message() + ": " + response.body().toString());
                if(listener!=null) listener.onResourceExecuted(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e("API", "Error: " +  t.toString());
                if(listener!=null) listener.onResourceExecuted(false);
            }
        });
    }


}
