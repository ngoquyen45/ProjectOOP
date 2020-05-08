package com.viettel.backend.oauth2.core;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.InvalidSignatureException;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.backend.exeption.BusinessAssert;

/**
 * @author thanh
 */
public class DefaultSignedRequestConverter implements SignedRequestConverter, InitializingBean {

    private static final Log logger = LogFactory.getLog(DefaultSignedRequestConverter.class);

    private String verifierKey = new RandomValueStringGenerator().generate();

    private Signer signer = new MacSigner(verifierKey);

    private String signingKey = verifierKey;

    private SignatureVerifier verifier = new MacSigner(verifierKey);;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String signRequest(HttpServletRequest request, HttpServletResponse response, Long validity,
            UserLogin userLogin) throws IOException {
        boolean redirect = request.getHeader(HttpHeaders.AUTHORIZATION) == null;
        TicketInfo info = extractRequest(request, validity, userLogin);

        String ticket = encode(info);

        StringBuilder redirectURL = new StringBuilder(request.getRequestURL());
        // Remove access token from request if present
        String queryString = request.getQueryString();
        if (queryString != null) {
            redirectURL.append("?").append(queryString.replaceAll(OAuth2AccessToken.ACCESS_TOKEN + "=[^&]*&?", ""));
        }
        if (redirectURL.charAt(redirectURL.length() - 1) != '&') {
            redirectURL.append("&");
        }
        redirectURL.append(SignedRequestConverter.PARAMETER_TICKET).append("=").append(ticket);

        if (redirect) {
            response.sendRedirect(redirectURL.toString());
        }

        return redirectURL.toString();
    }

    protected TicketInfo extractRequest(HttpServletRequest request, Long validity, UserLogin userLogin) {
        TicketInfo info = new TicketInfo();
        info.setUrl(request.getRequestURL().toString());
        info.setQuery(hashQueryString(request.getQueryString()));
        if (validity != null) {
            info.setExp(System.currentTimeMillis() + validity);
        }
        info.setUserInfo(new UserInfo(userLogin));
        return info;
    }

    @Override
    public TicketInfo verifyRequest(HttpServletRequest request, HttpServletResponse response) {
        String signedTicket = request.getParameter(PARAMETER_TICKET);
        if (signedTicket == null) {
            throw new InvalidTicketException("Ticket must be provided");
        }

        try {
            Jwt jwt = JwtHelper.decodeAndVerify(signedTicket, verifier);
            String content = jwt.getClaims();
            TicketInfo ticketInfo = objectMapper.readValue(content, TicketInfo.class);
            verifyRequest(request, signedTicket, ticketInfo);
            return ticketInfo;
        } catch (Exception e) {
            throw new InvalidTicketException("Cannot convert ticket to JSON", e);
        }
    }

    protected void verifyRequest(HttpServletRequest request, String signedTicket, TicketInfo ticketInfo) {
        if (ticketInfo.getExp() != null && ticketInfo.getExp() < System.currentTimeMillis()) {
            throw new InvalidTicketException("Ticket expired: " + signedTicket);
        }

        // Check match of URL
        if (!request.getRequestURL().toString().equals(ticketInfo.getUrl())) {
            throw new InvalidTicketException("Invaid ticket:" + signedTicket);
        }

        // Check match of query string
        if (!hashQueryString(request.getQueryString()).equals(ticketInfo.getQuery())) {
            throw new InvalidTicketException("Invaid ticket:" + signedTicket);
        }
    }

    private String hashQueryString(String queryString) {
        if (queryString == null) {
            return "";
        }
        List<String> values = new LinkedList<String>();
        StringTokenizer st = new StringTokenizer(queryString, "&");
        while (st.hasMoreTokens()) {
            String pair = st.nextToken();
            if (pair.startsWith(PARAMETER_TICKET + "=") || pair.startsWith(OAuth2AccessToken.ACCESS_TOKEN + "=")) {
                continue;
            }
            values.add(pair);
        }

        Collections.sort(values);

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
        }

