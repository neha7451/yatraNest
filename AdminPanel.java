import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminPanel extends JFrame {
    JTextField roomNo; JTextField price;
    JComboBox<String> type; JTable table;
    DefaultTableModel model;
    JLabel totalRoomsLabel; JLabel availableRoomsLabel;
    JLabel bookedRoomsLabel; JLabel revenueLabel;

    public AdminPanel() {
        setTitle("Admin Panel");
        setSize(1100,700);
        setLayout(new BorderLayout(10,10));

 // =========================================
// TOP CONTAINER
// =========================================
JPanel topContainer = new JPanel();
topContainer.setLayout(
        new BoxLayout(topContainer,
                BoxLayout.Y_AXIS)
);

// =========================================
// DASHBOARD PANEL
// =========================================
JPanel dashboard = new JPanel(
        new GridLayout(1,4,10,10) );
totalRoomsLabel = new JLabel();
availableRoomsLabel = new JLabel();
bookedRoomsLabel = new JLabel();
revenueLabel = new JLabel();

dashboard.add(totalRoomsLabel);
dashboard.add(availableRoomsLabel);
dashboard.add(bookedRoomsLabel);
dashboard.add(revenueLabel);

// =========================================
// FORM PANEL
// =========================================
JPanel top = new JPanel(
        new GridLayout(1,6,10,10) );

roomNo = new JTextField();
price = new JTextField();
type = new JComboBox<>(
        new String[]{"Single","Double","Deluxe"}
);
top.add(new JLabel("Room No"));
top.add(roomNo);

top.add(new JLabel("Type"));
top.add(type);

top.add(new JLabel("Price"));
top.add(price);

// =========================================
// ADD BOTH PANELS
// =========================================
topContainer.add(dashboard);
topContainer.add(top);
add(topContainer, BorderLayout.NORTH);

 // =========================================
 // TABLE
 // =========================================
 model = new DefaultTableModel(
    new String[]{
        "ID", "Room No", "Type",
        "Capacity", "Price", "Status"
    },0
);
table = new JTable(model);
add(new JScrollPane(table),
     BorderLayout.CENTER);

// =========================================
// BUTTON PANEL
// =========================================
        JPanel btnPanel = new JPanel();
        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete =new JButton("Delete");
        JButton refresh =new JButton("Refresh");
        JButton logout =new JButton("Logout");

        btnPanel.add(add);
        btnPanel.add(update);
        btnPanel.add(delete);
        btnPanel.add(refresh);
        btnPanel.add(logout);
        add(btnPanel, BorderLayout.SOUTH);

 // =========================================
// ACTIONS
// =========================================
    add.addActionListener( e -> addRoom() );
    update.addActionListener( e -> updateRoom() );
    delete.addActionListener(e -> deleteRoom());
        refresh.addActionListener(e -> {
           loadRooms();
           loadDashboard();
        });
        logout.addActionListener(e -> {
            new Login();
            dispose();
        });
        loadRooms();
        loadDashboard();
        setVisible(true);
    }

// =========================================
// LOAD ROOMS
// =========================================
void loadRooms() {
     model.setRowCount(0);
    try(Connection c = DB.connect()) {
        ResultSet rs = c.createStatement().executeQuery("SELECT * FROM rooms");
        while(rs.next()) {
            model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("room_no"),
                        rs.getString("type"),
                        rs.getInt("capacity"),
                        rs.getDouble("price"),
                        rs.getString("status")
                });
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================
    // LOAD DASHBOARD
    // =========================================
    void loadDashboard() {
        try(Connection c = DB.connect()) {
            // TOTAL ROOMS
            ResultSet total =c.createStatement().executeQuery("SELECT COUNT(*) AS total FROM rooms");
            totalRoomsLabel.setText( "Total Rooms : " + total.getInt("total"));

            // AVAILABLE ROOMS
            ResultSet available = c.createStatement().executeQuery(
                                    "SELECT COUNT(*) AS available FROM rooms WHERE status='available'" );

            availableRoomsLabel.setText("Available Rooms : " + available.getInt("available") );

            // BOOKED ROOMS
            ResultSet booked =c.createStatement().executeQuery(
                                    "SELECT COUNT(*) AS booked FROM rooms WHERE status='booked'");

            bookedRoomsLabel.setText("Booked Rooms : " + booked.getInt("booked"));

            // REVENUE
            ResultSet revenue =c.createStatement().executeQuery(
                                    "SELECT IFNULL(SUM(amount),0) AS revenue FROM bookings");

            revenueLabel.setText("Revenue : ₹" + revenue.getDouble("revenue"));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================
    // ROOM CAPACITY
    // =========================================
    int getCapacity(String roomType) {
        if(roomType.equals("Single")) {
            return 1;
        }
        if(roomType.equals("Double")) {
            return 2;
        }
        return 4;
    }

    // =========================================
    // ADD ROOM
    // =========================================
    void addRoom() {
        try(Connection c = DB.connect()) {
            String selectedType =type.getSelectedItem().toString();
            int capacity =getCapacity(selectedType);
            PreparedStatement ps =c.prepareStatement(
                 "INSERT INTO rooms(room_no,type,capacity,price,status) VALUES(?,?,?,?, 'available')");

            ps.setString(1,roomNo.getText());
            ps.setString(2,selectedType);
            ps.setInt(3,capacity);
            ps.setDouble(4,Double.parseDouble(price.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"Room Added Successfully!");
            loadRooms();
            loadDashboard();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================
    // UPDATE ROOM
    // =========================================
    void updateRoom() {int row =table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this,"Select Room!");
            return;
        }
        int id =(int) model.getValueAt(row,0);
        try(Connection c = DB.connect()) { String selectedType = type.getSelectedItem().toString();
            int capacity = getCapacity(selectedType);
            PreparedStatement ps = c.prepareStatement(
                            "UPDATE rooms SET room_no=?, type=?, capacity=?, price=? WHERE id=?"
                    );

            ps.setString(1,roomNo.getText());
            ps.setString(2,selectedType);
            ps.setInt(3,capacity);
            ps.setDouble(4,Double.parseDouble(price.getText()));
            ps.setInt(5,id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"Room Updated!");

            loadRooms();
            loadDashboard();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================
    // DELETE ROOM
    // =========================================
    void deleteRoom() {
        int row =table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this,"Select Room!");
            return;
        }

        int id =(int) model.getValueAt(row,0);
        try(Connection c = DB.connect()) {
            PreparedStatement ps =c.prepareStatement("DELETE FROM rooms WHERE id=?");
            ps.setInt(1,id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"Room Deleted!");
            loadRooms();
            loadDashboard();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}