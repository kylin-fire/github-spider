package com.only.github.event;

import com.google.common.eventbus.Subscribe;
import org.kohsuke.github.GHPersonSet;
import org.kohsuke.github.GHUser;
import org.spring.ext.event.AbstractResult;
import org.spring.ext.event.EventDrivenHelper;
import org.spring.ext.event.listener.AbstractEventListener;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by leiteng on 2017/5/24.
 */
public class GitHubFollowerSpiderEventListener extends AbstractEventListener<GitHubFollowersSpiderEvent, AbstractResult<Boolean>> {
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

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
