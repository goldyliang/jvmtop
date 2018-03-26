package com.jvmtop.profiler;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by elnggng on 3/6/18.
 */
public class TagStats implements Comparable<TagStats>
{
    private AtomicLong hits_       = new AtomicLong(0);

    private String        tagName_  = null;

    public TagStats(String tagName)
    {
        super();
        tagName_ = tagName;
    }


    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((tagName_ == null) ? 0 : tagName_.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        TagStats other = (TagStats) obj;
        if (tagName_ == null)
        {
            if (other.tagName_ != null)
            {
                return false;
            }
        }
        else if (!tagName_.equals(other.tagName_))
        {
            return false;
        }
        return true;
    }



    @Override
    /**
     * Compares a MethodStats object by its hits
     */
    public int compareTo(TagStats o)
    {
        return Long.valueOf(o.hits_.get()).compareTo(hits_.get());
    }

    public AtomicLong getHits()
    {
        return hits_;
    }

    public String getTagName()
    {
        return tagName_;
    }

}
