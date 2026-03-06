package com.cosmo.wanda_web.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import org.springframework.stereotype.Component;

@Component
public class FeignTracingInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        SpanContext spanContext = Span.current().getSpanContext();
        if (spanContext.isValid()) {
            String traceparent = String.format("00-%s-%s-%s",
                    spanContext.getTraceId(),
                    spanContext.getSpanId(),
                    spanContext.getTraceFlags().asHex());
            template.header("traceparent", traceparent);
        }
    }
}
