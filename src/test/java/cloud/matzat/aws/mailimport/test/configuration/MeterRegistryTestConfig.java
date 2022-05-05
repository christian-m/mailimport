package cloud.matzat.aws.mailimport.test.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.MockClock;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

/**
 * Test configuration for MeterRegistry.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
@TestConfiguration
public class MeterRegistryTestConfig {

    @Bean
    public MeterRegistry meterRegistry() {
        MockClock clock = new MockClock();
        MeterRegistry meterRegistry = new SimpleMeterRegistry(SimpleConfig.DEFAULT, clock);
        Metrics.globalRegistry.add(meterRegistry);
        return meterRegistry;
    }
}
