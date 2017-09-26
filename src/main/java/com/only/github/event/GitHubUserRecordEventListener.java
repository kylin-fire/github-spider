package com.only.github.event;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.eventbus.Subscribe;
import com.only.github.repository.User;
import com.only.github.repository.UserRepository;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.ext.client.cache.GuavaCacheClient;
import org.spring.ext.common.helper.BeanHelper;
import org.spring.ext.event.AbstractResult;
import org.spring.ext.event.listener.AbstractEventListener;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by leiteng on 2017/5/24.
 */
public class GitHubUserRecordEventListener extends AbstractEventListener<GitHubUserRecordEvent, AbstractResult<Object>> {
    private static Logger logger = LoggerFactory.getLogger(GitHubUserRecordEventListener.class);
    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    private static GuavaCacheClient cacheClient;
    private static UserRepository userRepository = new UserRepository();

    static {
        init();
    }

    private static void init() {
        try {
            List<User> userList = userRepository.listUser();

            cacheClient = new GuavaCacheClient();
            Cache<Serializable, Serializable> cache = CacheBuilder.newBuilder().maximumSize(100000).expireAfterAccess(3600, TimeUnit.SECONDS).weakValues().build();
            cacheClient.resetCache(cache);

            for (User each : userList) {
                cache.put(each.getId(), each.getLogin());
            }
        } catch (Exception e) {

        }

        executorService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                logger.warn("cache size:" + cacheClient.size());
            }
        }, 5, 10, TimeUnit.SECONDS);
    }

    @Subscribe
    public void listen(GitHubUserRecordEvent event) {
        handleEvent(event);
    }

    protected AbstractResult onHandle(GitHubUserRecordEvent event) {
        AbstractResult spiderResult = new AbstractResult();

        GHUser user = event.getModule();

        if (user != null) {
            String id = String.valueOf(user.getId());
            String login = cacheClient.get(id);
            if (login == null) {

                try {
                    cacheClient.put(id, user.getLogin(), 0);

                    User myUser = BeanHelper.copyProperties(new User(), user);

                    if (isMatch(user)) {
                        myUser.setStatus(1);
                    }

                    userRepository.addUser(myUser);
                } catch (Exception e) {
                    logger.error(String.format("onHandle@event:%s", event), e);
                }
                spiderResult.setSuccess(true);
                return spiderResult;
            }
        }

        spiderResult.setSuccess(true);
        return spiderResult;
    }

    private boolean isMatch(GHUser user) throws IOException {
        boolean match = false;
        if (user.getEmail() != null) {
            // 在中国
            if (isChinese(user.getLocation()) || isChineseEmail(user.getEmail())) {
                // 忽略tmall、taobao、alibaba
                if (isAlibabaUser(user.getCompany()) || isAlibabaUser(user.getEmail())) {

                } else if (isJavaDeveloper(user)) {
                    match = true;
                }
            }
        }
        return match;
    }

    private boolean isChineseEmail(String email) {
        String[] emailList = {"163.com", "qq.com", "126.com", "sina.com", "sohu.com", "aliyun.com", ".cn"};
        for (String each : emailList) {
            if (email.contains(each)) {
                return true;
            }
        }

        return false;
    }

    private boolean isJavaDeveloper(GHUser user) throws IOException {
        Map<String, GHRepository> repositories = user.getRepositories();
        Set<Map.Entry<String, GHRepository>> entries = repositories.entrySet();
        for (Map.Entry<String, GHRepository> entry : entries) {
            String language = entry.getValue().getLanguage();
            if ("java".equals(language) || "Java".equals(language)) {
                return true;
            }
        }

        return false;
    }

    private boolean isChinese(String location) {
        if (location == null) {
            return false;
        }

        location = location.toLowerCase();

        String[] cities = {"beijing", "shanghai", "guangzhou", "shenzhen", "hangzhou", "wuhan", "nanjing", "dalian", "chengdu", "changsha", "suzhou", "tianjing", "xian", "chongqing"};
        for (String city : cities) {
            if (location.contains(city)) {
                return true;
            }
        }

        return false;
    }

    private boolean isAlibabaUser(String company) {
        if (company == null) {
            return false;
        }

        company = company.toLowerCase();
        String[] companyList = {"tmall", "taobao", "alibaba", "alipay"};
        for (String each : companyList) {
            if (company.contains(each)) {
                return true;
            }
        }

        return false;
    }
}
