package com.tagperf.sampler;

import java.io.Serializable;

/**
 * Created by elnggng on 3/6/18.
 */
public class ThreadTagState implements Serializable {
    private long threadId;
    private String tag;
    private Thread.State state;

    public ThreadTagState(long threadId, String tag, Thread.State state) {
        this.threadId = threadId;
        this.tag = tag;
        this.state = state;
    }

    public long getThreadId() { return threadId; }

    public String getTag() {
        return tag;
    }

    public Thread.State getState() {
        return state;
    }

    public String toString() {
        return "tag:" + tag + ";state:" + state.toString();
    }
}
