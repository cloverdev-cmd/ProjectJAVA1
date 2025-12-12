import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalculatorGUI extends JFrame implements ActionListener {

    private JTextField display;
    private String input = "";
    private String operator = "";
    private double firstNumber = 0;

    public CalculatorGUI() {
        setTitle("Swing Calculator");
        setSize(300, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Display
        display = new JTextField();
        display.setEditable(false);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font("Arial", Font.BOLD, 24));
        add(display, BorderLayout.NORTH);

        // Button panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 4, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] keys = {
                "7", "8", "9", "+",
                "4", "5", "6", "-",
                "1", "2", "3", "*",
                "0", "C", "=", "/"
        };

        for (String key : keys) {
            JButton btn = new JButton(key);
            btn.setFont(new Font("Arial", Font.BOLD, 20));
            btn.addActionListener(this);
            panel.add(btn);
        }

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String label = event.getActionCommand();

        switch (label) {
            case "C":
                clear();
                break;
            case "+":
            case "-":
            case "*":
            case "/":
                setOperator(label);
                break;
            case "=":
                compute();
                break;
            default:
                append(label);
                break;
        }
    }

    private void append(String value) {
        input += value;
        display.setText(input);
    }

    private void clear() {
        input = "";
        operator = "";
        display.setText("");
    }

    private void setOperator(String op) {
        if (!input.isEmpty()) {
            firstNumber = Double.parseDouble(input);
            operator = op;
            input = "";
        }
    }

    private void compute() {
        if (input.isEmpty() || operator.isEmpty()) return;

        double secondNumber = Double.parseDouble(input);
        double result = 0;

        switch (operator) {
            case "+":
                result = firstNumber + secondNumber;
                break;
            case "-":
                result = firstNumber - secondNumber;
                break;
            case "*":
                result = firstNumber * secondNumber;
                break;
            case "/":
                if (secondNumber == 0) {
                    display.setText("Error: /0");
                    clear();
                    return;
                }
                result = firstNumber / secondNumber;
                break;
        }

        display.setText(String.valueOf(result));
        input = "";
        operator = "";
    }

    public static void main(String[] args) {
        new CalculatorGUI();
    }
}
