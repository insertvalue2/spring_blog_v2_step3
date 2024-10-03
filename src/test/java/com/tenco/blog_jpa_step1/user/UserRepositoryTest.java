package com.tenco.blog_jpa_step1.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.tenco.blog_jpa_step3.user.User;
import com.tenco.blog_jpa_step3.user.UserRepository;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findById_test(){
        // given
        int id = 1;

        // when
        User user = userRepository.findById(id);

        // then
        assertNotNull(user);
        assertEquals(id, user.getId());
    }
    
}