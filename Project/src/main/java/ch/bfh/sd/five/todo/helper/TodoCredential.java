package ch.bfh.sd.five.todo.helper;

public class TodoCredential {
    private String name;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isValid() {
        return (name != null && ! name.isEmpty() && password != null && ! password.isEmpty());
    }
}
