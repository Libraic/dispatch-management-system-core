package io.kovin.dispatch.management.system.config;

import java.util.Map;
import io.kovin.dispatch.management.system.model.enums.KpiName;
import io.kovin.dispatch.management.system.strategy.ApmKpiCalculationStrategy;
import io.kovin.dispatch.management.system.strategy.KpiCalculationStrategy;
import io.kovin.dispatch.management.system.strategy.MilesKpiCalculationStrategy;
import io.kovin.dispatch.management.system.strategy.RevenueKpiCalculationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StrategyConfig {

    @Bean
    public Map<KpiName, KpiCalculationStrategy> kpiCalculationStrategies() {
        return Map.of(
            KpiName.REVENUE, new RevenueKpiCalculationStrategy(),
            KpiName.MILES, new MilesKpiCalculationStrategy(),
            KpiName.APM, new ApmKpiCalculationStrategy()
        );
    }
}
