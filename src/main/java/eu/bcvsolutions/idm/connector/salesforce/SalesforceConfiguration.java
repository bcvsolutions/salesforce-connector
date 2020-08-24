package eu.bcvsolutions.idm.connector.salesforce;

import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;

public class SalesforceConfiguration extends AbstractConfiguration {

    private String url;
    private String authUrl;
    private String grandType;
    private GuardedString clientId;
    private GuardedString clientSecret;
    private String username;
    private GuardedString password;
    private GuardedString token;
    private int validity;
    private Boolean newTokenBeforeRequest;

    @ConfigurationProperty(displayMessageKey = "salesforce.url.display",
            helpMessageKey = "salesforce.url.help", order = 1, required = true)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @ConfigurationProperty(displayMessageKey = "salesforce.auth.display",
            helpMessageKey = "salesforce.auth.help", order = 2, required = true)
    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    @ConfigurationProperty(displayMessageKey = "salesforce.grant.type.display",
            helpMessageKey = "salesforce.grant.type.help", order = 3, required = true)
    public String getGrandType() {
        return grandType;
    }

    public void setGrandType(String grandType) {
        this.grandType = grandType;
    }

    @ConfigurationProperty(displayMessageKey = "salesforce.client.id.display",
            helpMessageKey = "salesforce.client.id.help", order = 4, confidential = true, required = true)
    public GuardedString getClientId() {
        return clientId;
    }

    public void setClientId(GuardedString clientId) {
        this.clientId = clientId;
    }

    @ConfigurationProperty(displayMessageKey = "salesforce.client.secret.display",
            helpMessageKey = "salesforce.client.secret.help", order = 5, confidential = true, required = true)
    public GuardedString getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(GuardedString clientSecret) {
        this.clientSecret = clientSecret;
    }

    @ConfigurationProperty(displayMessageKey = "salesforce.username.display",
            helpMessageKey = "salesforce.username.help", order = 6, required = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @ConfigurationProperty(displayMessageKey = "salesforce.password.display",
            helpMessageKey = "salesforce.password.help", order = 7, confidential = true, required = true)
    public GuardedString getPassword() {
        return password;
    }

    public void setPassword(GuardedString password) {
        this.password = password;
    }

    @ConfigurationProperty(displayMessageKey = "salesforce.password.token.display",
            helpMessageKey = "salesforce.password.token.help", order = 8, confidential = true, required = true)
    public GuardedString getToken() {
        return token;
    }

    public void setToken(GuardedString token) {
        this.token = token;
    }

    @ConfigurationProperty(displayMessageKey = "salesforce.validity.display",
            helpMessageKey = "salesforce.validity.help", order = 9, required = true)
    public int getValidity() {
        return validity;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    @ConfigurationProperty(displayMessageKey = "salesforce.new.token.display",
            helpMessageKey = "salesforce.new.token.help", order = 10)
    public Boolean getNewTokenBeforeRequest() {
        return newTokenBeforeRequest;
    }

    public void setNewTokenBeforeRequest(Boolean newTokenBeforeRequest) {
        this.newTokenBeforeRequest = newTokenBeforeRequest;
    }

    @Override
    public void validate() {
        if (StringUtil.isBlank(url)) {
            throw new ConfigurationException("sampleProperty must not be blank!");
        }
    }

    public String getMessage(String key) {
        return getConnectorMessages().format(key, key);
    }
}
