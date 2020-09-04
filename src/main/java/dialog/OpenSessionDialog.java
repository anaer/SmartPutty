package dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import constants.ButtonImage;
import control.InvokeProgram;
import dao.DbManager;
import model.ConfigSession;
import ui.MainFrame;

/**
 * 打开会话对话框.
 *
 * @author lvcn
 * @version $Id: OpenSessionDialog.java, v 1.0 Jul 22, 2019 3:45:59 PM lvcn Exp $
 */
public class OpenSessionDialog implements SelectionListener, MouseListener {
    private MainFrame mainFrame = null;
    private Shell     dialog    = null;
    protected Object  result;
    private Table     table;
    private DbManager dbm;
    /**
     * 新增按钮.
     */
    private Button    addButton;
    /**
     * 编辑按钮.
     */
    private Button    editButton;
    /**
     * 删除按钮.
     */
    private Button    deleteButton;
    /**
     * 连接按钮.
     */
    private Button    connectButton;
    /**
     * 启动Putty.
     */
    private Button    puttyWindow;

    public OpenSessionDialog(MainFrame mainFrame, Shell parent) {
        this.dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        this.mainFrame = mainFrame;
        this.dbm = DbManager.getDbManagerInstance();
        init();
    }

    /**
     * Initialize window in a safer way. Useful to avoid "Leaking This In Constructor" warnings.
     */
    private void init() {
        dialog.setImage(ButtonImage.OPEN_IMAGE);
        dialog.setText("Open Session Dialog");
        dialog.setSize(665, 300);

        table = new Table(dialog, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        table.setBounds(0, 0, 560, 257);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.addMouseListener(this);

        TableColumn tableNameColumn = new TableColumn(table, SWT.NONE);
        tableNameColumn.setWidth(150);
        tableNameColumn.setText("Name");

        TableColumn tableHostColumn = new TableColumn(table, SWT.NONE);
        tableHostColumn.setWidth(125);
        tableHostColumn.setText("Host");

        TableColumn tablePortColumn = new TableColumn(table, SWT.NONE);
        tablePortColumn.setWidth(50);
        tablePortColumn.setText("Port");

        TableColumn tableUserColumn = new TableColumn(table, SWT.NONE);
        tableUserColumn.setWidth(75);
        tableUserColumn.setText("User");

        TableColumn tableTimeColumn = new TableColumn(table, SWT.NONE);
        tableTimeColumn.setWidth(65);
        tableTimeColumn.setText("Protocol");

        TableColumn sessionColumn = new TableColumn(table, SWT.NONE);
        sessionColumn.setWidth(75);
        sessionColumn.setText("Session");

        loadTable();

        int xPos = 565;
        // button
        addButton = new Button(dialog, SWT.LEFT);
        addButton.setBounds(xPos, 5, 80, 27);
        addButton.setText("Add   ");
        addButton.setImage(ButtonImage.ADD_IMAGE);
        addButton.setToolTipText("Add a new connection");
        addButton.addSelectionListener(this);

        editButton = new Button(dialog, SWT.LEFT);
        editButton.setBounds(xPos, 38, 80, 27);
        editButton.setText("Edit ");
        editButton.setImage(ButtonImage.EDIT_IMAGE);
        editButton.setToolTipText("Edit selected connection");
        editButton.addSelectionListener(this);

        deleteButton = new Button(dialog, SWT.LEFT);
        deleteButton.setBounds(xPos, 70, 80, 27);
        deleteButton.setText("Delete");
        deleteButton.setImage(ButtonImage.DELETE_IMAGE);
        deleteButton.setToolTipText("Delete selected connection/s");
        deleteButton.addSelectionListener(this);

        puttyWindow = new Button(dialog, SWT.LEFT);
        puttyWindow.setBounds(xPos, 103, 80, 27);
        puttyWindow.setText("Putty");
        puttyWindow.setImage(ButtonImage.PUTTY_IMAGE);
        puttyWindow.setToolTipText("Open selected connection in a single window");
        puttyWindow.addSelectionListener(this);

        connectButton = new Button(dialog, SWT.NONE);
        connectButton.setBounds(xPos, 230, 80, 27);
        connectButton.setText("Connect");
        connectButton.setImage(ButtonImage.CONNECT_IMAGE);
        connectButton.setToolTipText("Open selected connection/s in a tab");
        connectButton.addSelectionListener(this);

        dialog.pack();
        dialog.open();
    }

    public ConfigSession getCurrentSelectSession() {
        if (table.getSelection().length > 0) {
            return (ConfigSession) (table.getSelection()[0].getData("session"));
        } else {
            return null;
        }
    }

    public void loadTable() {
        table.removeAll();
        List<ConfigSession> sessions = dbm.getAllSessions();
        for (ConfigSession session : sessions) {
            TableItem tableItem = new TableItem(table, SWT.NONE);
            tableItem.setData("session", session);
            tableItem
                .setText(new String[] { session.getName(), session.getHost(), session.getPort(),
                                        session.getUser(), session.getProtocol().getName(),
                                        session.getSession() });
        }
    }

    /**
     * Open all selected sessions in tabs.
     */
    private void openSelectedSessions() {
        TableItem[] tableItems = table.getSelection();
        ArrayList<ConfigSession> sessions = new ArrayList<>();
        for (TableItem tableItem : tableItems) {
            ConfigSession csession = dbm
                .querySessionBySession((ConfigSession) tableItem.getData("session"));
            if (csession != null) {
                sessions.add(csession);
            }
        }
        dialog.dispose();

        for (ConfigSession session : sessions) {
            this.mainFrame.addSession(null, session);
        }
    }

    /**
     * Open a Putty session in a window outside program.
     */
    private void openPutty() {
        TableItem[] tableItems = table.getSelection();
        if (tableItems != null) {
            ConfigSession csession = dbm
                .querySessionBySession((ConfigSession) tableItems[0].getData("session"));
            InvokeProgram.invokeSinglePutty(csession);
            dialog.dispose();
        }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent arg0) {
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        // System.out.println(e.getSource().toString());
        if (e.getSource() == addButton) {
            new NewSessionDialog(null, this, "add");
        } else if (e.getSource() == editButton) {
            if (table.getSelection().length == 1) {
                new NewSessionDialog(null, this, "edit");
            } else {
                MessageDialog.openInformation(dialog, "Warning", "Please select one record!");
            }
        } else if (e.getSource() == deleteButton) {
            if (this.table.getSelection().length == 0) {
                MessageDialog.openInformation(dialog, "Warning",
                    "Please select at least one record!");
                return;
            }
            TableItem[] tableItems = table.getSelection();
            for (TableItem item : tableItems) {
                ConfigSession session = (ConfigSession) item.getData("session");
                dbm.deleteSession(session);
            }
            loadTable();
        } else if (e.getSource() == puttyWindow) {
            if (this.table.getSelection().length == 0) {
                MessageDialog.openInformation(dialog, "Warning", "Please select one record!");
                return;
            }
            openPutty();
        } else if (e.getSource() == connectButton) {
            if (this.table.getSelection().length == 0) {
                MessageDialog.openInformation(dialog, "Warning",
                    "Please select at least one record!");
                return;
            }
            openSelectedSessions();

        }
    }

    @Override
    public void mouseDoubleClick(MouseEvent e) {
        if (e.getSource().equals(table)) {
            openSelectedSessions();
        }
    }

    @Override
    public void mouseDown(MouseEvent e) {
    }

    @Override
    public void mouseUp(MouseEvent e) {
    }

}
