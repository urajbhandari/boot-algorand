package com.demo.boot.algorand.config;

import com.demo.boot.algorand.model.AlgoAccount;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {

    /**
     * Key of the Map can is alias name (e.g. bob). The value of the Map is AlgoAccount which stores address and mnemonic.
     */
    private Map<String, AlgoAccount> accounts;

}
