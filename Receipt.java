import javax.swing.*;
import java.time.LocalDate;
public class Receipt extends JFrame {
    public Receipt(String id, String name, String room,
                   String in, String out, double roomCharges,
                   double gst, double service, double total,
                   String method)
                    {
        JTextArea area = new JTextArea();
        area.setFont(
                new java.awt.Font(
                        "Monospaced",
                        java.awt.Font.PLAIN,
                        12
                )
        );

        String paymentStatus = method.equalsIgnoreCase("Cash")
                        ? "UNPAID" : "PAID";

        String receipt =
                "====================================\n" +
                "          YatraNest Receipt\n" +
                "====================================\n\n" +

                "Booking ID     : " + id + "\n" +
                "Customer       : " + name + "\n" +
                "Room No        : " + room + "\n" +
                "Check-in       : " + in + "\n" +
                "Check-out      : " + out + "\n\n" +

                "------------------------------------\n" +
                "Room Charges   : " + String.format("%.2f", roomCharges) + "\n" +
                "GST (18%)      : " + String.format("%.2f", gst) + "\n" +
                "Service (5%)   : " + String.format("%.2f", service) + "\n" +
                "------------------------------------\n" +
                "TOTAL          : " + String.format("%.2f", total) + "\n" +
                "------------------------------------\n\n" +

                "Payment Status : " + paymentStatus + "\n" +
                "Payment Method : " + method + "\n" +
                "Payment Date   : " + LocalDate.now() + "\n" +

                "------------------------------------\n" +
                " Thank you for choosing YatraNest!\n" +
                " Visit Again\n" +
                "====================================";

        area.setText(receipt);
        area.setEditable(false);
        add(new JScrollPane(area));
        setSize(450,500);
        setVisible(true);
    }
}