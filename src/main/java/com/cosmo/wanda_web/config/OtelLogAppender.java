package com.cosmo.wanda_web.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import io.opentelemetry.api.trace.Span;

public class OtelLogAppender extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent event) {
        Span.current().addEvent(event.getFormattedMessage());
    }
}
