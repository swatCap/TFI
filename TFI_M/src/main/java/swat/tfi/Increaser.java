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
 * <br/>Создан Feb 26, 2014, 10:47:23 AM<br/>
 *
 * @author Sviatoslav Lobach (s.lobach@parkcode.com.ua)
 */

public interface Increaser extends FollowersInformator, Storage
{        
//    /**
//     * Get friends which aren't following me
//     * @return names of friends which aren't following me
//     */
//    public List<Twitterian> getFriendsNonFollowers();
    
    public void unfollowAllWhoDoesntFollowMeExceptFavourites();        
    
    /**
     * 
     * @param twitterian
     * @return list of twitterians whish were not added      
     */
    public List<Twitterian> addToFavouriteFriends(List<Twitterian> twitterian);
    
    public void follow(int count, boolean friendsMoreThanFollowers, boolean russianLanguage, boolean noCollectiveFollowingTweets) throws TFIException;
    
    
}
