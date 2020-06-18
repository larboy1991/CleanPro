package com.xiaoniu.cleanking.lifecyler;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ForegroundCallbacks implements Application.ActivityLifecycleCallbacks {
    public static final long CHECK_DELAY = 500;
    public static final String TAG = ForegroundCallbacks.class.getName();

    private static ForegroundCallbacks INSTANCE;
    private boolean foreground = false;
    private boolean paused = true;
    private Handler handler = new Handler();
    private List<LifecycleListener> listeners = new CopyOnWriteArrayList<LifecycleListener>();
    private Runnable check;

    public static ForegroundCallbacks init(Application application) {
        if (INSTANCE == null) {
            INSTANCE = new ForegroundCallbacks();
            application.registerActivityLifecycleCallbacks(INSTANCE);
        }
        return INSTANCE;
    }

    public static ForegroundCallbacks get(Application application) {
        if (INSTANCE == null) {
            init(application);
        }
        return INSTANCE;
    }

    public static ForegroundCallbacks get(Context ctx) {
        if (INSTANCE == null) {
            Context appCtx = ctx.getApplicationContext();
            if (appCtx instanceof Application) {
                init((Application) appCtx);
            }
            throw new IllegalStateException(
                    "Foreground is not initialised and " +
                            "cannot obtain the Application object");
        }
        return INSTANCE;
    }

    public static ForegroundCallbacks get() {
        if (INSTANCE == null) {
            throw new IllegalStateException(
                    "Foreground is not initialised - invoke " +
                            "at least once with parameterised init/get");
        }
        return INSTANCE;
    }

    public boolean isForeground() {
        return foreground;
    }

    public boolean isBackground() {
        return !foreground;
    }

    public void addListener(LifecycleListener listener) {
        listeners.add(listener);
    }

    public void removeListener(LifecycleListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        paused = false;
        boolean wasBackground = !foreground;
        foreground = true;
        if (check != null) {
            handler.removeCallbacks(check);
        }
        if (wasBackground) {
            for (LifecycleListener l : listeners) {
                try {
                    l.onBecameForeground(activity);
                } catch (Exception exc) {
//                    L.d("Listener threw exception!:" + exc.toString());
                }
            }
        } else {
//            L.d("still foreground");
        }
    }

    @Override
    public void onActivityPaused(final Activity activity) {
        paused = true;
        if (check != null) {
            handler.removeCallbacks(check);
        }
        check = new Runnable() {
            @Override
            public void run() {
                if (foreground && paused) {
                    foreground = false;
                    for (LifecycleListener l : listeners) {
                        try {
                            l.onBecameBackground(activity);
                        } catch (Exception exc) {
//                            L.d("Listener threw exception!:" + exc.toString());
                        }
                    }
                } else {
//                    L.d("still foreground");
                }
            }
        };

        handler.postDelayed(check, CHECK_DELAY);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
