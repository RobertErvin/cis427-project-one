package projectPackage;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import projectPackage.Constants.CMDS;
import projectPackage.User;

/*
 * Responsible for user-specific business logic
 */
public class UserService {
    private ArrayList<User> users;

    public UserService() {
        this.users = initializeUsers();
    }

    public boolean hasPermission(User user, CMDS command) {
        for (CMDS cmd: User.USER_STATUS.getPermissions(user.getStatus())) {
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
                    return userToAuth;
                }
            }

            throw new IllegalArgumentException("Wrong userId or password.");
        }

        throw new IllegalAccessError("User doesn't have permission to login.");
    }

    // Logout the user
    public User logout(User userToLogout) {
        if (hasPermission(userToLogout, CMDS.LOGOUT)) {
            userToLogout.setStatus(User.USER_STATUS.UNAUTHORIZED);
            return userToLogout;
        }

        throw new IllegalAccessError("User doesn't have permission to logout.");
    }
    
    // Parse LOGIN userId and password to ensure it matches with the given format
    public User parseUserAuthData(String data) {
        User user = new User();
        Pattern pattern = Pattern.compile("(LOGIN\\s(\\w+)\\s(\\w+))");
        Matcher matcher = pattern.matcher(data);
        if (!matcher.find() || matcher.groupCount() < 4) {
            throw new IllegalArgumentException();
        }

        String userId = matcher.group(1);
        String password = matcher.group(2);

        user.setId(userId);
        user.setPassword(password);

        return user;
    }
}
