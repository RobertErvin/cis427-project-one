package projectPackage;

import projectPackage.Constants.CMDS;

/*
 * Responsible for all user-specific in-memory storage
 * and permissions handling.
 */
public class User {
    public enum USER_STATUS {
        UNAUTHORIZED,
        AUTHORIZED;

        public static CMDS[] getPermissions(USER_STATUS status) {
            switch (status) {
                case UNAUTHORIZED:
                    return new CMDS[] {
                            CMDS.LOGIN,
                            CMDS.MSGGET,
                            CMDS.QUIT
                    };
                case AUTHORIZED:
                    return new CMDS[] {
                    		CMDS.MSGGET,
                    		CMDS.MSGSTORE,
                    		CMDS.LOGOUT,
                    		CMDS.QUIT
                    };
                default:
                    return new CMDS[0];
            }
        }
    }

    private String id;
    private String password;
    private USER_STATUS status;
    private boolean isRoot;

    public User() {
        this.status = USER_STATUS.UNAUTHORIZED;
        this.isRoot = false;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public USER_STATUS getStatus() {
        return this.status;
    }

    public void setStatus(USER_STATUS status) {
        this.status = status;
    }

    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    public boolean isRoot() {
        return this.isRoot;
    }

    public boolean isLoggedIn() {
        return this.status == USER_STATUS.AUTHORIZED;
    }
}
