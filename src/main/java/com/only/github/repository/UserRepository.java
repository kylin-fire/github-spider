package com.only.github.repository;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.ext.common.helper.JsonHelper;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by leiteng on 2017/7/3.
 */
public class UserRepository {
    private static Logger logger = LoggerFactory.getLogger(UserRepository.class);
    private static File userFile = new File("/home/leiteng/user.txt");

    public boolean addUser(User user) {
        try {
            String record = JsonHelper.toJson(user);

            Files.append(record + "\n", userFile, Charset.forName("UTF-8"));

            return true;
        } catch (Exception e) {
            logger.error(String.format("addUser@user:%s", JsonHelper.toJson(user)), e);
            return false;
        }
    }

    public List<User> listUser() {
        try {
            List<String> lines = Files.readLines(userFile, Charset.forName("UTF-8"));

            List<User> userList = Lists.newArrayListWithExpectedSize(lines.size());

            for (String each : lines) {
                User user = User.valueOf(each);

                userList.add(user);
            }
            return userList;
        } catch (Exception e) {
            logger.error(String.format("listUser"), e);
            return null;
        }

    }
}
