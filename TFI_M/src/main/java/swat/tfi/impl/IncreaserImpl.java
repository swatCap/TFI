/*
 *  Copyright (c) ParkCode 2004 - 2013
 *  all rights reserved
 */

package swat.tfi.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import swat.tfi.Increaser;
import swat.tfi.Storage;
import swat.tfi.data.Twitterian;
import swat.tfi.exceptions.TFIException;
import swat.tfi.utils.ConvertionUtils;
import twitter4j.IDs;
import twitter4j.RateLimitStatus;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.ResponseList;
import twitter4j.Status;
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
    private static final String MY_SCREEN_NAME                      = "mesviatsviat";
    private static final long CALL_SLEEP_TIME_MILLISECS             = 5000;
    private static final long RATE_LIMIT_UPDATE_SLEEP_TIME_SECS     = 3610;
    
    private final Long myId;
    
    private final Twitter informator;
    private final Storage storage = new StorageH2Impl();
    
    private final Set<Long> myFriends;
            
    public IncreaserImpl()
    {        
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
          .setOAuthConsumerKey("QTGVU3HCGqNRqTAGclBZ9g")
          .setOAuthConsumerSecret("88dCGDkuPBQZRGEwAhXGVqvUgZQT6NqY3wchEuVe9kg")
          .setOAuthAccessToken("1460447732-hneuIZUcbfNYBJkElzacS2vH1ljl69TzvHgudag")
          .setOAuthAccessTokenSecret("wmEgPeQwhE1No4k1oBuKThSQu4jYrih0s3KbGodNjwQ5B")                                                                                                                                                                                                           .setUser(MY_SCREEN_NAME).setPassword("181192Swat")
                ;
        
        TwitterFactory tf = new TwitterFactory(cb.build());
        informator = tf.getInstance();  
        informator.addRateLimitStatusListener(new RateLimitStatusListener()
        {
            private void checkRateLimit(RateLimitStatus status)
            {
                if (status != null)
                {
                    int remainCalls = status.getRemaining();

                    if (remainCalls <= 1)
                    { 
                        waitUntilCallsUpdate(status);                    
                    }
                }
            }
            
            public void onRateLimitStatus(RateLimitStatusEvent event)
            {
                checkRateLimit(event != null ? event.getRateLimitStatus() : null);
            }

            public void onRateLimitReached(RateLimitStatusEvent event)
            {
                System.out.println("Rate limit reached!");
                
                checkRateLimit(event != null ? event.getRateLimitStatus() : null);                                
            }

            
        });
        Twitterian me = getTwitterian(MY_SCREEN_NAME);        
        
        myId = me != null ? me.getId() : null;                
        
        myFriends = new HashSet<Long>(getMyFriendsIDs());        
    }

    @Override
    public List<Long> getMyFollowersIDs()
    {      
        sleepSafe(CALL_SLEEP_TIME_MILLISECS);
        
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
        sleepSafe(CALL_SLEEP_TIME_MILLISECS);
        
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
        sleepSafe(CALL_SLEEP_TIME_MILLISECS);
        
        try
        {            
            User u = informator.showUser(id);
            
            Twitterian twitterian = new Twitterian();
            twitterian.setId(u.getId());
            twitterian.setName(u.getName());
            twitterian.setScreenName(u.getScreenName());
            twitterian.setLanguage(u.getLang());
            twitterian.setFollowersCount(u.getFollowersCount());
            twitterian.setFriendsCount(u.getFriendsCount());
            
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
        sleepSafe(CALL_SLEEP_TIME_MILLISECS);
        
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
    public List<Long> getFriendsIds(Long friendToInspect)
    {
        sleepSafe(CALL_SLEEP_TIME_MILLISECS);
        
        if (friendToInspect != null)
        {
            try
            {
                IDs ids = informator.getFriendsIDs(friendToInspect, -1);
                
                return ConvertionUtils.idsToList(ids);
            }
            catch (TwitterException te)
            {
                boolean haveToWait = checkTwitterException(te);
                if (haveToWait)
                {
                    return getFriendsIds(friendToInspect);
                }  
            }
        }
        
        return null;
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
        
        while (currentFollowed < count)
        {
            Long candidateId = findCandidateToFollow(new ArrayList<Long>(myFriends), myId, friendsMoreThanFollowers, russianLanguage, noCollectiveFollowingTweets);

            if (candidateId != null)
            {
                try
                {
                    follow(candidateId);
                    currentFollowed++;
                    System.out.println("Followed number " + currentFollowed + " id: " + candidateId);
                    addRandomTweetToFavourites(candidateId);                    
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
                boolean unfollowed = unfollow(id);
                if (!unfollowed)
                {
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

    private Long findCandidateToFollow(List<Long> myFriendsIds, Long myId, boolean friendsMoreThanFollowers, boolean russianLanguage, boolean noCollectiveFollowingTweets) throws TFIException
    {        
        if (myFriendsIds != null && !myFriendsIds.isEmpty())
        {
            int indexOfFriend = (int) (Math.random() * myFriendsIds.size());
            
            Long friendToInspect = myFriendsIds.get(indexOfFriend);
                                       
            List<Long> friendFriends = getFriendsIdsFirstPage(friendToInspect);
            if (friendFriends != null && !friendFriends.isEmpty())
            {
                for (Long candidateId : friendFriends)
                {
                    if (!myFriends.contains(candidateId))
                    {
                        if (friendsMoreThanFollowers || russianLanguage || noCollectiveFollowingTweets)
                        {
                            Twitterian candidate = getTwitterian(candidateId);
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
                        else
                        {
                            return candidateId;
                        }
                    }
                }
            }                                      
        }
        else
        {
            throw new TFIException("You dont have friends! Follow at least one!");        
        }   
        
        //repeat attempt
        return findCandidateToFollow(myFriendsIds, myId, friendsMoreThanFollowers, russianLanguage, noCollectiveFollowingTweets);
    }
    
    private boolean candidateIsOkForFriendsMoreThanFollowers(Twitterian candidate)
    {
        if (candidate != null)
        {
            int candidateFollowersCount = candidate.getFollowersCount();
            int candidateFriendsCount = candidate.getFriendsCount();
            
            return candidateFriendsCount > candidateFollowersCount;
        }
        
        return false;
    }
    
    private boolean candidateIsOkForRussianLanguage(Twitterian candidate)
    {
        if (candidate != null)
        {
            String lang = candidate.getLanguage();
            
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
            if (exception.getErrorCode() == 88) //rate limit reached
            {
                waitUntilCallsUpdate(exception.getRateLimitStatus());
                
                return true;
            }
            else if (exception.getErrorCode() == -1)
            {
                System.err.println("Not authorized. Slleping " + RATE_LIMIT_UPDATE_SLEEP_TIME_SECS + " sec");
                sleepSafe(RATE_LIMIT_UPDATE_SLEEP_TIME_SECS * 1000l);
                //TODO :: add logging
                
                return true;
            }
            else
            {
                //TODO :: add logging
                exception.printStackTrace();
            }
        }
        
        return false;
    }
    
    private void follow(Long id) throws TFIException
    {
        sleepSafe(CALL_SLEEP_TIME_MILLISECS);
        
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

    /**
     * TODO :: split into two methods - get user timeline and add to favourites
     * @param candidateId
     * @return succeded
     */
    private boolean addRandomTweetToFavourites(long candidateId)
    {        
        sleepSafe(CALL_SLEEP_TIME_MILLISECS);
        
        try
        {
            ResponseList<Status> statuses = informator.getUserTimeline(candidateId);
            if (statuses != null)
            {
                sleepSafe(CALL_SLEEP_TIME_MILLISECS);        
                
                int statusIndexToAdd = (int) (Math.random() * statuses.size());
                Status status = statuses.get(statusIndexToAdd);
                if (status != null)
                {
                    informator.createFavorite(status.getId());
                    
                    return true;
                }                
            }
        }
        catch (TwitterException te)           
        {
            boolean haveToWait = checkTwitterException(te);
            if (haveToWait)
            {
                return addRandomTweetToFavourites(candidateId);
            }
        }
        
        return false;
    }

    private boolean unfollow(Long id)
    {
        sleepSafe(CALL_SLEEP_TIME_MILLISECS);        
        
        try
        {                    
            informator.destroyFriendship(id);
            return true;
        }
        catch (TwitterException exception)
        {
            boolean haveToWait = checkTwitterException(exception);
            if (haveToWait)
            {
                return unfollow(id);
            }            
        }
        
        return false;
    }

    private List<Long> getFriendsIdsFirstPage(Long friendToInspect)
    {
        sleepSafe(CALL_SLEEP_TIME_MILLISECS);
        
        if (friendToInspect != null)
        {
            try
            {
                IDs ids = informator.getFriendsIDs(friendToInspect, -1);
                
                return ids != null ? ConvertionUtils.longArrayToList(ids.getIDs()) : null;
            }
            catch (TwitterException te)
            {
                boolean haveToWait = checkTwitterException(te);
                if (haveToWait)
                {
                    return getFriendsIdsFirstPage(friendToInspect);
                }  
            }
        }
        
        return null;
    }        
    
    private void waitUntilCallsUpdate(RateLimitStatus status)
    {
        if (status != null)
        {
            int secondsUntilReset = status.getSecondsUntilReset();

            if (secondsUntilReset < 0)
            {
                secondsUntilReset = 0;
            }
            secondsUntilReset += RATE_LIMIT_UPDATE_SLEEP_TIME_SECS; //additional 30 mins

            System.err.println("Calls remain : " + status.getRemaining() + " Seconds until reset " + secondsUntilReset);

            Date nextRefreshDate = new Date();
            long waitTimeInMillis = secondsUntilReset * 1000l;
            nextRefreshDate.setTime(nextRefreshDate.getTime() + waitTimeInMillis);
            System.out.println("Have to wait until rate refreshes to time " + nextRefreshDate.toString());

            try
            {
                Thread.sleep(waitTimeInMillis);
            }
            catch (InterruptedException exception)
            {
                exception.printStackTrace();
            }
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
