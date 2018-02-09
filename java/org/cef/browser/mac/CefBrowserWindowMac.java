// Copyright (c) 2014 The Chromium Embedded Framework Authors. All rights
// reserved. Use of this source code is governed by a BSD-style license that
// can be found in the LICENSE file.

package org.cef.browser.mac;

import java.awt.Component;
import java.awt.peer.ComponentPeer;
import java.lang.reflect.Method;
import sun.lwawt.LWComponentPeer;
import sun.lwawt.PlatformWindow;
import sun.lwawt.macosx.CFRetainedResource;
import sun.lwawt.macosx.CPlatformWindow;

import org.cef.browser.CefBrowserWindow;

public class CefBrowserWindowMac implements CefBrowserWindow {
    @Override
    public long getWindowHandle(Component comp) {
        final long[] result = new long[1];
        while (comp != null) {
            if (comp.isLightweight()) {
                comp = comp.getParent();
                continue;
            }
            @SuppressWarnings("deprecation")
            ComponentPeer peer = comp.getPeer();
            if (peer instanceof LWComponentPeer) {
                @SuppressWarnings("rawtypes")
                PlatformWindow pWindow = ((LWComponentPeer) peer).getPlatformWindow();
                
                if (pWindow instanceof CPlatformWindow) {
                    Class<?> clazz = pWindow.getClass();

                    try {
                        try {
                            Method method = clazz.getMethod("getNSWindowPtr");
                            result[0] = (Long) method.invoke(pWindow);
                            break;
                        } catch (NoSuchMethodExceptionIgnore e) {
                            Method method = clazz.getMethod("getLayerPtr");
                            result[0] = (Long) method.invoke(pWindow);
                            break;
                        }
                    } catch (Throwable ignore) {
                    }
                    
                    // ((CPlatformWindow) pWindow).execute(new CFRetainedResource.CFNativeAction() {
                    //     @Override
                    //     public void run(long l) {
                    //         result[0] = l;
                    //     }
                    // });
                    // break;
                }
            }
            comp = comp.getParent();
        }
        return result[0];
    }
}
