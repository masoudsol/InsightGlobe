package com.example;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

// @Interceptor
public class ResponseTimeInterceptor implements IClientInterceptor {

    protected final List<Long> responseTimes = new ArrayList<>();

    public ResponseTimeInterceptor() {
		super();
	}

    @Override
    // @Hook(value = Pointcut.CLIENT_REQUEST, order = 1001)
    public void interceptRequest(IHttpRequest theRequest) {}

    @Override
    // @Hook(value = Pointcut.CLIENT_RESPONSE, order = -2)
    public void interceptResponse(IHttpResponse theResponse) {
        StopWatch stopwatch = theResponse.getRequestStopWatch();
        if (stopwatch != null) {
            long time = stopwatch.getMillis();
            responseTimes.add(time);
        }
    }

    public double getAverageResponseTime() {
        if (responseTimes.isEmpty()) return 0;
        return responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
    }

    public void clear() {
        responseTimes.clear();
    }
}
