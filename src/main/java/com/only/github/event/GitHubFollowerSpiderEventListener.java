package com.only.github.event;

import com.google.common.eventbus.Subscribe;
import com.only.github.common.event.AbstractResult;
import com.only.github.common.event.EventDrivenHelper;
import com.only.github.common.event.listener.AbstractEventListener;
import org.kohsuke.github.GHPersonSet;
import org.kohsuke.github.GHUser;

/**
 * Created by leiteng on 2017/5/24.
 */
public class GitHubFollowerSpiderEventListener extends AbstractEventListener<GitHubFollowersSpiderEvent, AbstractResult<Boolean>> {

    @Subscribe
    public void listen(GitHubFollowersSpiderEvent event) {
        handleEvent(event);
    }

    protected AbstractResult onHandle(GitHubFollowersSpiderEvent event) {
        GHPersonSet<GHUser> followers = event.getModule();

        for (final GHUser user : followers) {
            EventDrivenHelper.fireEvent(new GitHubUserRecordEvent(user));
        }

        return new AbstractResult();
    }
}
