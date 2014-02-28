/*
 *  Copyright (c) ParkCode 2004 - 2013
 *  all rights reserved
 */

package swat.tfi;

import java.util.List;

import swat.tfi.data.Twitterian;
import swat.tfi.exceptions.TFIException;

/**
 * <b>Предназначение:</b><br/>
 *   <p></p>
 *
 * <br/><b>Описание:</b><br/>
 *   <p></p>
 * 
 * <br/>Создан Feb 28, 2014, 12:57:41 PM<br/>
 *
 * @author Sviatoslav Lobach (s.lobach@parkcode.com.ua)
 */

public interface Storage
{
    public List<Twitterian> getFavouriteFriends();
    public void addToFavouriteFriends(Twitterian twitterian) throws TFIException;
    
    
    public void destroy();
}
