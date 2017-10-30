package com.only.github.event;

import com.only.github.common.event.EventDrivenHelper;
import com.only.github.repository.User;
import com.only.github.repository.UserRepository;
import org.apache.commons.collections.CollectionUtils;
import org.kohsuke.github.GHPersonSet;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;

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
        // spider.splitEmail();
    }

    private void splitEmail() {
        List<User> userList = userRepository.listUser();

        for (User each : userList) {
            if (each.isMatch()) {
                System.out.println(each.getEmail() + ",");
            }
        }
    }

    private void spider() {
        try {

            List<User> userList = userRepository.listSeed();

            // ff2fc87f04c44a444e60 618c68f8a25b88f7167e8542cfcc382a39732a9a
             GitHub gitHub = GitHub.connect("oleone", "7cab1d449890072d84abe4771dd14236da135550");

//            GitHub gitHub = GitHub.connectUsingPassword("oleone", "1qaz~WSX");

            if (CollectionUtils.isNotEmpty(userList)) {
                for (User each : userList) {
                    GHUser user = gitHub.getUser(each.getLogin());

                    EventDrivenHelper.publishEvent(new GitHubUserRecordEvent(user));

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
