/*
 *  Copyright (c) ParkCode 2004 - 2013
 *  all rights reserved
 */

package swat.tfi.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private static final String MY_SCREEN_NAME = "mesviatsviat";
    private final Long myId;
    
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
        
        Twitterian me = getTwitterian(MY_SCREEN_NAME);        
        
        myId = me != null ? me.getId() : null;
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
            boolean haveToWait = checkTwitterException(twitterException);
            if (haveToWait)
            {
                return getMyFollowersIDs();
            }
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
            boolean haveToWait = checkTwitterException(twitterException);
            if (haveToWait)
            {
                return getMyFriendsIDs();
            }
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
            boolean haveToWait = checkTwitterException(te);
            if (haveToWait)
            {
                return getTwitterian(id);
            }
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
            boolean haveToWait = checkTwitterException(te);
            if (haveToWait)
            {
                return getTwitterian(screenName);
            }
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
                Twitterian user = getTwitterian(screenName);
                if (user != null)
                {                        
                    storage.addToFavouriteFriends(user);                                
                }
                else
                {
                    throw new TFIException("Attempt to add to favourites invalid user");
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
        List<Long> myFriends = getMyFriendsIDs();
        
        while (currentFollowed < count)
        {
            Long candidateId = findCandidateToFollow(myFriends, myId, friendsMoreThanFollowers, russianLanguage, noCollectiveFollowingTweets);

            if (candidateId != null)
            {
                try
                {
                    follow(candidateId);
                    currentFollowed++;
                    System.out.println("Followed number " + currentFollowed + " id: " + candidateId);
                }
                catch (TFIException exception)
                {
                    System.err.println(exception.getMessage());
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

    private Long findCandidateToFollow(List<Long> myFriends, Long myId, boolean friendsMoreThanFollowers, boolean russianLanguage, boolean noCollectiveFollowingTweets) throws TFIException
    {        
        if (myFriends != null && !myFriends.isEmpty())
        {
            int indexOfFriend = (int) (Math.random() * myFriends.size());
            
            Long friendToInspect = myFriends.get(indexOfFriend);
            
            try
            {
                IDs friendFriendsIds = informator.getFriendsIDs(friendToInspect, -1);
                
                long[] friendFriends = friendFriendsIds != null ? friendFriendsIds.getIDs() : null;
                if (friendFriends != null && friendFriends.length != 0)
                {
                    for (long candidateId : friendFriends)
                    {
                        User candidate = informator.showUser(candidateId);
                        if (candidate != null && (myId == null || candidateId != myId.longValue()))
                        {                                                        
                            if (friendsMoreThanFollowers && !candidateIsOkForFriendsMoreThanFollowers(candidate))
                            {
                                continue;
                            }
                            if (russianLanguage && !candidateIsOkForRussianLanguage(candidate))
                            {
                                continue;
                            }
                            //TODO :: collective followers check
                            return candidateId;
                        }                        
                    }
                }                
            }
            catch (TwitterException exception)
            {
                checkTwitterException(exception);
            }            
        }
        else
        {
            throw new TFIException("You dont have friends! Follow at least one!");        
        }   
        
        //repeat attempt
        return findCandidateToFollow(myFriends, myId, friendsMoreThanFollowers, russianLanguage, noCollectiveFollowingTweets);
    }
    
    private boolean candidateIsOkForFriendsMoreThanFollowers(User candidate)
    {
        if (candidate != null)
        {
            int candidateFollowersCount = candidate.getFollowersCount();
            int candidateFriendsCount = candidate.getFriendsCount();
            
            return candidateFriendsCount > candidateFollowersCount;
        }
        
        return false;
    }
    
    private boolean candidateIsOkForRussianLanguage(User candidate)
    {
        if (candidate != null)
        {
            String lang = candidate.getLang();
            
            return lang != null && lang.equalsIgnoreCase("ru");
        }
        
        return false;
    }
    
    /**
     * 
     * @param exception
     * @return have to wait
     */
    private boolean checkTwitterException(TwitterException exception)
    {
        if (exception != null)
        {
            if (exception.getErrorCode() == 88)
                {
                    try
                    {                        
                        int secondsUntilReset = exception.getRateLimitStatus().getSecondsUntilReset();
                        Date nextRefreshDate = new Date(secondsUntilReset * 1000 + 1000l);
                        
                        System.out.println("Have to wait until " + nextRefreshDate.toString());
                        
                        Thread.sleep(nextRefreshDate.getTime());
                        
                        return true;
                    }
                    catch (InterruptedException interruptedException)
                    {
                        //TODO :: add logging
                        interruptedException.printStackTrace();
                        
                        return true;
                    }
                }
                //TODO :: add logging
                exception.printStackTrace();
        }
        
        return false;
    }
    
    private void follow(Long id) throws TFIException
    {
        if (id != null)
        {
            try
            {
                informator.createFriendship(id);
            }
            catch (TwitterException te)
            {
                boolean haveToWait = checkTwitterException(te);
                if (haveToWait)
                {
                    follow(id);
                }
                else
                {
                    throw new TFIException("Failed to follow", te);
                }
            }
        }
    }
}
