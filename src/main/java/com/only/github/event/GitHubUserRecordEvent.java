package com.only.github.event;

import org.kohsuke.github.GHUser;
import org.spring.ext.event.AbstractEvent;
import org.spring.ext.event.EventDrivenHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by leiteng on 2017/5/24.
 */
public class GitHubUserRecordEvent extends AbstractEvent<GHUser> {

    static {
        EventDrivenHelper.registerListener(new GitHubUserRecordEventListener());
    }

    public GitHubUserRecordEvent(GHUser result) {
        super(result);
    }

    @Override
    public void onSuccess() {
        super.onSuccess();
    }

    @Override
    public void onFailure(String message) {
        super.onFailure(message);
    }

    @Override
    public void onException(Exception e) {
        super.onException(e);
    }
}
