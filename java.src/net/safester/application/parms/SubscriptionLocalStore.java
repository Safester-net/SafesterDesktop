/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.safester.application.parms;

import net.safester.clientserver.SubscriptionLocal;

/**
 *
 * @author Nicolas de Pomereu
 */
public class SubscriptionLocalStore {

    private static SubscriptionLocal subscriptionLocal = null;

    public static void put(SubscriptionLocal theSubscriptionLocal) {
        subscriptionLocal = theSubscriptionLocal;
    }

    public static SubscriptionLocal getSubscriptionLocal() {
        return subscriptionLocal;
    }

    /**
     * Comfort wrapper because of Legacy code...
     *
     * @return
     */
    public static short getSubscription() {
        if (subscriptionLocal == null) {
            return StoreParms.PRODUCT_FREE;
        } else {
            return (short) subscriptionLocal.getTypeSubscription();
        }
    }

    /**
     * Comfort wrapper because of Legacy code...
     * 
     * @param typeSubscription
     * @param userNumber 
     */
    public static void setSubscription(short typeSubscription, int userNumber) {
        if (subscriptionLocal == null) {
            subscriptionLocal = new SubscriptionLocal();
            subscriptionLocal.setUserNumber(userNumber);
            subscriptionLocal.setTypeSubscription(typeSubscription);
        }
        subscriptionLocal.setTypeSubscription(typeSubscription);
    }

}
