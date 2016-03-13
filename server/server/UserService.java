package server;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import server.Constants.CMDS;
import server.User;

/*
 * Responsible for user-specific business logic
 */
public class UserService {
    private ArrayList<User> users;
    private ArrayList<User> activeUsers;
    private static UserService instance;

    public UserService() {
        this.users = initializeUsers();
        this.activeUsers = new ArrayList<User>();
    }
    
    public static UserService getInstance() {
    	if (instance == null) {
    		instance = new UserService();
    	}
    	
    	return instance;
    }

    public boolean hasPermission(User user, CMDS command) {
        for (server.Constants.CMDS cmd: User.USER_STATUS.getPermissions(user.getStatus())) {
            if (cmd == command) {
                return true;
            }
        }

        return false;
    }

    private ArrayList<User> initializeUsers() {
        ArrayList<User> users = new ArrayList<>();

        User root = new User();
        root.setId("root");
        root.setPassword("root01");
        root.setRoot(true);

        User john = new User();
        john.setId("john");
        john.setPassword("john01");

        User david = new User();
        david.setId("david");
        david.setPassword("david01");

        User mary = new User();
        mary.setId("mary");
        mary.setPassword("mary01");

        users.add(root);
        users.add(john);
        users.add(david);
        users.add(mary);

        return users;
    }

    // Verify a user is in the system and has access
    public User authorize(User userToAuth) {
        if (hasPermission(userToAuth, CMDS.LOGIN)) {
            for (User user : users) {
                if (user.getId().equals(userToAuth.getId()) &&
                        user.getPassword().equals(userToAuth.getPassword())) {
                    userToAuth.setStatus(User.USER_STATUS.AUTHORIZED);
                    userToAuth.setRoot(user.isRoot());
                    activeUsers.add(userToAuth);
                    return userToAuth;
                }
            }

            throw new IllegalArgumentException("Wrong userId or password.");
        }

        throw new IllegalAccessError("User doesn't have permission to login.");
    }
    
    public boolean isUserLoggedIn(String userId) {
    	for (User user: activeUsers) {
    		if (user.getId().equals(userId)) {
    			return true;
    		}
    	}
    	
    	return false;
    }

    // Logout the user
    public User logout(User userToLogout) {
        if (hasPermission(userToLogout, CMDS.LOGOUT)) {
            userToLogout.setStatus(User.USER_STATUS.UNAUTHORIZED);
            activeUsers.remove(userToLogout);
            return userToLogout;
        }

        throw new IllegalAccessError("User doesn't have permission to logout.");
    }
    
    public ArrayList<User> getAllActiveUsers() {
    	return activeUsers;
    }
    
    public String parseUserId(String data) {
    	String[] credentials = data.split(" ");
        if (credentials.length < 2 ) {
            throw new IllegalArgumentException();
        }
        return credentials[1];
    }
    
    // Parse LOGIN userId and password to ensure it matches with the given format
    public User parseUserAuthData(String data, String iNetAddress) {
        User user = new User();
        String[] credentials = data.split(" ");
        if (credentials.length < 3 ) {
            throw new IllegalArgumentException();
        }
        String userId = credentials[1];
        String password = credentials[2];

        user.setId(userId);
        user.setPassword(password);
        user.setIpAddress(iNetAddress);
        return user;
    }
}
