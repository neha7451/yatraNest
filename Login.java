import javax.swing.*;
import java.awt.*;
import java.sql.*;
public class Login extends JFrame {
    public static String loggedInUser = "";
    JTextField user;
    JPasswordField pass;
    JComboBox<String> role;

    public Login() {
        setTitle("YatraNest Login");
        setSize(400,250);
        setLayout(new GridLayout(4,2,10,10));

        user = new JTextField();
        pass = new JPasswordField();
        role = new JComboBox<>(new String[]{"User","Admin"});

        JButton login = new JButton("Login");
        JButton register = new JButton("Register");

        add(new JLabel("Username"));
        add(user);
        add(new JLabel("Password"));
        add(pass);
        add(new JLabel("Role"));
        add(role);
        add(login);
        add(register);

        login.addActionListener(e -> loginUser());
        register.addActionListener(e -> new Register());
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    void loginUser() {
        String u = user.getText();
        String p = new String(pass.getPassword());
        String r = role.getSelectedItem().toString();

        // ADMIN LOGIN
        if(r.equals("Admin")) {
            if(u.equals("admin") && p.equals("admin123")) {
                new AdminPanel();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Wrong Admin Credentials");
            }
            return;
        }

        // USER LOGIN
        try(Connection c = DB.connect()) {
            PreparedStatement ps = c.prepareStatement(
                    "SELECT * FROM users WHERE username=? AND password=?");
            ps.setString(1, u);
            ps.setString(2, p);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                loggedInUser = u;
                new UserPanel();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid Login");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}