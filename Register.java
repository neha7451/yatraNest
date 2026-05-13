import javax.swing.*;
import java.awt.*;
import java.sql.*;
public class Register extends JFrame {
    JTextField user;
    JPasswordField pass;

    public Register() {
        setTitle("Register");
        setSize(300,200);
        setLayout(new GridLayout(3,2));
        user = new JTextField();
        pass = new JPasswordField();
        JButton btn = new JButton("Register");

        add(new JLabel("Username")); add(user);
        add(new JLabel("Password")); add(pass);
        add(btn);
        btn.addActionListener(e -> registerUser());
        setVisible(true);
    }

    void registerUser() {
        try (Connection c = DB.connect()) {
            PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO users(username,password) VALUES(?,?)");
            ps.setString(1, user.getText());
            ps.setString(2, new String(pass.getPassword()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registered!");
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "User exists!");
        }
    }
}