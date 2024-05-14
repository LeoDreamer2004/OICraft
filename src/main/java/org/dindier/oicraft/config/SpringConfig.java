package org.dindier.oicraft.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "org.dindier.oicraft")
@PropertySource("classpath:/ai.properties")
public class SpringConfig {
}
