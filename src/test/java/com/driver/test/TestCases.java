package com.driver.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.driver.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest(classes = Application.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestCases {
//    @Test
//    public void testMessageConstructor() {
//        int id = 1;
//        String content = "Hello, World!";
//        Date date = new Date();
//
//        // Create an instance of Message with the new constructor
//        Message message = new Message(id, content, date);
//
//        // Verify the fields
//        assertEquals(id, message.getId());
//        assertEquals(content, message.getContent());
//        assertEquals(date, message.getTimestamp());
//    }
}
