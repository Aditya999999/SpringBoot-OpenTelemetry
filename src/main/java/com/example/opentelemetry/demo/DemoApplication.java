package com.example.opentelemetry.demo;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.AggregationTemporalitySelector;
import io.opentelemetry.sdk.metrics.export.DefaultAggregationSelector;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.util.Optional;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	// Enable @Timed annotation
	@Bean
	public TimedAspect timedAspect(MeterRegistry registry) {
		return new TimedAspect(registry);
	}

	@Bean
	public OpenTelemetry openTelemetry() {
		return OpenTelemetrySdk.builder()
				.setMeterProvider(
						SdkMeterProvider.builder()
								.setResource(
										Resource.getDefault().toBuilder()
												.put("service.name", "micrometer-shim")
												.put("instrumentation.provider", "micrometer")
												.build())
								.registerMetricReader(
										PeriodicMetricReader.builder(
														OtlpHttpMetricExporter.builder()
																.setEndpoint("https://otlp.nr-data.net/v1/metrics")
																/*.addHeader(
																		"api-key",
																		Optional.ofNullable(System.getenv("NEW_RELIC_LICENSE_KEY"))
																				.filter(str -> !str.isEmpty() && !str.isBlank())
																				.orElseThrow(() -> new IllegalArgumentException("New Relic License Key not found")))
																*/
																.addHeader("api-key", "your_actual_new_relic_api_key")
																.setAggregationTemporalitySelector(
																		AggregationTemporalitySelector.deltaPreferred())
																.setDefaultAggregationSelector(
																		DefaultAggregationSelector.getDefault())
																.build())
												.setInterval(Duration.ofSeconds(60))
												.build())
								.build())
				.build();
	}

	@Bean
	public MeterRegistry meterRegistry(OpenTelemetry openTelemetry) {
		// Create a CompositeMeterRegistry which can hold multiple meter registries
		CompositeMeterRegistry compositeMeterRegistry = new CompositeMeterRegistry();

		// Add a simple meter registry for basic in-memory metrics (for example/testing)
		compositeMeterRegistry.add(new SimpleMeterRegistry());

		// Additional configuration can go here if you have other specific registries
		// For example, adding Prometheus, etc.

		return compositeMeterRegistry;
	}
}
