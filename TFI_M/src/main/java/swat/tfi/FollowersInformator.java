/*
 *  Copyright (c) ParkCode 2004 - 2013
 *  all rights reserved
 */

package swat.tfi;

import swat.tfi.data.Twitterian;


/**
 * <b>Предназначение:</b><br/>
 *   <p></p>
 *
 * <br/><b>Описание:</b><br/>
 *   <p></p>
 * 
 * <br/>Создан Feb 25, 2014, 6:32:33 PM<br/>
 *
 * @author Sviatoslav Lobach (s.lobach@parkcode.com.ua)
 */

public interface FollowersInformator
{
    public long[] getMyFollowersIDs();
    
    public long[] getMyFriendsIDs();
    
    public Twitterian getTwitterian(long id);
    public Twitterian getTwitterian(String screenName);
        
}