        try {
            byte[] bytes = digest.digest(values.toString().getBytes("UTF-8"));
            return String.format("%032x", new BigInteger(1, bytes));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
        }
    }

    protected String encode(TicketInfo value) {
        String content;
        try {
            content = objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot convert access token to JSON", e);
        }
        String token = JwtHelper.encode(content, signer).getEncoded();
        return token;
    }

    protected TicketInfo decode(String token) {
        try {
            Jwt jwt = JwtHelper.decodeAndVerify(token, verifier);
            String content = jwt.getClaims();
            TicketInfo value = objectMapper.readValue(content, TicketInfo.class);
            return value;
        } catch (Exception e) {
            throw new InvalidTokenException("Cannot convert access token to JSON", e);
        }
    }

    /**
     * Get the verification key for the token signatures.
     * 
     * @return the key used to verify tokens
     */
    public Map<String, String> getKey() {
        Map<String, String> result = new LinkedHashMap<String, String>();
        result.put("alg", signer.algorithm());
        result.put("value", verifierKey);
        return result;
    }

    public void setKeyPair(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        Assert.state(privateKey instanceof RSAPrivateKey, "KeyPair must be an RSA ");
        signer = new RsaSigner((RSAPrivateKey) privateKey);
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        verifier = new RsaVerifier(publicKey);
        verifierKey = "-----BEGIN PUBLIC KEY-----\n" + new String(Base64.encode(publicKey.getEncoded()))
                + "\n-----END PUBLIC KEY-----";
    }

    /**
     * Sets the JWT signing key. It can be either a simple MAC key or an RSA
     * key. RSA keys should be in OpenSSH format, as produced by
     * <tt>ssh-keygen</tt>.
     * 
     * @param key
     *            the key to be used for signing JWTs.
     */
    public void setSigningKey(String key) {
        Assert.hasText(key);
        key = key.trim();

        this.signingKey = key;

        if (isPublic(key)) {
            signer = new RsaSigner(key);
            logger.info("Configured with RSA signing key");
        } else {
            // Assume it's a MAC key
            this.verifierKey = key;
            signer = new MacSigner(key);
        }
    }

    /**
     * @return true if the key has a public verifier
     */
    private boolean isPublic(String key) {
        return key.startsWith("-----BEGIN");
    }

    /**
     * @return true if the signing key is a public key
     */
    public boolean isPublic() {
        return signer instanceof RsaSigner;
    }

    /**
     * The key used for verifying signatures produced by this class. This is not
     * used but is returned from the endpoint to allow resource servers to
     * obtain the key.
     * 
     * For an HMAC key it will be the same value as the signing key and does not
     * need to be set. For and RSA key, it should be set to the String
     * representation of the public key, in a standard format (e.g. OpenSSH
     * keys)
     * 
     * @param key
     *            the signature verification key (typically an RSA public key)
     */
    public void setVerifierKey(String key) {
        this.verifierKey = key;
        try {
            new RsaSigner(verifierKey);
            throw new IllegalArgumentException("Private key cannot be set as verifierKey property");
        } catch (Exception expected) {
            // Expected
        }
    }

    public void afterPropertiesSet() throws Exception {
        SignatureVerifier verifier = new MacSigner(verifierKey);
        try {
            verifier = new RsaVerifier(verifierKey);
        } catch (Exception e) {
            logger.warn("Unable to create an RSA verifier from verifierKey (ignoreable if using MAC)");
        }
        // Check the signing and verification keys match
        if (signer instanceof RsaSigner) {
            byte[] test = "test".getBytes();
            try {
                verifier.verify(test, signer.sign(test));
                logger.info("Signing and verification RSA keys match");
            } catch (InvalidSignatureException e) {
                logger.error("Signing and verification RSA keys do not match");
            }
        } else if (verifier instanceof MacSigner) {
            // Avoid a race condition where setters are called in the wrong
            // order. Use of == is intentional.
            Assert.state(this.signingKey == this.verifierKey,
                    "For MAC signing you do not need to specify the verifier key separately, and if you do it must match the signing key");
        }
        this.verifier = verifier;
    }

    public static class TicketInfo {

        private String url;
        private String query;
        private Long exp;
        private UserInfo userInfo;

        @JsonIgnore
        private UserLogin userLogin;

        public TicketInfo() {

        }

        public TicketInfo(String url, String query, long exp, UserLogin userLogin) {
            super();
            this.url = url;
            this.query = query;
            this.exp = exp;
            this.setUserInfo(new UserInfo(userLogin));
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public Long getExp() {
            return exp;
        }

        public void setExp(Long exp) {
            this.exp = exp;
        }

        public UserInfo getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(UserInfo userInfo) {
            this.userInfo = userInfo;
        }

        @JsonIgnore
        public UserLogin toUserLogin() {
            if (this.userLogin == null) {
                BusinessAssert.isTrue(
                        ObjectId.isValid(userInfo.getClientId()) && ObjectId.isValid(userInfo.getUserId()),
                        "client id or user id is null");

                UserLogin userLogin = new UserLogin(new ObjectId(userInfo.getClientId()), userInfo.getClientCode(),
                        userInfo.getClientName(), new ObjectId(userInfo.getUserId()), userInfo.getUsername(),
                        userInfo.getRole());
                this.userLogin = userLogin;
            }
            return this.userLogin;
        }

    }

    public static class UserInfo implements Serializable {

        private static final long serialVersionUID = 3804684082399553639L;

        private String clientId;
        private String clientCode;
        private String clientName;
        private String userId;
        private String username;
        private String role;

        public UserInfo() {
            super();
        }

        public UserInfo(UserLogin userLogin) {
            super();
            this.clientId = userLogin.getClientId().toString();
            this.clientCode = userLogin.getClientCode();
            this.clientName = userLogin.getClientName();
            this.userId = userLogin.getUserId().toString();
            this.username = userLogin.getUsername();
            this.role = userLogin.getRole();
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientCode() {
            return clientCode;
        }

        public void setClientCode(String clientCode) {
            this.clientCode = clientCode;
        }

        public String getClientName() {
            return clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

    }

}
