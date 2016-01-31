package projectPackage;

import java.util.ArrayList;

import projectPackage.SocketServer.CMDS;

/**
 * Created by robert on 1/31/16.
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

    public User authorize(User userToAuth) {
        if (hasPermission(userToAuth, CMDS.LOGIN)) {
            for (User user : users) {
                if (user.getId().equals(userToAuth.getId()) &&
                        user.getPassword().equals(userToAuth.getPassword())) {
                    userToAuth.setStatus(User.USER_STATUS.LOGGED_IN);
                    return userToAuth;
                }
            }

            throw new IllegalArgumentException("Wrong userId or password.");
        }

        throw new IllegalAccessError("User doesn't have permission to login.");
    }

    public User logout(User userToLogout) {
        if (hasPermission(userToLogout, CMDS.LOGOUT)) {
            userToLogout.setStatus(User.USER_STATUS.UNAUTHORIZED);
            return userToLogout;
        }

        throw new IllegalAccessError("User doesn't have permission to logout.");
    }
}
