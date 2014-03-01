/*
 *  Copyright (c) ParkCode 2004 - 2013
 *  all rights reserved
 */

package swat.tfi.data;

/**
 * <b>Предназначение:</b><br/>
 *   <p></p>
 *
 * <br/><b>Описание:</b><br/>
 *   <p></p>
 * 
 * <br/>Создан Feb 28, 2014, 12:38:33 PM<br/>
 *
 * @author Sviatoslav Lobach (s.lobach@parkcode.com.ua)
 */

public class Twitterian
{
    private Long        id;
    private String      name;
    private String      screenName;
    
    private String      language;
    private int         followersCount;
    private int         friendsCount;
    
    public Twitterian()
    {
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getScreenName()
    {
        return screenName;
    }

    public void setScreenName(String screenName)
    {
        this.screenName = screenName;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public int getFollowersCount()
    {
        return followersCount;
    }

    public void setFollowersCount(int followersCount)
    {
        this.followersCount = followersCount;
    }

    public int getFriendsCount()
    {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount)
    {
        this.friendsCount = friendsCount;
    }
    
    
    
    public boolean isValid()
    {
        return id != null && screenName != null && name != null;
    }

    
}
