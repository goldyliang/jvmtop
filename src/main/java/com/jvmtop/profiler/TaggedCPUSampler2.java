/**
 * jvmtop - java monitoring for the command-line
 *
 * Copyright (C) 2013 by Patric Rufflar. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.jvmtop.profiler;

import com.jvmtop.monitor.VMInfo;
import com.tagperf.sampler.ThreadTagMBean;
import com.tagperf.sampler.ThreadTagState;

import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class TaggedCPUSampler2
{
  private ThreadTagMBean threadTagMBean_ = null;
  private ThreadMXBean                       threadMXBean_ = null;

  private ConcurrentMap<String, TagStats> data_         = new ConcurrentHashMap<String, TagStats>();

  //private long                               beginCPUTime_ = 0;

  private long beginSystemTime_ = 0;
  private long lastUpdatedSystemTime_ = 0;

  private AtomicLong                         totalThreadCPUTime_ = new AtomicLong(
                                                                     0);


  private ConcurrentMap<Long, Long>          threadCPUTime = new ConcurrentHashMap<Long, Long>();

  private AtomicLong                         updateCount_       = new AtomicLong(
                                                                     0);

  private VMInfo                             vmInfo_;

  public TaggedCPUSampler2(VMInfo vmInfo) throws Exception
  {
    super();
    threadTagMBean_ = vmInfo.getThreadTagMBean();
    threadMXBean_ = vmInfo.getThreadMXBean();
    //beginCPUTime_ = vmInfo.getProxyClient().getProcessCpuTime();
    beginSystemTime_ = System.currentTimeMillis();
    vmInfo_ = vmInfo;
  }

  public synchronized List<TagStats> getTop(int limit)
  {
    ArrayList<TagStats> statList = new ArrayList<TagStats>(data_.values());
    Collections.sort(statList);
    return statList.subList(0, Math.min(limit, statList.size()));
  }

  public synchronized void reset() {
    for (TagStats stat: data_.values()) {
      stat.getHits().getAndSet(0);
    }
    //updateCount_.getAndSet(0);
    totalThreadCPUTime_.getAndSet(0);
    beginSystemTime_ = lastUpdatedSystemTime_ = System.currentTimeMillis();
  }

  public long getTotal()
  {
    return totalThreadCPUTime_.get();
  }

  public void update() throws Exception
  {
    boolean samplesAcquired = false;
//    vmInfo_.flush();
    ThreadTagState [] states = threadTagMBean_.getAllThreadTagState();
    samplesAcquired = false;
    for (ThreadTagState tagState : states)
    {
      long cpuTime = threadMXBean_.getThreadCpuTime(tagState.getThreadId());
      Long tCPUTime = threadCPUTime.get(tagState.getThreadId());
      if (tCPUTime == null)
      {
        tCPUTime = 0L;
      }
      else
      {
      Long deltaCpuTime = (cpuTime - tCPUTime);

      //if (tagState.getState() == State.RUNNABLE) {
      if (true) {
          String key = tagState.getTag();
          if (key == null) {
            key = "null";
          } else {
            key = key;
          }
          data_.putIfAbsent(key, new TagStats(key));
          data_.get(key).getHits().addAndGet(deltaCpuTime);
          totalThreadCPUTime_.addAndGet(deltaCpuTime);
            samplesAcquired = true;
        }
      }
      threadCPUTime.put(tagState.getThreadId(), cpuTime);
    }
    if (samplesAcquired) {
      updateCount_.incrementAndGet();
    }
    lastUpdatedSystemTime_ = System.currentTimeMillis();
  }

  public Long getUpdateCount()
  {
    return updateCount_.get();
  }

  // nano second
  public long getElapsedSystemTime() {
    return (lastUpdatedSystemTime_ - beginSystemTime_) * 1000000;
  }

}

