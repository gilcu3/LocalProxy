package uci.wifiproxy.data.profile;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import uci.wifiproxy.profile.AuthScheme;

/**
 * Created by daniel on 16/09/17.
 */

public class Profile extends RealmObject {

    public static final String ID_FIELD = "id";
    public static final String NAME_FIELD = "name";

    @PrimaryKey
    private String id;

    @Required
    private String name;

    @Required
    private String authScheme;

    @Required
    private String domain;

    @Required
    private String server;

    private int inPort;

    private int outPort;

    private String bypass;

    public static Profile newProfile(String name, AuthScheme authScheme, String domain,
                                     String server, int inPort, int outPort, String bypass){
        Profile p = new Profile();
        p.setId(UUID.randomUUID().toString());
        p.setName(name);
        p.setAuthScheme(authScheme);
        p.setDomain(domain);
        p.setServer(server);
        p.setInPort(inPort);
        p.setOutPort(outPort);
        p.setBypass(bypass);

        return p;
    }

    public static Profile newProfile(String profileId, String name, AuthScheme authScheme, String domain,
                                     String server, int inPort, int outPort, String bypass){
        Profile p = new Profile();
        p.setId(profileId);
        p.setName(name);
        p.setAuthScheme(authScheme);
        p.setDomain(domain);
        p.setServer(server);
        p.setInPort(inPort);
        p.setOutPort(outPort);
        p.setBypass(bypass);

        return p;
    }

    private void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AuthScheme getAuthScheme() {
        return AuthScheme.valueOf(authScheme);
    }

    public void setAuthScheme(AuthScheme authScheme) {
        this.authScheme = authScheme.name();
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getInPort() {
        return inPort;
    }

    public void setInPort(int inPort) {
        this.inPort = inPort;
    }

    public int getOutPort() {
        return outPort;
    }

    public void setOutPort(int outPort) {
        this.outPort = outPort;
    }

    public String getBypass() {
        return bypass;
    }

    public void setBypass(String bypass) {
        this.bypass = bypass;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}