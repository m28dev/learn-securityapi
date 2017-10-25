/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xyz.aoiro27go.learn.securityapi;

import java.util.List;

/**
 *
 * @author m28dev
 */
public class JwksRoot {

    private List<JwksKeys> keys;

    public List<JwksKeys> getKeys() {
        return keys;
    }

    public void setKeys(List<JwksKeys> keys) {
        this.keys = keys;
    }

}
