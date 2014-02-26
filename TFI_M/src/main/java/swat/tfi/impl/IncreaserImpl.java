/*
 *  Copyright (c) ParkCode 2004 - 2013
 *  all rights reserved
 */

package swat.tfi.impl;

import java.util.List;
import swat.tfi.FollowersInformator;
import swat.tfi.Increaser;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 * <b>Предназначение:</b><br/>
 *   <p></p>
 *
 * <br/><b>Описание:</b><br/>
 *   <p></p>
 * 
 * <br/>Создан Feb 25, 2014, 6:52:43 PM<br/>
 *
 * @author Sviatoslav Lobach (s.lobach@parkcode.com.ua)
 */

public class IncreaserImpl implements Increaser
{
    private final Twitter twitter;
    private final DatabaseOperator database = new DatabaseOperator();
    
    public IncreaserImpl()
    {        
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
          .setOAuthConsumerKey("QTGVU3HCGqNRqTAGclBZ9g")
          .setOAuthConsumerSecret("88dCGDkuPBQZRGEwAhXGVqvUgZQT6NqY3wchEuVe9kg")
          .setOAuthAccessToken("1460447732-hneuIZUcbfNYBJkElzacS2vH1ljl69TzvHgudag")
          .setOAuthAccessTokenSecret("wmEgPeQwhE1No4k1oBuKThSQu4jYrih0s3KbGodNjwQ5B");
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();  
        
    }

    @Override
    public long[] getMyFollowersIDs()
    {
        try
        {
            return twitter.getFollowersIDs(-1);
        }
        catch (TwitterException twitterException)
        {
            twitterException.printStackTrace();
            return null;
        }
    }

    @Override
    public long[] getMyFriends()
    {
        try
        {
            IDs ids = twitter.getFriendsIDs(-1);
            if (ids != null)
            {
                long[] res = 
                do
                {
                    for (long i : friendsIDs.getIDs())
                    {
                        System.out.println("follower ID #" + i);
                        System.out.println(twitter.showUser(i).getName());
                    }
                }
                while (friendsIDs.hasNext());
            }
        }
        catch (TwitterException twitterException)
        {
            twitterException.printStackTrace();
        }
        
        return null;
    }

    public List<String> getNamesOfFriendsNonFollowers()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void unfollowAllWhoDoesntFollowMe()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    

}
