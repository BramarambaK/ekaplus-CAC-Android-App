package com.eka.cacapp.utils;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.webtoken.JsonWebSignature;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.security.cert.X509Certificate;

/**
 * to verify the device attestation statement.
 */
public class OfflineVerify {

    private static final DefaultHostnameVerifier HOSTNAME_VERIFIER = new DefaultHostnameVerifier();

    public static AttestationStatement parseAndVerify(String signedAttestationStatment) {
        // Parse JSON Web Signature format.
        JsonWebSignature jws;
        try {
            jws = JsonWebSignature.parser(JacksonFactory.getDefaultInstance())
                    .setPayloadClass(AttestationStatement.class).parse(signedAttestationStatment);
        } catch (IOException e) {
            System.err.println("Failure: " + signedAttestationStatment + " is not valid JWS " +
                    "format.");
            return null;
        }


        // Extract and use the payload data.
        AttestationStatement stmt = (AttestationStatement) jws.getPayload();
        return stmt;
    }

    /**
     * Verifies that the certificate matches the specified hostname.
     * Uses the {@link DefaultHostnameVerifier} from the Apache HttpClient library
     * to confirm that the hostname matches the certificate.
     *
     * @param hostname
     * @param leafCert
     * @return
     */
    private static boolean verifyHostname(String hostname, X509Certificate leafCert) {
        try {
            // Check that the hostname matches the certificate. This method throws an exception if
            // the cert could not be verified.
            HOSTNAME_VERIFIER.verify(hostname, leafCert);
            return true;
        } catch (SSLException e) {

        }

        return false;
    }


    public static AttestationStatement process(String signedAttestationStatement) {
        AttestationStatement stmt = parseAndVerify(signedAttestationStatement);
        if (stmt == null) {
            System.err.println("Failure: Failed to parse and verify the attestation statement.");
            return null;
        }

        return stmt;
    }


}
