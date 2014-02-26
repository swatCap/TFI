/*
 *  Copyright (c) ParkCode 2004 - 2013
 *  all rights reserved
 */

package swat.tfi;

import java.util.List;

/**
 * <b>Предназначение:</b><br/>
 *   <p></p>
 *
 * <br/><b>Описание:</b><br/>
 *   <p></p>
 * 
 * <br/>Создан Feb 26, 2014, 10:47:23 AM<br/>
 *
 * @author Sviatoslav Lobach (s.lobach@parkcode.com.ua)
 */

public interface Increaser extends FollowersInformator
{        
    /**
     * Get names of friends which aren't following me
     * @return names of friends which aren't following me
     */
    public List<String> getNamesOfFriendsNonFollowers();
    
    public void unfollowAllWhoDoesntFollowMe();
    
//    public List<Long> findPoss
}
