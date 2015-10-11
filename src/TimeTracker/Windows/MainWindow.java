package TimeTracker.Windows;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import TimeTracker.TimeTracker;
import TimeTracker.Tasks.TableTaskModel;
import TimeTracker.Tasks.Task;
import TimeTracker.Tasks.TaskTime;
import TimeTracker.Tasks.TaskCategory;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;

    public static final Comparator<Task> TASK_ORDER_UPDATED_DESC = new Comparator<Task>() {
            public int compare(Task t1, Task t2) {
                return t2.getUpdated().compareTo(t1.getUpdated());
            }
    };
	
	private JTable					tableTasks;
	private JScrollPane				tableTasksScroll;
	private TableTaskModel				tableTasksModel;
	private TaskPanel				editTaskPanel;
	
	public MainWindow() {
		super("TimeTracker v0.0.1 (Alpha version)");
		// Title
		JLabel labelHeading = new JLabel("Task list");
		labelHeading.setFont(new Font("Arial", Font.BOLD, 20));
		JPanel panelHeading = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelHeading.add(labelHeading);
		// Create components
		initTableTasks();
		initPanelNew();
		// Add components
		setLayout(new BorderLayout());
		add(panelHeading, BorderLayout.NORTH);
		add(tableTasksScroll, BorderLayout.CENTER);
		add(editTaskPanel, BorderLayout.SOUTH);
		// Add state listener
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent event) {
				// Nothing yet				
			}			
			@Override
			public void windowIconified(WindowEvent event) {
				// Nothing yet
			}			
			@Override
			public void windowDeiconified(WindowEvent event) {
				// Nothing yet
			}
			@Override
			public void windowDeactivated(WindowEvent event) {
				// Nothing yet
			}			
			@Override
			public void windowClosing(WindowEvent event) {
				// Discard unsaved changes / ask to save them
				if (editTaskPanel.checkTaskSaved()) {
					MainWindow.this.dispose();
				}
			}			
			@Override
			public void windowClosed(WindowEvent event) {
				// Nothing yet
			}			
			@Override
			public void windowActivated(WindowEvent event) {
				// Nothing yet
			}
		});
	}

	private void initTableTasks() {
		tableTasksModel = new TableTaskModel();
		tableTasks = new JTable(tableTasksModel);
		tableTasks.setFillsViewportHeight(true);
		tableTasks.getColumnModel().getColumn(0).setMinWidth(48);
		tableTasks.getColumnModel().getColumn(1).setPreferredWidth(400);
		tableTasks.getColumnModel().getColumn(1).setMinWidth(172);
		tableTasks.getColumnModel().getColumn(2).setMinWidth(80);
		tableTasks.getColumnModel().getColumn(3).setMinWidth(200);
		tableTasks.getColumnModel().getColumn(4).setMinWidth(200);
		tableTasks.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
		        ListSelectionModel selectionModel = (ListSelectionModel)event.getSource();
				if (!selectionModel.isSelectionEmpty()) {
					int 	selectedRow = selectionModel.getLeadSelectionIndex();
					Task	selectedTask = tableTasksModel.getTasks().elementAt(selectedRow);
					if (selectedTask != null) {
						TimeTracker.getInstance().setTaskActive(selectedTask);
					}					
				}
			}
		});
		tableTasksScroll = new JScrollPane(tableTasks);
	}
	
	private void initPanelNew() {
		editTaskPanel = new TaskPanel();
		editTaskPanel.addSyncronisationListener(new TaskPanel.TaskListSyncronisationListener() {
			@Override
			public void discardedTask(Task task, Boolean previousWasEmpty) {
				// Nothing yet
			}
			@Override
			public void clearSelection() {
				tableTasks.getSelectionModel().clearSelection();
			}
			@Override
			public void selectTask(Task task) {
				int taskIndex = tableTasksModel.getTasks().indexOf(task);
				if (taskIndex >= 0) {
					tableTasks.getSelectionModel().setSelectionInterval(taskIndex, taskIndex);
				} else {
					clearSelection();
				}
			}
			@Override
			public void savedTask(Task task) {
				// Update task list
				TimeTracker.getInstance().updateTasks();
			}
			@Override
			public void pauseTracking(Task task) {
				// Update task list
				TimeTracker.getInstance().setStatus(TaskTime.Status.PAUSED);
				TimeTracker.getInstance().updateTasks();
			}
			@Override
			public void startTracking(Task task) {
				// Save task
				editTaskPanel.saveTask();
				// Update task list
				TimeTracker.getInstance().setStatus(TaskTime.Status.TRACKING);
				TimeTracker.getInstance().updateTasks();
				// Hide main window
				MainWindow.this.setVisible(false);
			}
			@Override
			public void stopTracking(Task task) {
				// Save task
				editTaskPanel.saveTask();
				// Update task list
				TimeTracker.getInstance().setStatus(TaskTime.Status.STOPPED);
				TimeTracker.getInstance().updateTasks();
			}
		});
	}

	public Vector<Task> getTasks() {
		return tableTasksModel.getTasks();
	}

	public void setCategories(Vector<TaskCategory> categoryList) {
		editTaskPanel.setCategories(categoryList);
	}
	
	public void setTasks(Vector<Task> tasks) {
		// Filter task list
		Vector<Task>	tasksVisisble = new Vector<Task>();
		for (Iterator<Task> iterator = tasks.iterator(); iterator.hasNext(); ) {
			Task task = iterator.next();
			if (!task.getHidden() && !task.getDeleted()) {
				tasksVisisble.add(task);
			}
		}
        Collections.sort(tasksVisisble, TASK_ORDER_UPDATED_DESC);
		// Add visible tasks to table
		tableTasksModel.setTasks(tasksVisisble);
		tableTasksModel.fireTableDataChanged();
	}

	public Boolean setTaskActive(Task activeTask) {
		return editTaskPanel.editTask(activeTask);
	}

	public void updateTasks() {
		tableTasksModel.fireTableDataChanged();
		// Update selection
		int taskIndex = tableTasksModel.getTasks().indexOf(editTaskPanel.getTask());
		if (taskIndex >= 0) {
			tableTasks.getSelectionModel().setSelectionInterval(taskIndex, taskIndex);
		} else {
			tableTasks.getSelectionModel().clearSelection();
		}
	}
	
}
