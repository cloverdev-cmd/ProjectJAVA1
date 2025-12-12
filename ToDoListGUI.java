import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ToDoListGUI {
    private JFrame frame;
    private DefaultListModel<String> taskListModel;
    private DefaultListModel<Task> taskObjectsModel; // store Task objects
    private JList<String> taskList;
    private ArrayList<Task> historyTasks;
    private final String TASK_FILE = "tasks.dat";
    private final String HISTORY_FILE = "history.dat";
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoListGUI::new);
    }

    public ToDoListGUI() {
        historyTasks = loadHistory();
        ArrayList<Task> loadedTasks = loadTasks();

        frame = new JFrame("To-Do List GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Task list models
        taskListModel = new DefaultListModel<>();
        taskObjectsModel = new DefaultListModel<>();
        for (Task task : loadedTasks) {
            taskObjectsModel.addElement(task);
            taskListModel.addElement(task.toString());
        }

        taskList = new JList<>(taskListModel);
        JScrollPane scrollPane = new JScrollPane(taskList);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Task");
        JButton deleteBtn = new JButton("Delete Task");
        JButton renameBtn = new JButton("Rename Task");
        JButton historyBtn = new JButton("View Deleted");
        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(renameBtn);
        buttonPanel.add(historyBtn);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Add Task with dropdown date/time
        addBtn.addActionListener(e -> openAddTaskDialog());

        // Delete Task
        deleteBtn.addActionListener(e -> {
            int index = taskList.getSelectedIndex();
            if (index != -1) {
                Task removedTask = taskObjectsModel.remove(index);
                taskListModel.remove(index);
                historyTasks.add(removedTask);
                saveTasks();
                saveHistory();
            } else {
                JOptionPane.showMessageDialog(frame, "Select a task to delete!");
            }
        });

        // Rename Task
        renameBtn.addActionListener(e -> {
            int index = taskList.getSelectedIndex();
            if (index != -1) {
                Task oldTask = taskObjectsModel.getElementAt(index);
                String newName = JOptionPane.showInputDialog(frame, "Rename task:", oldTask.getName());
                if (newName != null && !newName.trim().isEmpty()) {
                    oldTask.setName(newName);
                    taskListModel.set(index, oldTask.toString());
                    saveTasks();
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Select a task to rename!");
            }
        });

        // View History
        historyBtn.addActionListener(e -> openHistoryWindow());

        frame.setVisible(true);
    }

    // Opens Add Task dialog with 12-hour format and AM/PM
    private void openAddTaskDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField nameField = new JTextField();
        Calendar now = Calendar.getInstance();

        // Year, Month, Day dropdowns
        Integer[] years = new Integer[11];
        for (int i = 0; i < 11; i++) years[i] = now.get(Calendar.YEAR) + i;

        Integer[] months = new Integer[12];
        for (int i = 0; i < 12; i++) months[i] = i + 1;

        Integer[] days = new Integer[31];
        for (int i = 0; i < 31; i++) days[i] = i + 1;

        JComboBox<Integer> yearBox = new JComboBox<>(years);
        JComboBox<Integer> monthBox = new JComboBox<>(months);
        JComboBox<Integer> dayBox = new JComboBox<>(days);

        // 12-hour format hours and minutes/seconds
        Integer[] hours = new Integer[12];
        for (int i = 0; i < 12; i++) hours[i] = i + 1;

        Integer[] minutes = new Integer[60];
        for (int i = 0; i < 60; i++) minutes[i] = i;

        Integer[] seconds = new Integer[60];
        for (int i = 0; i < 60; i++) seconds[i] = i;

        String[] amPmOptions = {"AM", "PM"};

        JComboBox<Integer> hourBox = new JComboBox<>(hours);
        JComboBox<Integer> minuteBox = new JComboBox<>(minutes);
        JComboBox<Integer> secondBox = new JComboBox<>(seconds);
        JComboBox<String> amPmBox = new JComboBox<>(amPmOptions);

        // Set defaults to current time
        int hour = now.get(Calendar.HOUR);
        if (hour == 0) hour = 12; // 12-hour format correction
        hourBox.setSelectedItem(hour);
        minuteBox.setSelectedItem(now.get(Calendar.MINUTE));
        secondBox.setSelectedItem(now.get(Calendar.SECOND));
        amPmBox.setSelectedIndex(now.get(Calendar.AM_PM));

        // Layout for date and time
        JPanel datePanel = new JPanel(new GridLayout(2, 4));
        datePanel.add(yearBox);
        datePanel.add(monthBox);
        datePanel.add(dayBox);
        datePanel.add(new JLabel(""));
        datePanel.add(hourBox);
        datePanel.add(minuteBox);
        datePanel.add(secondBox);
        datePanel.add(amPmBox);

        panel.add(new JLabel("Task Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Select Date & Time:"));
        panel.add(datePanel);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add Task", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                Calendar cal = Calendar.getInstance();
                int selectedHour = (Integer) hourBox.getSelectedItem();
                if ("PM".equals(amPmBox.getSelectedItem()) && selectedHour != 12) {
                    selectedHour += 12;
                }
                if ("AM".equals(amPmBox.getSelectedItem()) && selectedHour == 12) {
                    selectedHour = 0;
                }
                cal.set(
                        (Integer) yearBox.getSelectedItem(),
                        (Integer) monthBox.getSelectedItem() - 1,
                        (Integer) dayBox.getSelectedItem(),
                        selectedHour,
                        (Integer) minuteBox.getSelectedItem(),
                        (Integer) secondBox.getSelectedItem()
                );
                Date date = cal.getTime();
                Task task = new Task(name, date);
                taskObjectsModel.addElement(task);
                taskListModel.addElement(task.toString());
                saveTasks();
            }
        }
    }

    // Opens history window
    private void openHistoryWindow() {
        JFrame historyFrame = new JFrame("Deleted Tasks");
        historyFrame.setSize(400, 300);
        DefaultListModel<String> historyListModel = new DefaultListModel<>();
        JList<String> historyList = new JList<>(historyListModel);
        for (Task t : historyTasks) {
            historyListModel.addElement(t.toString());
        }

        JButton restoreBtn = new JButton("Restore Selected Task");
        restoreBtn.addActionListener(e -> {
            int index = historyList.getSelectedIndex();
            if (index != -1) {
                Task restored = historyTasks.remove(index);
                taskObjectsModel.addElement(restored);
                taskListModel.addElement(restored.toString());
                historyListModel.remove(index);
                saveTasks();
                saveHistory();
            } else {
                JOptionPane.showMessageDialog(historyFrame, "Select a task to restore!");
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(historyList), BorderLayout.CENTER);
        panel.add(restoreBtn, BorderLayout.SOUTH);
        historyFrame.add(panel);
        historyFrame.setVisible(true);
    }

    // Task class
    static class Task implements Serializable {
        private String name;
        private Date addedTime;

        public Task(String name, Date addedTime) {
            this.name = name;
            this.addedTime = addedTime;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getAddedTime() {
            return addedTime;
        }

        @Override
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
            return name + " (Task: " + sdf.format(addedTime) + ")";
        }
    }

    // Save/load tasks & history
    private void saveTasks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TASK_FILE))) {
            ArrayList<Task> tasks = new ArrayList<>();
            for (int i = 0; i < taskObjectsModel.size(); i++) tasks.add(taskObjectsModel.getElementAt(i));
            oos.writeObject(tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TASK_FILE))) {
            tasks = (ArrayList<Task>) ois.readObject();
        } catch (IOException | ClassNotFoundException ignored) {}
        return tasks;
    }

    private void saveHistory() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HISTORY_FILE))) {
            oos.writeObject(historyTasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Task> loadHistory() {
        ArrayList<Task> tasks = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HISTORY_FILE))) {
            tasks = (ArrayList<Task>) ois.readObject();
        } catch (IOException | ClassNotFoundException ignored) {}
        return tasks;
    }
}
