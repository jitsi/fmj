/*
 * Created on Jun 27, 2004
 */
package com.lti.utils.synchronization;

/**
 * @author Ken Larson
 */
public interface MessageDrivenThreadListener
{
    public void onMessage(MessageDrivenThread sender, Object o);
}
