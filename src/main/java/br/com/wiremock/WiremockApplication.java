package br.com.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class WiremockApplication {

    public static void main(String[] args) {
        WireMockServer wireMockServer = new WireMockServer(options()
                .port(8443)
                .notifier(new ConsoleNotifier(true))
                .extensions(new ResponseTemplateTransformer(true))
                .mappingSource(new S3MappingSource()));

        wireMockServer.start();
    }
}
