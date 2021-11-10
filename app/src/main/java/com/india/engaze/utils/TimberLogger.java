package com.india.engaze.utils;

import timber.log.Timber;


public class TimberLogger extends Timber.DebugTree {
    @Override
    protected String createStackElementTag(StackTraceElement element) {
        return String.format("(%s:%s)#%s",
                element.getFileName(),
                element.getLineNumber(),
                element.getMethodName());
    }
}
