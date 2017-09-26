package com.only.github.event;

import org.kohsuke.github.GHPersonSet;
import org.kohsuke.github.GHUser;
import org.spring.ext.event.AbstractEvent;
import org.spring.ext.event.EventDrivenHelper;

/**
 * Created by leiteng on 2017/5/24.
 */
public class GitHubFollowersSpiderEvent extends AbstractEvent<GHPersonSet<GHUser>> {
    static {
        EventDrivenHelper.registerListener(new GitHubFollowerSpiderEventListener());
    }

    public GitHubFollowersSpiderEvent(GHPersonSet<GHUser> followers) {
        super(followers);
    }
}
