package com.tagperf.sampler;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * Created by elnggng on 3/6/18.
 */
public class ThreadTag implements ThreadTagMBean {
    public static final String MXBEAN_NAME = "com.tagperf.sampler:type=ThreadTag";

    @Override
    public void enableTag() {

    }

    @Override
    public void disableTag() {

    }

    @Override
    public ThreadTagState [] getAllThreadTagState() {
        //System.out.println ("Getting thread state.");
        return ThreadTagProvider.instance().getAllThreadTagState();
    }

    public static void registerMBean() throws Exception{
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName(MXBEAN_NAME);
        ThreadTag mbean = new ThreadTag();
        mbs.registerMBean(mbean, name);
    }
}
