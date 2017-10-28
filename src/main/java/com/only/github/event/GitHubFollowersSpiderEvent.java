package com.only.github.event;

import com.only.github.common.event.AbstractEvent;
import com.only.github.common.event.EventDrivenHelper;
import org.kohsuke.github.GHPersonSet;
import org.kohsuke.github.GHUser;

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
