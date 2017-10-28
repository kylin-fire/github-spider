package com.only.github.event;

import com.only.github.common.event.AbstractEvent;
import com.only.github.common.event.EventDrivenHelper;
import org.kohsuke.github.GHUser;

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
