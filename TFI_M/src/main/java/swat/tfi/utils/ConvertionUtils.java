/*
 *  Copyright (c) ParkCode 2004 - 2013
 *  all rights reserved
 */

package swat.tfi.utils;

import java.util.ArrayList;
import java.util.List;

import twitter4j.IDs;

/**
 * <b>Предназначение:</b><br/>
 *   <p></p>
 *
 * <br/><b>Описание:</b><br/>
 *   <p></p>
 * 
 * <br/>Создан Feb 28, 2014, 5:58:06 PM<br/>
 *
 * @author Sviatoslav Lobach (s.lobach@parkcode.com.ua)
 */

public class ConvertionUtils
{

    public static List<Long> longArrayToList(long[] array)
    {
        if (array != null && array.length != 0)
        {
            List<Long> res = new ArrayList<Long>();
            
            for (long l : array)
            {
                res.add(l);
            }
            
            return res;
        }
        
        return null;
    }
    
    public static Long[] listToLongArray(List<Long> list)
    {
        if (list != null)
        {
            Long[] res = new Long[list.size()];
            for(int i = 0; i < list.size(); i++) 
            {
                res[i] = list.get(i);
            }

            return res;
        }
        
        return null;
    }
    
    public static List<Long> idsToList(IDs ids)
    {
        if (ids != null)
        {
            List<Long> resList = new ArrayList<Long>();       
            do
            {
                for (long id : ids.getIDs())
                {
                    resList.add(id);
                }
            }
            while (ids.hasNext());            

            return resList;
        }
        
        return null;
    }

}
