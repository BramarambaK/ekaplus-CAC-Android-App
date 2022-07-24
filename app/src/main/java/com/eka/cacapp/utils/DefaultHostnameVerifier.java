package com.eka.cacapp.utils;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

class DefaultHostnameVerifier implements HostnameVerifier {

    private final static String[] BAD_COUNTRY_2LDS =
            { "ac", "co", "com", "ed", "edu", "go", "gouv", "gov", "info",
                    "lg", "ne", "net", "or", "org" };
    static {
        Arrays.sort(BAD_COUNTRY_2LDS);
    }
    public final void verify(String host, SSLSocket ssl)
            throws IOException {
        if (host == null) {
            throw new NullPointerException("host to verify is null");
        }
        SSLSession session = ssl.getSession();
        Certificate[] certs = session.getPeerCertificates();
        X509Certificate x509 = (X509Certificate) certs[0];
        verify(host, x509);
    }
    public final boolean verify(String host, SSLSession session) {
        try {
            Certificate[] certs = session.getPeerCertificates();
            X509Certificate x509 = (X509Certificate) certs[0];
            verify(host, x509);
            return true;
        } catch (SSLException e) {
            return false;
        }
    }
    public final void verify(String host, X509Certificate cert)
            throws SSLException {
        String[] cns = getCNs(cert);
        String[] subjectAlts = getDNSSubjectAlts(cert);
        verify(host, cns, subjectAlts);
    }
    public final void verify(final String host, final String[] cns,
                             final String[] subjectAlts,
                             final boolean strictWithSubDomains)
            throws SSLException {
        LinkedList<String> names = new LinkedList<String>();
        if (cns != null && cns.length > 0 && cns[0] != null) {
            names.add(cns[0]);
        }
        if (subjectAlts != null) {
            for (String subjectAlt : subjectAlts) {
                if (subjectAlt != null) {
                    names.add(subjectAlt);
                }
            }
        }
        if (names.isEmpty()) {
            String msg = "Certificate for <" + host + "> doesn't contain CN or DNS subjectAlt";
            throw new SSLException(msg);
        }
        // StringBuilder for building the error message.
        StringBuilder buf = new StringBuilder();
        // We can be case-insensitive when comparing the host we used to
        // establish the socket to the hostname in the certificate.
        String hostName = host.trim().toLowerCase(Locale.ENGLISH);
        boolean match = false;
        for (Iterator<String> it = names.iterator(); it.hasNext();) {
            // Don't trim the CN, though!
            String cn = it.next();
            cn = cn.toLowerCase(Locale.ENGLISH);
            // Store CN in StringBuilder in case we need to report an error.
            buf.append(" <");
            buf.append(cn);
            buf.append('>');
            if (it.hasNext()) {
                buf.append(" OR");
            }

            boolean doWildcard = cn.startsWith("*.") &&
                    cn.lastIndexOf('.') >= 0 &&
                    acceptableCountryWildcard(cn) &&
                    !isValidIPV4Address(host);
            if (doWildcard) {
                match = hostName.endsWith(cn.substring(1));
                if (match && strictWithSubDomains) {
                    match = countDots(hostName) == countDots(cn);
                }
            } else {
                match = hostName.equals(cn);
            }
            if (match) {
                break;
            }
        }
        if (!match) {
            throw new SSLException("hostname in certificate didn't match: <" + host + "> !=" + buf);
        }
    }
    /**
     * Takes a string and parses it to see if it is a valid IPV4 address.
     *
     * @return true, if the string represents an IPV4 address in dotted
     *         notation, false otherwise
     */
    private static boolean isValidIPV4Address(String value) {
        // BEGIN android-changed
        // general test
        if (!value.matches("\\p{Digit}+(\\.\\p{Digit}+)*")) {
            return false;
        }
        String[] parts = value.split("\\.");
        int length = parts.length;
        if (length < 1 || length > 4) {
            return false;
        }
        if (length == 1) {
            // One part decimal numeric address
            long longValue = Long.parseLong(parts[0]);
            return longValue <= 0xFFFFFFFFL;
        } else {
            // Test each part for inclusion in the correct range
            for (int i = 0; i < length; i++) {
                // For two part addresses, the second part expresses
                // a 24-bit quantity; for three part addresses, the third
                // part expresses a 16-bit quantity.
                int max = 0xff;
                if ((length == 2) && (i == 1)) {
                    max = 0xffffff;
                } else if ((length == 3) && (i == 2)) {
                    max = 0xffff;
                }
                if (Integer.parseInt(parts[i]) > max) {
                    return false;
                }
            }
            return true;
        }
        // END android-changed
    }
    public static boolean acceptableCountryWildcard(String cn) {
        int cnLen = cn.length();
        if (cnLen >= 7 && cnLen <= 9) {
            // Look for the '.' in the 3rd-last position:
            if (cn.charAt(cnLen - 3) == '.') {
                // Trim off the [*.] and the [.XX].
                String s = cn.substring(2, cnLen - 3);
                // And test against the sorted array of bad 2lds:
                int x = Arrays.binarySearch(BAD_COUNTRY_2LDS, s);
                return x < 0;
            }
        }
        return true;
    }
    public static String[] getCNs(X509Certificate cert) {
        LinkedList<String> cnList = new LinkedList<String>();

        String subjectPrincipal = cert.getSubjectX500Principal().toString();
        StringTokenizer st = new StringTokenizer(subjectPrincipal, ",");
        while(st.hasMoreTokens()) {
            String tok = st.nextToken();
            int x = tok.indexOf("CN=");
            if (x >= 0) {
                cnList.add(tok.substring(x + 3));
            }
        }
        if (!cnList.isEmpty()) {
            String[] cns = new String[cnList.size()];
            cnList.toArray(cns);
            return cns;
        } else {
            return null;
        }
    }
    /**
     * Extracts the array of SubjectAlt DNS names from an X509Certificate.
     * Returns null if there aren't any.
     * <p/>
     * Note:  Java doesn't appear able to extract international characters
     * from the SubjectAlts.  It can only extract international characters
     * from the CN field.
     * <p/>
     * (Or maybe the version of OpenSSL I'm using to test isn't storing the
     * international characters correctly in the SubjectAlts?).
     *
     * @param cert X509Certificate
     * @return Array of SubjectALT DNS names stored in the certificate.
     */
    public static String[] getDNSSubjectAlts(X509Certificate cert) {
        LinkedList<String> subjectAltList = new LinkedList<String>();
        Collection<List<?>> c = null;
        try {
            c = cert.getSubjectAlternativeNames();
        } catch (CertificateParsingException cpe) {
            Logger.getLogger(DefaultHostnameVerifier.class.getName())
                    .log(Level.FINE, "Error parsing certificate.", cpe);
        }
        if (c != null) {
            for (List<?> aC : c) {
                List<?> list = aC;
                int type = ((Integer) list.get(0)).intValue();
                // If type is 2, then we've got a dNSName
                if (type == 2) {
                    String s = (String) list.get(1);
                    subjectAltList.add(s);
                }
            }
        }
        if (!subjectAltList.isEmpty()) {
            String[] subjectAlts = new String[subjectAltList.size()];
            subjectAltList.toArray(subjectAlts);
            return subjectAlts;
        } else {
            return null;
        }
    }
    /**
     * Counts the number of dots "." in a string.
     * @param s  string to count dots from
     * @return  number of dots
     */
    public static int countDots(final String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '.') {
                count++;
            }
        }
        return count;
    }
    /**
     * Checks to see if the supplied hostname matches any of the supplied CNs
     * or "DNS" Subject-Alts.  Most implementations only look at the first CN,
     * and ignore any additional CNs.  Most implementations do look at all of
     * the "DNS" Subject-Alts. The CNs or Subject-Alts may contain wildcards
     * according to RFC 2818.
     *
     * @param cns         CN fields, in order, as extracted from the X.509
     *                    certificate.
     * @param subjectAlts Subject-Alt fields of type 2 ("DNS"), as extracted
     *                    from the X.509 certificate.
     * @param host        The hostname to verify.
     * @throws SSLException If verification failed.
     */
    public final void verify(
            final String host,
            final String[] cns,
            final String[] subjectAlts) throws SSLException {
        verify(host, cns, subjectAlts, false);
    }
}