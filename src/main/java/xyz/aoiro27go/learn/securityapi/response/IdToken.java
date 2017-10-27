/*
 * The MIT License
 *
 * Copyright 2017 m28dev.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package xyz.aoiro27go.learn.securityapi.response;

import javax.json.bind.annotation.JsonbProperty;

/**
 *
 * @author m28dev
 */
public class IdToken {

    private String iss;

    private String sub;

    private String aud;

    private String exp;

    private String iat;

    @JsonbProperty(value = "auth_time")
    private String authTime;

    private String nonce;

    private String acr;

    private String amr;

    private String azp;

    @JsonbProperty(value = "at_hash")
    private String atHash;

    @JsonbProperty(value = "c_hash")
    private String cHash;

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getAud() {
        return aud;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getIat() {
        return iat;
    }

    public void setIat(String iat) {
        this.iat = iat;
    }

    public String getAuthTime() {
        return authTime;
    }

    public void setAuthTime(String authTime) {
        this.authTime = authTime;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getAcr() {
        return acr;
    }

    public void setAcr(String acr) {
        this.acr = acr;
    }

    public String getAmr() {
        return amr;
    }

    public void setAmr(String amr) {
        this.amr = amr;
    }

    public String getAzp() {
        return azp;
    }

    public void setAzp(String azp) {
        this.azp = azp;
    }

    public String getAtHash() {
        return atHash;
    }

    public void setAtHash(String atHash) {
        this.atHash = atHash;
    }

    public String getcHash() {
        return cHash;
    }

    public void setcHash(String cHash) {
        this.cHash = cHash;
    }

}
