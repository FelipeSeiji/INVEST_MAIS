package com.repositorio.mvp.infrastructure.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configurações de Hardening de Infraestrutura / DevOps.
 * Objetivo: Reduzir a superfície de reconhecimento (Anti-Nmap / Fingerprinting).
 */
@Configuration
public class DevOpsSecurityConfig {

    /**
     * Customiza o Tomcat para ocultar detalhes do servidor nos cabeçalhos HTTP.
     * Altera o cabeçalho 'Server' de 'Apache-Coyote/1.1' para um nome genérico.
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> customizeTomcat() {
        return factory -> factory.addConnectorCustomizers((Connector connector) -> {
            // Define o atributo "server" para mascarar a identidade do servidor
            // Isso 'confunde' ferramentas de fingerprinting como o Nmap.
            connector.setProperty("server", "InvesteApp/1.0 (Production)");
        });
    }
}
