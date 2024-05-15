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
    WhatsappController whatsappController = new WhatsappController();
    @Test
    public void testCreateUser() throws Exception {
        String mobile = "1234567890";
        String status = whatsappController.createUser("John Doe", mobile);
        assertEquals(status, "SUCCESS");
    }

    @Test
    public void testCreateUser_Exception() throws Exception {
        String mobile = "1234567890";
        String status = whatsappController.createUser("John Doe", mobile);
        assertEquals(status, "SUCCESS");

        try {
            String status1 = whatsappController.createUser("John Doe", mobile);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "User already exists");
        }
    }

    @Test
    public void testCreateGroup_PersonalChat() throws Exception {
        User admin = new User("John Doe", "1234567890");
        User user = new User("Jane Smith", "0987654321");
        List<User> users = new ArrayList<>();
        users.add(admin);
        users.add(user);

        Group group = whatsappController.createGroup(users);
        String groupName = group.getName();
        int groupSize = group.getNumberOfParticipants();
        assertEquals(groupName, user.getName());
        assertEquals(2, groupSize);
    }

    @Test
    public void testCreateGroup_MultipleUsers() throws Exception {
        User admin = new User("John Doe", "1234567890");
        User user1 = new User("Jane Smith", "0987654321");
        User user2 = new User("Bob Johnson", "0123456789");
        List<User> users = new ArrayList<>();
        users.add(admin);
        users.add(user1);
        users.add(user2);

        Group group = whatsappController.createGroup(users);
        String groupName = group.getName();
        int groupSize = group.getNumberOfParticipants();
        assertEquals(groupName, "Group 1");
        assertEquals(3, groupSize);
    }

    @Test //FAIL Expected :Group1   Actual :Group 1
    public void testCreateGroup_MultipleGroups() throws Exception {
        User admin1 = new User("John Doe", "1234567890");
        User user1 = new User("Jane Smith", "0987654321");
        User admin2 = new User("Bob Johnson", "0123456789");
        User user2 = new User("Mike", "9876543210");
        User user3 = new User("Williams", "987654210");
        User admin3 = new User("Johnson", "012345689");
        User user4 = new User("Mike Williams", "987654310");
        User user5 = new User("Bob", "987654320");
        List<User> users1 = new ArrayList<>();
        users1.add(admin1);
        users1.add(user1);
        users1.add(user3);
        List<User> users2 = new ArrayList<>();
        users2.add(admin2);
        users2.add(user2);
        List<User> users3 = new ArrayList<>();
        users3.add(admin3);
        users3.add(user4);
        users3.add(user5);

        Group group1 = whatsappController.createGroup(users1);
        String groupName1 = group1.getName();
        int groupSize1 = group1.getNumberOfParticipants();
        assertEquals(groupName1, "Group 1");
        assertEquals(groupSize1, 3);

        Group group2 = whatsappController.createGroup(users2);
        String groupName2 = group2.getName();
        int groupSize2 = group2.getNumberOfParticipants();
        assertEquals(groupName2, user2.getName());
        assertEquals(groupSize2, 2);

        Group group3 = whatsappController.createGroup(users3);
        String groupName3 = group3.getName();
        int groupSize3 = group3.getNumberOfParticipants();
        assertEquals(groupName3, "Group 2");
        assertEquals(groupSize3, 3);
    }

    @Test //PASS
    public void testCreateMessage() {
        int messageId = whatsappController.createMessage("Hello World!");
        assertEquals(messageId, 1);
    }

    @Test //PASS
    public void testCreateMultipleMessages() {
        int messageId1 = whatsappController.createMessage("Hello World!");
        int messageId2 = whatsappController.createMessage("How are you?");
        assertEquals(messageId1, 1);
        assertEquals(messageId2, 2);
    }

    @Test
    public void testSendMessage_Success() throws Exception {
        User sender = new User("John Doe", "1234567890");
        User receiver1 = new User("Jane Doe", "0987654321");
        User receiver2 = new User("Bob Smith", "1029384756");
        List<User> users = new ArrayList<>();
        users.add(sender);
        users.add(receiver1);
        users.add(receiver2);
        Group group = whatsappController.createGroup(users);
        Message message = new Message(1, "Hello, group!");

        int numMessages = whatsappController.sendMessage(message, sender, group);
        assertEquals(1, numMessages);
    }

    @Test //PASS
    public void testSendMessage_GroupDoesNotExist() {
        User sender = new User("John Doe", "1234567890");
        Group group = new Group("Test Group", 2);
        Message message = new Message(1, "Hello, group!");

        try {
            int numMessages = whatsappController.sendMessage(message, sender, group);
        } catch (Exception e) {
            assertEquals("Group does not exist", e.getMessage());
        }
    }

    @Test
    public void testSendMessage_NotAMember() throws Exception {
        User sender = new User("John Doe", "1234567890");
        User receiver1 = new User("Jane Doe", "0987654321");
        User receiver2 = new User("Bob Smith", "1029384756");
        List<User> users = new ArrayList<>();
        users.add(receiver1);
        users.add(receiver2);
        Group group = whatsappController.createGroup(users);
        Message message = new Message(1, "Hello, group!");

        try {
            int numMessages = whatsappController.sendMessage(message, sender, group);
        } catch (Exception e) {
            assertEquals("You are not allowed to send message", e.getMessage());
        }
    }

    @Test   //FAIL  because "msgList" is null
    public void testSendMultipleMessages_Success() throws Exception {
        // create a group and add users to it
        List<User> users = new ArrayList<>();
        User user1 = new User("User 1", "111-111-1111");
        User user2 = new User("User 2", "222-222-2222");
        users.add(user1);
        users.add(user2);
        Group group = whatsappController.createGroup(users);

        // create messages
        Message message1 = new Message(1, "Hello World");
        Message message2 = new Message(2, "How are you?");
        Message message3 = new Message(3, "I'm good, thanks for asking.");

        // send messages
        int numMessages = whatsappController.sendMessage(message1, user1, group);
        assertEquals(1, numMessages);
        numMessages = whatsappController.sendMessage(message2, user1, group);
        assertEquals(2, numMessages);
        numMessages = whatsappController.sendMessage(message3, user2, group);
        assertEquals(3, numMessages);
    }

    @Test
    public void testChangeAdmin_Success() throws Exception {
        //Create group and add users
        List<User> users = new ArrayList<>();
        User admin = new User("John Doe", "1234567890");
        User user1 = new User("Jane Doe", "0987654321");
        User user2 = new User("Bob Smith", "1029384756");
        users.add(admin);
        users.add(user1);
        users.add(user2);
        Group group = whatsappController.createGroup(users);

        //Change admin
        String status = whatsappController.changeAdmin(admin, user1, group);
        assertEquals(status, "SUCCESS");
    }

    @Test
    public void testChangeAdmin_GroupNotExists() throws Exception {
        //Create group and add users
        List<User> users = new ArrayList<>();
        User admin = new User("John Doe", "1234567890");
        User user1 = new User("Jane Doe", "0987654321");
        User user2 = new User("Bob Smith", "1029384756");
        users.add(admin);
        users.add(user1);
        users.add(user2);
        Group group = new Group("Group", 3);

        //Change admin
        try {
            String status = whatsappController.changeAdmin(admin, user1, group);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Group does not exist");
        }
    }

    @Test
    public void testChangeAdmin_ApproverDoesNotHaveRights() throws Exception {
        //Create group and add users
        List<User> users = new ArrayList<>();
        User admin = new User("John Doe", "1234567890");
        User user1 = new User("Jane Doe", "0987654321");
        User user2 = new User("Bob Smith", "1029384756");
        users.add(admin);
        users.add(user1);
        users.add(user2);
        Group group = whatsappController.createGroup(users);

        //Change admin
        try {
            String status = whatsappController.changeAdmin(user2, user1, group);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Approver does not have rights");
        }
    }

    @Test
    public void testChangeAdmin_UserNotParticipant() throws Exception {
        //Create group and add users
        List<User> users = new ArrayList<>();
        User admin = new User("John Doe", "1234567890");
        User user1 = new User("Jane Doe", "0987654321");
        User user2 = new User("Bob Smith", "1029384756");
        User user3 = new User("Bob", "109384756");
        users.add(admin);
        users.add(user1);
        users.add(user2);
        Group group = whatsappController.createGroup(users);

        //Change admin
        try {
            String status = whatsappController.changeAdmin(admin, user3, group);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "User is not a participant");
        }
    }

}
