package com.tagperf.sampler;

import java.util.Map;

/**
 * Created by elnggng on 3/6/18.
 */
public interface ThreadTagMBean {
    public void enableTag();
    public void disableTag();

    public ThreadTagState[] getAllThreadTagState ();
}
