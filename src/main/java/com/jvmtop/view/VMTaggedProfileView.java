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
package com.jvmtop.view;

import com.jvmtop.monitor.VMInfo;
import com.jvmtop.monitor.VMInfoState;
import com.jvmtop.openjdk.tools.LocalVirtualMachine;
import com.jvmtop.profiler.CPUSampler;
import com.jvmtop.profiler.MethodStats;
import com.jvmtop.profiler.TagStats;
import com.jvmtop.profiler.TaggedCPUSampler;

import java.util.Iterator;

/**
 * CPU sampling-based profiler view which shows methods with top CPU usage.
 *
 * @author paru
 *
 */
public class VMTaggedProfileView extends AbstractConsoleView
{

  private TaggedCPUSampler cpuSampler_;

  private VMInfo     vmInfo_;

  public VMTaggedProfileView(int vmid, Integer width) throws Exception
  {
    super(width);
    LocalVirtualMachine localVirtualMachine = LocalVirtualMachine
        .getLocalVirtualMachine(vmid);
    vmInfo_ = VMInfo.processNewVM(localVirtualMachine, vmid);
    cpuSampler_ = new TaggedCPUSampler(vmInfo_);
  }

  @Override
  public void sleep(long millis) throws Exception
  {
    long cur = System.currentTimeMillis();
    cpuSampler_.update();
    while (cur + millis > System.currentTimeMillis())
    {
      cpuSampler_.update();
      //super.sleep(100);
      super.sleep(20);
    }

  }

  @Override
  public void printView() throws Exception
  {
    if (vmInfo_.getState() == VMInfoState.ATTACHED_UPDATE_ERROR)
    {
      System.out
          .println("ERROR: Could not fetch telemetries - Process terminated?");
      exit();
      return;
    }
    if (vmInfo_.getState() != VMInfoState.ATTACHED)
    {
      System.out.println("ERROR: Could not attach to process.");
      exit();
      return;
    }

    int w = width - 40;
    System.out.printf(" Profiling PID %d: %40s %n%n", vmInfo_.getId(),
        leftStr(vmInfo_.getDisplayName(), w));

    // these are the spaces taken up by the formatting, the rest is usable
    // for printing out the method name
    w = width - (1 + 6 + 3 + 9 + 3 + 2);
    for (Iterator<TagStats> iterator = cpuSampler_.getTop(25).iterator(); iterator
        .hasNext();)
    {
      TagStats stats = iterator.next();
      //double wallRatio = (double) stats.getHits().get()
      //    / cpuSampler_.getTotal() * 100;
      double wallRatio = (double) stats.getHits().get()
              / cpuSampler_.getElapsedSystemTime() * 100;
      if (!Double.isNaN(wallRatio))
      {
        System.out.printf(" %6.2f%% (%9.2fs) %s%n", wallRatio, wallRatio
            / 100d
            * cpuSampler_.getUpdateCount() * 0.1d,
            shortFQN(stats.getTagName(), w));
      }
    }

    cpuSampler_.reset();
  }

  private String shortFQN(String tag, int size)
  {
    String line = tag;
    if (line.length() > size)
    {
      line = "..." + line.substring(3, size);
    }
    return line;
  }

}
