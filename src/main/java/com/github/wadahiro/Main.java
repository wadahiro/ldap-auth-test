package com.github.wadahiro;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

public class Main {

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out
                    .println("java -jar ad-auth-test.jar ldaps://myadhost.org:636 Administrator@myadhost.org mypass /tmp/mycacert");
            return;
        }
        String url = args[0];
        String principal = args[1];
        String credentials = args[2];
        String trustStorePath = args[3];

        System.out.println("Connect to " + url + " by " + principal);

        InitialDirContext ctx = null;
        try {
            ctx = newContext(url, principal, credentials, trustStorePath);
            System.out.println("Success Authentication!");
        } catch (NamingException e) {
            System.err.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException ignore) {
                }
            }
        }
    }

    private static InitialDirContext newContext(String url, String principal,
            String credentials, String trustStorePath) throws NamingException {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, principal);
        env.put(Context.SECURITY_CREDENTIALS, credentials);
        if (url.startsWith("ldaps")) {
            env.put(Context.SECURITY_PROTOCOL, "ssl");
        }
        env.put(Context.REFERRAL, "ignore");
        env.put("com.sun.jndi.ldap.connect.pool", "false");
        System.setProperty("javax.net.ssl.trustStore", trustStorePath);

        InitialDirContext context = new InitialDirContext(env);
        return context;
    }
}