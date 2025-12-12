import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TicTacToeGame extends JFrame {

    private JButton[][] board;
    private char turn = 'X';

    public TicTacToeGame() {
        setTitle("Tic Tac Toe");
        setSize(320, 320);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 3));

        setupBoard();
        setVisible(true);
    }

    private void setupBoard() {
        board = new JButton[3][3];

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {

                JButton cell = new JButton();
                cell.setFont(new Font("Arial", Font.BOLD, 45));
                cell.setFocusable(false);

                cell.addActionListener(new CellClickListener());

                board[r][c] = cell;
                add(cell);
            }
        }
    }

    private class CellClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton btn = (JButton) e.getSource();

            if (!btn.getText().isEmpty()) return;

            btn.setText(String.valueOf(turn));

            if (hasWinner()) {
                finishGame(turn + " wins!");
            } else if (isFull()) {
                finishGame("Draw!");
            } else {
                turn = (turn == 'X') ? 'O' : 'X';
            }
        }
    }

    private boolean hasWinner() {
        // horizontal & vertical
        for (int i = 0; i < 3; i++) {
            if (same(board[i][0], board[i][1], board[i][2])) return true;
            if (same(board[0][i], board[1][i], board[2][i])) return true;
        }

        // diagonals
        return same(board[0][0], board[1][1], board[2][2]) ||
               same(board[0][2], board[1][1], board[2][0]);
    }

    private boolean same(JButton a, JButton b, JButton c) {
        return !a.getText().isEmpty() &&
               a.getText().equals(b.getText()) &&
               b.getText().equals(c.getText());
    }

    private boolean isFull() {
        for (JButton[] row : board) {
            for (JButton btn : row) {
                if (btn.getText().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void finishGame(String message) {
        for (JButton[] row : board) {
            for (JButton btn : row) {
                btn.setEnabled(false);
            }
        }
        JOptionPane.showMessageDialog(this, message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicTacToeGame::new);
    }
}
