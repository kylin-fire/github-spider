package com.only.github.event;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.eventbus.Subscribe;
import com.only.github.common.cache.GuavaCacheClient;
import com.only.github.common.event.AbstractResult;
import com.only.github.common.event.listener.AbstractEventListener;
import com.only.github.common.helper.BeanHelper;
import com.only.github.repository.User;
import com.only.github.repository.UserRepository;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final String[] cities = {"china", "中国", "beijing", "北京", "shanghai", "上海", "guangzhou", "广州", "shenzhen", "深圳", "hangzhou", "杭州", "wuhan", "武汉", "nanjing", "南京", "dalian", "大连", "chengdu", "成都", "changsha", "长沙", "suzhou", "苏州", "tianjin", "天津", "xian", "西安", "chongqing", "重庆", "shenyang", "沈阳", "ningbo", "宁波"};
    private final String[] emailList = {"163.com", "qq.com", "126.com", "sina.com", "sohu.com", "aliyun.com", ".cn"};
    private final String[] companyList = {"阿里", "ali", "阿里巴巴", "alibaba", "淘宝", "taobao", "支付宝", "alipay", "天猫", "tmall", "腾讯", "tencent", "百度", "baidu", "美团", "meituan", "大众点评", "dianping", "新美大", "华为", "huawei", "中兴", "今日头条", "蘑菇街", "mogujie", "菜鸟", "京东", "jidong", "阿里云", "aliyun", "挖财", "wacai"};

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
            if (isChinese(user.getLocation()) || isChineseEmail(user.getEmail()) || isChineseCompany(user.getCompany())) {
                if (isMatchLanguage(user)) {
                    match = true;
                }
            }
        }
        return match;
    }

    private boolean isChineseCompany(String company) {
        if (company != null) {
            company = company.toLowerCase();
            for (String each : companyList) {
                if (company.contains(each)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isChineseEmail(String email) {

        for (String each : emailList) {
            if (email.contains(each)) {
                return true;
            }
        }

        return false;
    }

    private boolean isMatchLanguage(GHUser user) throws IOException {
        Map<String, GHRepository> repositories = user.getRepositories();
        Set<Map.Entry<String, GHRepository>> entries = repositories.entrySet();
        for (Map.Entry<String, GHRepository> entry : entries) {
            String language = entry.getValue().getLanguage();
            language = language.toLowerCase();

            if ("java".equals(language) || "php".equals(language) || "go".equals(language) || "objective-c".equals(language) || "javascript".equals(language)) {
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


        for (String city : cities) {
            if (location.contains(city)) {
                return true;
            }
        }

        return false;
    }

}
