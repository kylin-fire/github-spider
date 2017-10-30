package com.only.github.repository;

import com.google.common.collect.Lists;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.only.github.common.helper.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by leiteng on 2017/7/3.
 */
public class UserRepository {
    private static Logger logger = LoggerFactory.getLogger(UserRepository.class);
    private static File userFile;
    private static File seedFile;

    static {
        String path = UserRepository.class.getResource("/").getPath();
        userFile = new File(path + "user.txt");

        seedFile = new File(path + "seed.txt");
    }

    public boolean addUser(User user) {
        return add(user, userFile);
    }

    public boolean addSeed(User user) {
        return add(user, seedFile);
    }

    private boolean add(User user, File seedFile) {
        try {
            String record = JsonHelper.toJson(user);

            Files.asCharSink(seedFile, Charset.forName("UTF-8"), FileWriteMode.APPEND).write(record + "\n");

            return true;
        } catch (Exception e) {
            logger.error(String.format("addUser@user:%s", JsonHelper.toJson(user)), e);
            return false;
        }
    }

    public List<User> listUser() {
        return list(userFile);

    }

    public List<User> listSeed() {
        return list(seedFile);

    }

    private List<User> list(File userFile) {
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
