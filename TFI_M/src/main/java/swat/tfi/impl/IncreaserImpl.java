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
import swat.tfi.Reinitor;
import swat.tfi.Storage;
import swat.tfi.data.Twitterian;
import swat.tfi.exceptions.TFIException;
import swat.tfi.utils.ConvertionUtils;
import swat.tfi.utils.DateTimeUtils;
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
    private static final long RATE_LIMIT_UPDATE_SLEEP_TIME_SECS     = 1000;
    
    private final Long myId;
    
    private Twitter informator;
    private final Storage storage = new StorageH2Impl();
    
    private final Set<Long> myFriends;
                
    private final boolean debugEnabled;
    
    private Reinitor reinitor;
    
    private int followed = 0;
    
    private boolean isUnfollowing = false;
        
    public IncreaserImpl(Reinitor r, boolean debugEnabled)
    {                
        this.reinitor = r;
        this.debugEnabled = debugEnabled;
        
        reinit();
        
        Twitterian me = getTwitterian(MY_SCREEN_NAME);        
        
        myId = me != null ? me.getId() : null;                
        
        myFriends = new HashSet<Long>(getMyFriendsIDs());        
    }
    
    public IncreaserImpl(Reinitor r)
    {
        this(r, false);
    }
    
    public IncreaserImpl()
    {
        this(null, false);
    }
    
    private void reinit()
    {
        if (debugEnabled)
        {
            System.out.println("Reinitting");
        }
        
        ConfigurationBuilder cb = new ConfigurationBuilder().setDebugEnabled(false)
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
    }

    @Override
    public List<Long> getMyFollowersIDs()
    {      
        if (debugEnabled)
        {
            System.out.println("getMyFollowersIDs");
        }
        
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
        if (debugEnabled)
        {
            System.out.println("getMyFriendsIDs");
        }
        
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
        if (debugEnabled)
        {
            System.out.println("unfollowAllWhoDoesntFollowMeExceptFavourites");
        }
        
        List<Long> usersToUnfollow = findIdsOfAllWhoDoesntFollowMeExceptFavourites();
        List<Long> unsucceeded = unfollow(usersToUnfollow);
                
        //TODO :: add unsucceeded logging
    }

    @Override
    public List<Twitterian> getFavouriteFriends()
    {
        if (debugEnabled)
        {
            System.out.println("getFavouriteFriends");
        }
        
        return storage.getFavouriteFriends();
    }

    @Override
    public Twitterian getTwitterian(long id)
    {        
        if (debugEnabled)
        {
            System.out.println("getTwitterian by id " + id);
        }
        
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
        if (debugEnabled)
        {
            System.out.println("getTwitterian by screen name " + screenName);
        }
        
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
        if (debugEnabled)
        {
            System.out.println("getFriendsIds " + friendToInspect);
        }
        
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
            if (debugEnabled)
            {
                System.out.println("addToFavouriteFriends " + twitterian.getScreenName());
            }
            
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
        if (debugEnabled)
        {
            System.out.println("addToFavouriteFriends list ");
        }
        
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
        if (debugEnabled)
        {
            System.out.println("follow " + count);
        }
        
        followed = 0;
        isUnfollowing = false;
                
        while (followed < count)
        {
            Twitterian candidate = findCandidateToFollow(new ArrayList<Long>(myFriends), myId, friendsMoreThanFollowers, russianLanguage, noCollectiveFollowingTweets);
            Long candidateId = candidate != null ? candidate.getId() : null;
            
            if (candidate != null)
            {
                try
                {              
//                    addRandomTweetToFavourites(candidateId);
                    follow(candidateId);
                    followed++;
                    System.out.println("Followed number " + followed + ", " + candidate.getName() + " " + candidate.getScreenName());                                        
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

    private Twitterian findCandidateToFollow(List<Long> myFriendsIds, Long myId, boolean friendsMoreThanFollowers, boolean russianLanguage, boolean noCollectiveFollowingTweets) throws TFIException
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
                                return candidate;
                            }  
                        }
                        else
                        {
                            Twitterian candidate = new Twitterian();
                            candidate.setId(candidateId);
                            
                            return candidate;
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
                System.err.println(DateTimeUtils.dateToString(new Date()) + " Not authorized. Sleeping " + RATE_LIMIT_UPDATE_SLEEP_TIME_SECS + " sec");
                sleepSafe(RATE_LIMIT_UPDATE_SLEEP_TIME_SECS * 1000l);
                if (reinitor != null)
                {
                    if (!isUnfollowing)
                    {
                        reinitor.reinitForFollow(followed);
                    }
                    else
                    {
                        reinitor.reinitForUnfollow();
                    }
                }
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
        if (debugEnabled)
        {
            System.out.println("follow id:" + id);
        }
        
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
        if (debugEnabled)
        {
            System.out.println("addRandomTweetToFavourites id:" + candidateId);
        }
        
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
        isUnfollowing = true;
        
        if (debugEnabled)
        {
            System.out.println("unfollow id:" + id);
        }
        
        sleepSafe(CALL_SLEEP_TIME_MILLISECS);        
        
        try
        {                    
            informator.destroyFriendship(id);
            isUnfollowing = false;
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
        
        isUnfollowing = false;
        return false;
    }

    private List<Long> getFriendsIdsFirstPage(Long friendToInspect)
    {
        if (debugEnabled)
        {
            System.out.println("getFriendsIdsFirstPage id:" + friendToInspect);
        }
        
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
        if (debugEnabled)
        {
            System.out.println("waitUntilCallsUpdate");
        }
        
        if (status != null)
        {
            int secondsUntilReset = status.getSecondsUntilReset();

            if (secondsUntilReset < 0)
            {
                secondsUntilReset = 0;
            }
            secondsUntilReset += RATE_LIMIT_UPDATE_SLEEP_TIME_SECS;

            System.err.println("Calls remain : " + status.getRemaining() + " Seconds until reset " + secondsUntilReset);

            Date nextRefreshDate = new Date();
            long waitTimeInMillis = secondsUntilReset * 1000l;
            nextRefreshDate.setTime(nextRefreshDate.getTime() + waitTimeInMillis);
            System.out.println(DateTimeUtils.dateToString(new Date()) + " Have to wait until rate refreshes to time " + nextRefreshDate.toString());

            sleepSafe(waitTimeInMillis);
            
            if (!isUnfollowing)
            {
                reinitor.reinitForFollow(followed);
            }
            else
            {
                reinitor.reinitForUnfollow();
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
