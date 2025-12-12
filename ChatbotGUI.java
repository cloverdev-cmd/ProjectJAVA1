import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatbotGUI extends JFrame implements ActionListener {

    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;

    private String userName = "";

    public ChatbotGUI() {
        setTitle("Chatbot");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

       
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

   
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 16));
        inputField.addActionListener(this);

        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        
        askForName();

        setVisible(true);
    }

    private void askForName() {
        userName = JOptionPane.showInputDialog(this, "Hello! I'm your chatbot.\nWhat's your name?");
        if (userName == null || userName.trim().isEmpty()) {
            userName = "User";
        }
        chatArea.append("Chatbot: Nice to meet you, " + userName + "!\n");
        chatArea.append("Chatbot: Ask me anything!\n\n");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = inputField.getText().trim();
        if (message.isEmpty()) return;

        chatArea.append(userName + ": " + message + "\n");
        inputField.setText("");

        if (message.equalsIgnoreCase("bye")) {
            chatArea.append("Chatbot: Goodbye, " + userName + "! Take care.\n");
            inputField.setEditable(false);
            sendButton.setEnabled(false);
        } else {
            String reply = getBotReply(message);
            chatArea.append("Chatbot: " + reply + "\n\n");
        }
    }

    private String getBotReply(String text) {
        text = text.toLowerCase();

        if (text.contains("hello") || text.contains("hi")) {
            return "Hello! How can I help you?";
        }

        if (text.contains("earth")) {
            return "The Earth is about 4.54 billion years old and is the third planet from the Sun.";
        }

        if (text.contains("sun")) {
            return "The Sun is 1.39 million km wide — about 109 times wider than Earth.";
        }

        if (text.contains("java")) {
            return "Java was created by James Gosling at Sun Microsystems and released in 1995.";
        }

        if (text.contains("philippines")) {
            return "The Philippines has 7,641 islands and its capital is Manila.";
        }

        if (text.contains("ai")) {
            return "AI stands for Artificial Intelligence — machines that simulate human thinking.";
        }

        if (text.contains("president of usa")) {
            return "As of 2025, the President of the United States is Joe Biden.";
        }

        if (text.contains("largest ocean")) {
            return "The Pacific Ocean is the largest ocean on Earth.";
        }

        
        return "I'm still learning! But I can answer basic questions about Earth, Java, AI, and more.";
    }

    public static void main(String[] args) {
        new ChatbotGUI();
    }
}
