package net.sf.fmj.media.rtp;

/**
 * Represents the cause of an invocation of
 * {@link RTPSessionMgr#generateSSRC()}.
 *
 * @author Lyubomir Marinov
 */
public enum GenerateSSRCCause
{
    CREATE_SEND_STREAM,
    INIT_SESSION,
    INITIALIZE,
    LOCAL_COLLISION,
    REMOVE_SEND_STREAM
}
