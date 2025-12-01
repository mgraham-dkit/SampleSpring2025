package web_patterns.samplespring2025.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import web_patterns.samplespring2025.persistence.UserDao;

import java.sql.SQLException;

@Slf4j
@Service
public class UserService {
    private UserDao userDao;

    public UserService(UserDao dao){
        this.userDao = dao;
    }

    public void shutdownService(){
        userDao.closeConnection();
    }

    public boolean login(String username, String password) throws SQLException {
        if(username == null || username.isBlank() || password == null || password.isBlank()){
            throw new IllegalArgumentException("username and password cannot be blank");
        }

        log.info("Login attempt: {}", username);
        return userDao.login(username, password);
    }
}
