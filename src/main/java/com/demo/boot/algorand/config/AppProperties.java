package com.demo.boot.algorand.config;

import com.demo.boot.algorand.model.AppAccount;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * Key of the Map can be thought of as an alias name.
     */
    private Map<String, AppAccount> accounts;

    public Map<String, AppAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(Map<String, AppAccount> accounts) {
        this.accounts = accounts;
    }

}
