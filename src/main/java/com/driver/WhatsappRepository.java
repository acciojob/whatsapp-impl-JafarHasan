package com.driver;

import java.util.*;

import ch.qos.logback.core.encoder.EchoEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;



    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;

    }

    public String createUser(String name, String mobile) throws Exception{
        if(userMobile.contains(mobile)){
            throw new Exception("User already exists");
        }

        userMobile.add(mobile);

        User user=new User();
        user.setName(name);
        user.setMobile(mobile);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
        // The list contains at least 2 users where the first user is the admin.
// If there are only 2 users, the group is a personal chat and the group name should be kept as the name of the second user(other than admin)
// If there are 2+ users, the name of group should be "Group customGroupCount". For example, the name of first group would be "Group 1", second would be "Group 2" and so on.
// If group is successfully created, return group.
        Group group = new Group();
        //first user is the admin
        User admin=users.get(0);
        String groupName;

        if(users.size()==2){
            //if only 2 participants then 2nd is the group name
            groupName=users.get(1).getName();
        }
        else{
            groupName="Group"+(++customGroupCount);
        }
        group.setName(groupName);
        group.setNumberOfParticipants(customGroupCount);

        groupUserMap.put(group,users);
        adminMap.put(group,admin);
        return group;
    }

    public int createMessage(String content) {
        // The 'i^th' created message has message id 'i'.
        // Return the message id.
        Message message=new Message();

        message.setId(content.charAt(0)-'0');
        message.setContent(content);

        Date date=new Date();
        message.setTimestamp(date);

        return message.getId();
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
    //Throw "You are not allowed to send message" if the sender is not a member of the group
    //If the message is sent successfully, return the final number of messages in that group.
        if(!groupUserMap.containsKey(group.getName())){
            throw new Exception("Group does not exist");
        }
        if(!groupUserMap.containsKey(sender.getName())){
            throw new Exception("You are not allowed to send message");
        }

        List<Message> msgList=groupMessageMap.get(group);
        msgList.add(message);

        groupMessageMap.put(group,msgList);

        senderMap.put(message,sender);
        return msgList.size();

    }

    public String changeAdmin(User approver, User user, Group group)throws Exception {
        //Throw "Group does not exist" if the mentioned group does not exist
//Throw "Approver does not have rights" if the approver is not the current admin of the group
//Throw "User is not a participant" if the user is not a part of the group
//Change the admin of the group to "user" and return "SUCCESS".
        if(!groupUserMap.containsKey(group)) {
            throw new Exception("Group does not exist");
        }

        User isAdmin=adminMap.get(group);

        if(!isAdmin.getName().equals(approver.getName())){
            throw new Exception("Approver does not have rights");
        }

        List<User> userList=groupUserMap.get(group);
        if(!userList.contains(user)) {
            throw new Exception("User is not a participant");
        }
        adminMap.remove(group);
        adminMap.put(group,user);
        return "SUCESS";
    }

    public int removeUser(User user) throws Exception {
        //If user is not found in any group, throw "User not found" exception
        //If user is found in a group and it is the admin, throw "Cannot remove admin" exception
        //If user is not the admin, remove the user from the group, remove all its messages from all the databases, and update relevant attributes accordingly.
        //If user is removed successfully, return (the updated number of users in the group + the updated number of messages in group + the updated number of overall messages)

        int updatedNoOfUser=0;
        int updatedNoOfMsg=0;
        int overallMsg=0;
        List<Group> groupList=new ArrayList<>();

       for(Group group:groupUserMap.keySet()){

          List<User> userList=groupUserMap.get(group);
          for(User findUser: userList){

              String name=findUser.getName();
              if(name.equals(user.getName())){

                  User newUser=adminMap.get(group);
                  if(newUser.getName().equals(name)){//admin found
                      throw new Exception("Cannot remove admin");
                  }
                  groupList.add(group);
                  userList.remove(findUser); //remove user from group

                  updatedNoOfUser=userList.size();
              }

          }
       }

       List<Message> msgList = new ArrayList<>();
       //removing messages from senderMap
        for(Message message:senderMap.keySet()){
            User findUser=senderMap.get(message);
            if(findUser.getName().equals(user.getName())){
                msgList.add(message);
                senderMap.remove(message);
            }
        }

        //removing messages from Group also
        for(Group newGroup:groupMessageMap.keySet()){
            if(groupList.contains(newGroup)){
                List<Message> list=groupMessageMap.get(newGroup);
                for(Message message:list){
                    if(msgList.contains(message)){
                        list.remove(message);
                        updatedNoOfMsg=list.size();
                    }
                }
            }
        }
        return updatedNoOfUser+updatedNoOfMsg+overallMsg;

    }

    public String findMessage(Date start, Date end, int k) throws Exception {
        // Find the Kth latest message between start and end (excluding start and end)
// If the number of messages between given time is less than K, throw "K is greater than the number of messages" exception
        List<Message> messageDateRange=new ArrayList<>();
        for(Group group:groupMessageMap.keySet()){

            List<Message> msgList=groupMessageMap.get(group);
            for(Message message:msgList){
                if(message.getTimestamp().after(start) && message.getTimestamp().before(end)){
                    messageDateRange.add(message);
                }
            }
            if (messageDateRange.size() < k) {
                throw new Exception("K is greater than the number of messages");
            }
            messageDateRange.sort((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp())); // descending order sorting of timestamp

            return messageDateRange.get(k - 1).getContent(); // Kth latest message
        }
        return "";
    }
}

