/**
 * Copyright (C) 2009  Hiram Chirino
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package webbytest.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.hiramchirino.restygwt.client.Method;
import com.hiramchirino.restygwt.client.Resource;
import com.hiramchirino.restygwt.client.TextCallback;

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Client implements EntryPoint {

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

        // start checking for page updates...
        onUpdate(Window.Location.getHref(), new Runnable() {
            public void run() {
                Window.Location.reload();
            }
        });

        // If we can load the http://localhost:51235/icon image
        // then the user had the Atlassian Connector installed in his IDE
        Image img = new Image();
        img.setVisible(false);
        img.setUrl("http://localhost:51235/icon");
        img.addLoadHandler(new LoadHandler(){
            public void onLoad(LoadEvent loadEvent) {

                // Add IDE links to all the stack traces
                Element[] elements = GQuery.$(".stacktrace").elements();
                for (Element element : elements) {
                    String file = element.getAttribute("file");
                    String line = element.getAttribute("line");
                    if( file!=null && line!=null ) {
                        double random = Math.floor(Math.random()*1000);
                        String innerHTML = element.getInnerHTML();
                        element.setInnerHTML(innerHTML+
                            "<img" +
                            " title='Open file "+file+" in IDE'" +
                            " class='ide-icon tb_right_mid'" +
                            " src='http://localhost:51235/icon'" +
                            " alt='Open in IDE'" +
                            " onclick=\"this.src='http://localhost:51235/file?file=List.scala&amp;line=45&amp;id="+random+"'\""+
                            "/>");
                    } else {
                        Window.alert("element missing bits..");
                    }
                }
            }
        });
        img.addErrorHandler(new ErrorHandler(){
            public void onError(ErrorEvent errorEvent) {
                // Can't link to the user's IDE.
                // TODO: link to the source code with something
                // like bespin
            }
        });
        RootPanel.get().add(img);

    }

    public void onUpdate(String url, Runnable onUpdate) {
        schedualUpdateCheck(url, onUpdate, null);
    }

    private void schedualUpdateCheck(final String url, final Runnable onUpdate, final String last) {
        // It's still the same...
        // Check again in a few seconds..
        new Timer() {
            public void run() {
                updateCheck(url, onUpdate, last);
            }
        }.schedule(1000);
    }

    private void updateCheck(final String url, final Runnable onUpdate, final String last) {

        Resource resource = new Resource(url);
        Method method = null;

        // the file: url is a little different than
        // when talking to a remote web server..
        if (url.startsWith("file:")) {
            method = resource.get();
            try {
                method.expect(0);
            } catch (RequestException e) {
            }
        } else {
            method = resource.head();
        }

        method.send(new TextCallback() {
            public void onSuccess(Method method, String response) {
                if (!url.startsWith("file:")) {
                    response = method.getResponse().getHeader("Last-Modified");
                    GWT.log("Last-Modified: " + response, null);
                }
                if (last != null && !last.equals(response)) {

                } else {
                    schedualUpdateCheck(url, onUpdate, response);
                }
            }

            public void onFailure(Method method, Throwable exception) {
                GWT.log("update check failed.", exception);
                schedualUpdateCheck(url, onUpdate, last);
            }
        });
    }

}
