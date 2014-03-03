/*
 *  Copyright (c) ParkCode 2004 - 2013
 *  all rights reserved
 */

package swat.tfi.impl;

import swat.tfi.IncreaseJob;
import swat.tfi.Increaser;
import swat.tfi.exceptions.TFIException;

/**
 * <b>Предназначение:</b><br/>
 *   <p></p>
 *
 * <br/><b>Описание:</b><br/>
 *   <p></p>
 * 
 * <br/>Создан Mar 3, 2014, 5:21:04 PM<br/>
 *
 * @author Sviatoslav Lobach (s.lobach@parkcode.com.ua)
 */

public class IncreaseJobImpl implements IncreaseJob
{
    private static final long REINIT_SLEEP_TIME_MILLISECS     = 1000000L;    
    
    private boolean debug = false;
    private Increaser increaser;
    private int haveToFollow = 0;
    private boolean friendsMoreThanFollowers = false;
    private boolean russianLanguage = false;
    private boolean noCollectiveFollowingTweets = false;
    
    
    
    public IncreaseJobImpl(boolean debug)
    {
        this.debug = debug;
        increaser = new IncreaserImpl(this, debug);
    }
   

    public void unfollowAllWhoDoesntFollowMeExceptFavourites()
    {
        increaser.unfollowAllWhoDoesntFollowMeExceptFavourites();
    }

    public void reinitForFollow(int followed)
    {
        if (followed < haveToFollow)
        {
            sleepSafe(REINIT_SLEEP_TIME_MILLISECS);
            increaser = new IncreaserImpl(this, debug);
            
            follow(haveToFollow - followed, friendsMoreThanFollowers, russianLanguage, noCollectiveFollowingTweets);
        }
    }

    public void reinitForUnfollow()
    {
        sleepSafe(REINIT_SLEEP_TIME_MILLISECS);
        increaser = new IncreaserImpl(this, debug);

        unfollowAllWhoDoesntFollowMeExceptFavourites();
    }        

    public void follow(int count, boolean friendsMoreThanFollowers, boolean russianLanguage, boolean noCollectiveFollowingTweets)
    {
        haveToFollow = count;
        this.friendsMoreThanFollowers = friendsMoreThanFollowers;
        this.russianLanguage = russianLanguage;
        this.noCollectiveFollowingTweets = noCollectiveFollowingTweets;
        
        try
        {
            increaser.follow(count, friendsMoreThanFollowers, russianLanguage, noCollectiveFollowingTweets);
        }
        catch (TFIException exception)
        {
            exception.printStackTrace();            
        }
    }       
    
    
    private void sleepSafe(long ms) 
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException  exception)
        {
            exception.printStackTrace();
        }
    }
}
