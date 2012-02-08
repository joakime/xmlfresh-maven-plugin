package net.erdfelt.maven.xmlfresh.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WeightedAttrComparator implements Comparator<String>
{
    public static final WeightedAttrComparator INSTANCE = new WeightedAttrComparator();
    private List<String> importantNames = new ArrayList<String>();

    @Override
    public int compare(String o1, String o2)
    {
        String s1 = o1 == null?"":o1;
        String s2 = o2 == null?"":o2;
        int diff = s1.compareTo(s2);

        diff = diff - weighted(s1);
        diff = diff + weighted(s2);

        return diff;
    }

    private int weighted(String name)
    {
        // xmlns is grouped first
        if (name.startsWith("xmlns:"))
        {
            return 1000000; // always first
        }

        int idx = importantNames.indexOf(name);
        if (idx >= 0)
        {
            return 100000 + idx;
        }

        // non-namespaced are next group
        if (name.indexOf(':') < 0)
        {
            return 1000;
        }

        // namespaced is next
        return 0;
    }
    
    public List<String> getImportantNames()
    {
        return importantNames;
    }

    public void setImportantNames(List<String> importantNames)
    {
        this.importantNames = importantNames;
    }
}
