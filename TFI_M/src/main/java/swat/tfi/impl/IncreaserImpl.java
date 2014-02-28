/*
 *  Copyright (c) ParkCode 2004 - 2013
 *  all rights reserved
 */

package swat.tfi.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import swat.tfi.Increaser;
import swat.tfi.Storage;
import swat.tfi.data.Twitterian;
import swat.tfi.exceptions.TFIException;
import swat.tfi.utils.ConvertionUtils;
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
    private final Twitter informator;
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
        informator = tf.getInstance();  
    }

    @Override
    public List<Long> getMyFollowersIDs()
    {
        try
        {
            return ConvertionUtils.idsToList(informator.getFollowersIDs(-1));
        }
        catch (TwitterException twitterException)
        {
            twitterException.printStackTrace();      
            return null;
        }          
    }

    @Override
    public List<Long> getMyFriendsIDs()
    {
        try
        {
            return ConvertionUtils.idsToList(informator.getFriendsIDs(-1));
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
        List<Long> usersToUnfollow = findIdsOfAllWhoDoesntFollowMeExceptFavourites();
        List<Long> unsucceeded = unfollow(usersToUnfollow);
                
        //TODO :: add unsucceeded logging
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
            User u = informator.showUser(id);
            
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
            User u = informator.showUser(screenName);
            
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
                    User user = informator.showUser(screenName);
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
    public void follow(int count, boolean friendsMoreThanFollowers, boolean russianLanguage, boolean noCollectiveFollowingTweets) throws TFIException
    {        
        int currentFollowed = 0;

        while (currentFollowed < count)
        {
            Long candidateId = findCandidateToFollow(friendsMoreThanFollowers, russianLanguage, noCollectiveFollowingTweets);

            if (candidateId != null)
            {
                try
                {
                    informator.createFriendship(candidateId);
                    currentFollowed++;
                }
                catch (TwitterException exception)
                {
                    //TODO :: add logging
                    exception.printStackTrace();                                                
                }
            }
        }        
    }

    @Override
    public void destroy()
    {
        storage.destroy();
    }
                            
    
    private List<Long> findIdsOfAllWhoDoesntFollowMeExceptFavourites()
    {
        List<Long> friendsIds     = getMyFriendsIDs();        
        List<Long> followersIds   = getMyFollowersIDs();
        List<Long> favouritesIds  = new ArrayList<Long>();
       
        List<Twitterian> favourites = getFavouriteFriends();
        if (favourites != null && !favourites.isEmpty())
        {
            for (Twitterian twitterian : favourites)
            {
                favouritesIds.add(twitterian.getId());
            }
        }
        
        if (friendsIds != null && !friendsIds.isEmpty())
        {
            if (followersIds != null && !followersIds.isEmpty())
            {
                friendsIds.removeAll(followersIds);
            }

            if (!favouritesIds.isEmpty())
            {
                friendsIds.removeAll(favouritesIds);
            }
        }
        
        return friendsIds;
    }
    
    /**
     * Unfollow users with ids
     * @param idsToUnfollow
     * @return ids which were not unfollowed
     */
    private List<Long> unfollow(List<Long> idsToUnfollow)
    {
        if (idsToUnfollow != null && !idsToUnfollow.isEmpty())
        {
            List<Long> res = null;
            
            for (Long id : idsToUnfollow)
            {
                try
                {
                    informator.destroyFriendship(id);
                }
                catch (TwitterException exception)
                {
                    //TODO :: add logging
                    exception.printStackTrace();
                    
                    if (res == null)
                    {
                        res = new ArrayList<Long>();
                    }
                    
                    res.add(id);
                }
            }
            
            return res;
        }
        
        return null;
    }

    private Long findCandidateToFollow(boolean friendsMoreThanFollowers, boolean russianLanguage, boolean noCollectiveFollowingTweets) throws TFIException
    {
        List<Long> friends = getMyFriendsIDs();
        
        if (friends != null && !friends.isEmpty())
        {
            int indexOfFriend = (int) (Math.random() * friends.size());
            
            Long friendToInspect = friends.get(indexOfFriend);
            
            try
            {
                IDs friendFriendsIds = informator.getFriendsIDs(friendToInspect, -1);
                
                long[] friendFriends = friendFriendsIds != null ? friendFriendsIds.getIDs() : null;
                if (friendFriends != null && friendFriends.length != 0)
                {
                    for (long candidateId : friendFriends)
                    {
                        User candidate = informator.showUser(candidateId);
                        if (candidate != null)
                        {
                            
                        }                        
                    }
                }                
            }
            catch (TwitterException exception)
            {
                //TODO :: add logging
                exception.printStackTrace();
                
                //repeat attempt
                return findCandidateToFollow(friendsMoreThanFollowers, russianLanguage, noCollectiveFollowingTweets);
            }            
        }
        else
        {
            throw new TFIException("You dont have friends! Follow at least one!");        
        }   
        
        return findCandidateToFollow(friendsMoreThanFollowers, russianLanguage, noCollectiveFollowingTweets);
    }
}
