package ch.bfh.sd.five.todo.helper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TodoPassword {

    public static String getHashedPassword (String password)  {
        return new BCryptPasswordEncoder().encode(password);
    }

    public static boolean compare (String password, String hashedPassword) {
        return new BCryptPasswordEncoder().matches(password, hashedPassword);
    }
}
