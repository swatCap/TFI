/*
 *  Copyright (c) ParkCode 2004 - 2013
 *  all rights reserved
 */

package swat.tfi.impl;

import java.util.ArrayList;
import java.util.List;
import swat.tfi.Increaser;
import swat.tfi.Storage;
import swat.tfi.data.Twitterian;
import swat.tfi.exceptions.TFIException;
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
    private final Storage storage = new StorageH2Impl();
    
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
            return idsToLongArray(twitter.getFollowersIDs(-1));
        }
        catch (TwitterException twitterException)
        {
            twitterException.printStackTrace();      
            return null;
        }          
    }

    @Override
    public long[] getMyFriendsIDs()
    {
        try
        {
            return idsToLongArray(twitter.getFriendsIDs(-1));
        }
        catch (TwitterException twitterException)
        {
            twitterException.printStackTrace();      
            return null;
        }        
    }    

    @Override
    public void unfollowAllWhoDoesntFollowMeExceptFavourites()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Twitterian> getFavouriteFriends()
    {
        return storage.getFavouriteFriends();
    }

    @Override
    public Twitterian getTwitterian(long id)
    {
        try
        {            
            User u = twitter.showUser(id);
            
            Twitterian twitterian = new Twitterian();
            twitterian.setId(u.getId());
            twitterian.setName(u.getName());
            twitterian.setScreenName(u.getScreenName());
            
            return twitterian;
        }
        catch (TwitterException te)
        {
            //TODO :: add logging
            te.printStackTrace();
            return null;
        }
    }

    @Override
    public Twitterian getTwitterian(String screenName)
    {
        try
        {            
            User u = twitter.showUser(screenName);
            
            Twitterian twitterian = new Twitterian();
            twitterian.setId(u.getId());
            twitterian.setName(u.getName());
            twitterian.setScreenName(u.getScreenName());
            
            return twitterian;
        }
        catch (TwitterException te)
        {
            //TODO :: add logging
            te.printStackTrace();
            return null;
        }
    }        

    @Override
    public void addToFavouriteFriends(Twitterian twitterian) throws TFIException
    {
        if (twitterian != null)
        {
            Long id = twitterian.getId();
            String screenName = twitterian.getScreenName();
            if (id != null)
            {
                storage.addToFavouriteFriends(twitterian);
            }
            else if (screenName != null)
            {
                try
                {
                    User user = twitter.showUser(screenName);
                    if (user != null)
                    {
                        twitterian.setId(user.getId());
                        twitterian.setName(user.getName());
                        storage.addToFavouriteFriends(twitterian);                                
                    }
                    else
                    {
                        throw new TFIException("Attempt to add to favourites invalid user");
                    }
                }
                catch (TwitterException te)
                {
                    throw new TFIException("Error getting user with screen name " + screenName, te);
                }
            }
            else 
            {
                throw new TFIException("Attempt to add invalid user to favourites");
            }
        }
    }

    @Override
    public List<Twitterian> addToFavouriteFriends(List<Twitterian> twitterians)
    {
        if (twitterians != null && !twitterians.isEmpty())
        {
            List<Twitterian> notAdded = null;
            
            for (Twitterian twitterian : twitterians)
            {
                if (twitterian != null)
                {
                    try
                    {
                        addToFavouriteFriends(twitterian);
                    }
                    catch (TFIException exception)
                    {        
                        //TODO : :add logging
                        System.err.println(exception.getMessage());
                        
                        if (notAdded == null)
                        {
                            notAdded = new ArrayList<Twitterian>();
                        }
                        
                        notAdded.add(twitterian);
                    }
                }
            }
            
            return notAdded;
        }
        
        return null;
    }
       
    @Override
    public void follow(int count, boolean followersLessThanFriends, boolean russianLanguage, boolean noCollectiveFollowingTweets)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void destroy()
    {
        storage.destroy();
    }
    
                    
    private long[] listToLongArray(List<Long> list)
    {
        if (list != null)
        {
            long[] res = new long[list.size()];
            for(int i = 0; i < list.size(); i++) 
            {
                res[i] = list.get(i);
            }

            return res;
        }
        
        return null;
    }
    
    private long[] idsToLongArray(IDs ids)
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

            return listToLongArray(resList);
        }
        
        return null;
    }

}
