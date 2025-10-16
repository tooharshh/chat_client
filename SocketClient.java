import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@SuppressWarnings("serial")
public class SocketClient extends JFrame implements ActionListener, Runnable {
    JTextArea textArea = new JTextArea();
    JScrollPane jp = new JScrollPane(textArea);
    JTextField input_Text = new JTextField();

    Socket sk;
    BufferedReader br;
    PrintWriter pw;

    public SocketClient() {
        super("ChitChat - Modern Messaging");
        setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        Color backgroundColor = new Color(240, 242, 245);
        Color chatAreaColor = new Color(255, 255, 255);
        Color textColor = new Color(33, 33, 33);
        Color accentColor = new Color(0, 120, 212);
        
        textArea.setToolTipText("Chat History");
        textArea.setForeground(textColor);
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBackground(chatAreaColor);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        
        jp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(backgroundColor);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        input_Text.setText("");
        input_Text.setToolTipText("Type your message here...");
        input_Text.setForeground(textColor);
        input_Text.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        input_Text.setBackground(Color.WHITE);
        input_Text.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sendButton.setBackground(accentColor);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.addActionListener(this);
        
        inputPanel.add(input_Text, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        getContentPane().setBackground(backgroundColor);
        getContentPane().add(jp, BorderLayout.CENTER);
        getContentPane().add(inputPanel, BorderLayout.SOUTH);
        
        setSize(500, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        input_Text.requestFocus();
        input_Text.addActionListener(this);
    }

    public void serverConnection() {
        try {
            UIManager.put("OptionPane.background", new Color(240, 242, 245));
            UIManager.put("Panel.background", new Color(240, 242, 245));
            UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 12));
            
            String IP = JOptionPane.showInputDialog(this, 
                "Enter server IP address:\n(Use 127.0.0.1 for local connection)", 
                "Connect to Server",
                JOptionPane.QUESTION_MESSAGE);
            
            if (IP == null || IP.trim().isEmpty()) {
                System.exit(0);
                return;
            }
            
            sk = new Socket(IP, 1234);

            String name = JOptionPane.showInputDialog(this, 
                "Choose your nickname:", 
                "Enter Nickname",
                JOptionPane.QUESTION_MESSAGE);
            
            if (name == null || name.trim().isEmpty()) {
                name = "Anonymous";
            }

            br = new BufferedReader(new InputStreamReader(sk.getInputStream()));
            pw = new PrintWriter(sk.getOutputStream(), true);
            pw.println(name);

            new Thread(this).start();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to connect to server.\nPlease check the IP address and try again.", 
                "Connection Error", 
                JOptionPane.ERROR_MESSAGE);
            System.out.println(e + " Socket Connection error");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new SocketClient().serverConnection();
    }

    @Override
    public void run() {
        String data = null;
        try {
            while ((data = br.readLine()) != null) {
                String formattedData = formatMessage(data);
                textArea.append(formattedData + "\n");
                textArea.setCaretPosition(textArea.getText().length());
            }
        } catch (Exception e) {
            System.out.println(e + "--> Client run fail");
        }
    }
    
    private String formatMessage(String message) {
        java.time.LocalTime time = java.time.LocalTime.now();
        String timestamp = String.format("[%02d:%02d]", time.getHour(), time.getMinute());
        
        if (message.contains("**")) {
            return timestamp + " " + message.replace("**", "");
        } else {
            return timestamp + " " + message;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String data = input_Text.getText();
        pw.println(data);
        input_Text.setText("");
    }
}