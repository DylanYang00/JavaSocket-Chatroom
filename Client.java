package version3;

import java.io.Serializable;


/**
 * 聊天用户的封装类
 */
public class Client implements Serializable {
    private String name;
    private String userID;
    private String password;
    private String message=null;

    public Client(String name, String userID, String password) {
        this.name = name;
        this.userID = userID;
        this.password = password;
    }

    public Client(String userID, String password) {
        this.userID = userID;
        this.password = password;
    }

    public String getMessage() {
        return message;
    }
    public String getUserID() {
        return userID;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return userID.equals(client.userID) &&
                password.equals(client.password);
    }


}
