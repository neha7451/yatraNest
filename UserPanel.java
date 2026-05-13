import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class UserPanel extends JFrame {
    JTextField name; JTextField phone; JTextField address;
    JTextField email; JTextField checkin; JTextField checkout;
    JComboBox<String> payment;
    JTable roomTable; JTable bookingTable;

    DefaultTableModel roomModel;
    DefaultTableModel bookingModel;

    public UserPanel() {
        setTitle("User Booking");
        setSize(1100,700);
        setLayout(new BorderLayout(10,10));

        // =========================================
        // FORM PANEL
        // =========================================
        JPanel form = new JPanel(new GridLayout(2,8,10,10));
        name = new JTextField();
        phone = new JTextField();
        address = new JTextField();
        email = new JTextField();
        checkin = new JTextField();
        checkout = new JTextField();
        payment = new JComboBox<>(new String[]{"Cash","Card","UPI"} );

        // =========================
        // ROW 1
        // =========================
        form.add(new JLabel("Name"));
        form.add(name);
        form.add(new JLabel("Phone"));
        form.add(phone);
        form.add(new JLabel("Address"));
        form.add(address);
        form.add(new JLabel("Email"));
        form.add(email);

        // =========================
        // ROW 2
        // =========================
        form.add(new JLabel("Checkin (yyyy-MM-dd)"));
        form.add(checkin);
        form.add(new JLabel("Checkout (yyyy-MM-dd)"));
        form.add(checkout);
        form.add(new JLabel("Payment"));
        form.add(payment);

        // EMPTY CELLS
        form.add(new JLabel(""));
        form.add(new JLabel(""));
        add(form, BorderLayout.NORTH);

        // =========================================
        // ROOM TABLE
        // =========================================

        roomModel = new DefaultTableModel(
                new String[]{
                        "ID", "Room No","Type", "Price"
                },0
        );
        roomTable = new JTable(roomModel);

        // =========================================
        // BOOKING TABLE
        // =========================================
        bookingModel = new DefaultTableModel(
                new String[]{
                        "Booking ID", "Room No", "Customer",
                        "Checkin", "Checkout"
                },0
        );
        bookingTable = new JTable(bookingModel);

        // =========================================
        // SPLIT PANE
        // =========================================

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(roomTable),
                new JScrollPane(bookingTable)
        );
        split.setDividerLocation(300);
        add(split, BorderLayout.CENTER);

        // =========================================
        // BUTTON PANEL
        // =========================================
        JPanel btn = new JPanel();
        JButton searchRooms =new JButton("Search Rooms");
        JButton book =new JButton("Book");
        JButton cancelBooking =new JButton("Cancel Booking");
        JButton refresh = new JButton("Refresh");
        JButton logout =new JButton("Logout");
        btn.add(searchRooms);
        btn.add(book);
        btn.add(cancelBooking);
        btn.add(refresh);
        btn.add(logout);
        add(btn, BorderLayout.SOUTH);

        // =========================================
        // ACTIONS
        // =========================================
        searchRooms.addActionListener(
                e -> loadRooms());

        book.addActionListener(
                e -> bookRoom());

        cancelBooking.addActionListener(
                e -> cancelBooking());

        refresh.addActionListener(e -> {
            loadRooms();
            loadBookings();});

        logout.addActionListener(e -> {
            new Login();
            dispose();
        });
        loadBookings();
        setVisible(true);
    }

    // =========================================
    // LOAD AVAILABLE ROOMS
    // =========================================
    void loadRooms() {
        roomModel.setRowCount(0);
        try (Connection c = DB.connect()) {
            String query =
                    "SELECT * FROM rooms " +  "WHERE room_no NOT IN (" +
                    "SELECT room_no FROM bookings " +
                    "WHERE (? < checkout AND ? > checkin)" +
                    ")";
            PreparedStatement ps =c.prepareStatement(query);

            ps.setString(1,checkin.getText());
            ps.setString(2,checkout.getText());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                roomModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("room_no"),
                        rs.getString("type"),
                        rs.getDouble("price")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================
    // LOAD BOOKINGS OF LOGGED IN USER
    // =========================================
    void loadBookings() {
        bookingModel.setRowCount(0);
        try (Connection c = DB.connect()) {
            PreparedStatement ps = c.prepareStatement(
                            "SELECT * FROM bookings WHERE username=?"
                    );
            ps.setString(1,Login.loggedInUser);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                bookingModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("room_no"),
                        rs.getString("name"),
                        rs.getString("checkin"),
                        rs.getString("checkout")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================
    // BOOK ROOM
    // =========================================
    void bookRoom() {
        int row = roomTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,"Select a room!" );
            return;
        }
        String roomNo =roomModel.getValueAt(row,1).toString();
        double pricePerNight =(double) roomModel.getValueAt(row,3);

        try {
            LocalDate inDate =LocalDate.parse(checkin.getText());
            LocalDate outDate =LocalDate.parse(checkout.getText());
            long days =ChronoUnit.DAYS.between(
                            inDate, outDate
                    );
            if (days <= 0) {
                JOptionPane.showMessageDialog(this,"Invalid Dates!");
                return;
            }

            // =========================
            // BILLING
            // =========================
            double roomCharges =pricePerNight * days;
            double gst =roomCharges * 0.18;
            double service =roomCharges * 0.05;
            double total =roomCharges + gst + service;
            Connection c = DB.connect();
            PreparedStatement ps =c.prepareStatement(
           "INSERT INTO bookings(username,name,phone,address,email,checkin,checkout,room_no,amount,payment_method) [Type text]VALUES(?,?,?,?,?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS );

            ps.setString(1,Login.loggedInUser);
            ps.setString(2,name.getText());
            ps.setString(3,phone.getText());
            ps.setString(4,address.getText());
            ps.setString(5,email.getText());
            ps.setString(6,checkin.getText());
            ps.setString(7,checkout.getText());
            ps.setString(8,roomNo);
            ps.setDouble(9,total);
            ps.setString(10,payment.getSelectedItem().toString());
            ps.executeUpdate();
            ResultSet key = ps.getGeneratedKeys();
            key.next();

            // =========================
            // RECEIPT
            // =========================
            new Receipt(
                    key.getString(1), name.getText(),
                    roomNo, checkin.getText(), checkout.getText(),
                    roomCharges, gst, service, total,
                    payment.getSelectedItem().toString()
            );
            loadRooms();
            loadBookings();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Use yyyy-MM-dd format!");
        }
    }

    // =========================================
    // CANCEL BOOKING
    // =========================================
    void cancelBooking() {
        int row = bookingTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,"Select Booking!");
            return;
        }

        int bookingId =(int) bookingModel.getValueAt(row,0);
        try (Connection c = DB.connect()) {
            PreparedStatement delete =c.prepareStatement("DELETE FROM bookings WHERE id=?");
            delete.setInt(1, bookingId);
            delete.executeUpdate();
            JOptionPane.showMessageDialog(this,"Booking Cancelled!");
            loadRooms();
            loadBookings();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}