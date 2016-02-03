package projectPackage;

public class Constants {
	public enum CMDS {
        MSGGET,
        MSGSTORE,
        SHUTDOWN,
        LOGIN,
        LOGOUT,
        QUIT
    }
	
	public enum RESPONSES {
		OK,
		LOGIN_ERROR,
		LOGOUT_ERROR,
		FORMAT_ERROR,
		BAD_USERNAME_OR_PASSWORD_ERROR,
		BAD_PERMISSIONS_ERROR;
		
		@Override
		public String toString() {
			switch (ordinal()) {
				case 0:
					return "200 OK";
				case 1:
					return "401 You are not currently logged in, login first.";
				case 2:
					return "401 You are already logged in, logout first.";
				case 3:
					return "300 message format error";
				case 4:
					return "410 Wrong UserID or Password";
				case 5:
					return "402 User not allowed to execute this command.";
				default:
					return "";
					
			}
		}
	}
}
