/*
 *  Copyright (c) ParkCode 2004 - 2013
 *  all rights reserved
 */

package swat.tfi.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import swat.tfi.data.Twitterian;

/**
 * <b>Предназначение:</b><br/>
 *   <p></p>
 *
 * <br/><b>Описание:</b><br/>
 *   <p></p>
 * 
 * <br/>Создан Feb 28, 2014, 2:56:58 PM<br/>
 *
 * @author Sviatoslav Lobach (s.lobach@parkcode.com.ua)
 */

public class TwitteriansReadUtils
{
    private static final String FAVOURITES_KEY = "favourites";
    
    public static List<Twitterian> readFavouritesFromFile(File f)
    {
        if (f != null)
        {
            ObjectMapper mapper = new ObjectMapper();
            try
            {
                List<Twitterian> res = new ArrayList<Twitterian>();
                
                JsonNode node = mapper.readTree(f);
                JsonNode list = node.get(FAVOURITES_KEY);
                if (list != null && list.isArray())
                {
                    for (JsonNode twitterianNode : list)
                    {
                        String nodeInString = twitterianNode.toString();
                        Twitterian twitterian = mapper.readValue(nodeInString, Twitterian.class);
                        if (twitterian != null)
                        {
                            res.add(twitterian);
                        }
                    }
                }
                        
                return res;        
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
                
            }            
        }
        
        return null;
    }

}
