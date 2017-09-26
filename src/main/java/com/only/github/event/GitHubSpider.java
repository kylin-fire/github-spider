package com.only.github.event;

import com.only.github.repository.User;
import com.only.github.repository.UserRepository;
import org.apache.commons.collections.CollectionUtils;
import org.kohsuke.github.GHPersonSet;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.spring.ext.event.EventDrivenHelper;

import java.io.IOException;
import java.util.List;

/**
 * Created by leiteng on 2017/5/22.
 */
public class GitHubSpider {
    private static UserRepository userRepository = new UserRepository();

    public static void main(String[] args) throws IOException {
        GitHubSpider spider = new GitHubSpider();
        spider.spider();
    }

    private void spider() {
        try {

            List<User> userList = userRepository.listUser();

            GitHub gitHub = GitHub.connect("oleone", "cd1019a52a6ad65e1b5aedd4e9fc983f1175509a");

            if (CollectionUtils.isNotEmpty(userList)) {
                for (User each : userList) {
                    GHUser user = gitHub.getUser(each.getLogin());

                    spiderFollower(user);
                }
            }
            // 默认从oldratlee开始爬取
            else {
                GHUser user = gitHub.getUser("oldratlee");

                spiderFollower(user);
            }
        } catch (Exception e) {
            System.err.print(e);
        }
    }

    private void spiderFollower(GHUser user) throws IOException {
        GHPersonSet<GHUser> followers = user.getFollowers();

        if (followers != null && !followers.isEmpty()) {
            EventDrivenHelper.publishEvent(new GitHubFollowersSpiderEvent(followers));
        }

        GHPersonSet<GHUser> following = user.getFollows();

        if (following != null && !following.isEmpty()) {
            EventDrivenHelper.publishEvent(new GitHubFollowersSpiderEvent(following));
        }
    }
}
