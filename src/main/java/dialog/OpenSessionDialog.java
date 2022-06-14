package dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import constants.FieldConstants;
import control.InvokeProgram;
import dao.SessionManager;
import lombok.extern.slf4j.Slf4j;
import model.ConfigSession;
import ui.MainFrame;

/**
 * 打开会话对话框.
 *
 * @author anaer
 * @version $Id: OpenSessionDialog.java, v 1.0 Jul 22, 2019 3:45:59 PM anaer Exp $
 */
@Slf4j
public class OpenSessionDialog implements SelectionListener, MouseListener {
    private MainFrame mainFrame = null;
    private Shell     dialog    = null;
    protected Object  result;
    private Table     table;
    private SessionManager dbm;
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

    /**
     * 上移.
     */
    private Button btnUp;

    /**
     * 下移.
     */
    private Button btnDown;

    public OpenSessionDialog(MainFrame mainFrame, Shell parent) {
        this.dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        this.mainFrame = mainFrame;
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
        tableHostColumn.setWidth(110);
        tableHostColumn.setText("Host");

        TableColumn tableIntranetColumn = new TableColumn(table, SWT.NONE);
        tableIntranetColumn.setWidth(110);
        tableIntranetColumn.setText("Intranet");

        TableColumn tablePortColumn = new TableColumn(table, SWT.NONE);
        tablePortColumn.setWidth(50);
        tablePortColumn.setText("Port");

        TableColumn tableUserColumn = new TableColumn(table, SWT.NONE);
        tableUserColumn.setWidth(50);
        tableUserColumn.setText("User");

        TableColumn tableTimeColumn = new TableColumn(table, SWT.NONE);
        tableTimeColumn.setWidth(65);
        tableTimeColumn.setText("Protocol");

        // 隐藏session列, 主要目前都没怎么用session
        // TableColumn sessionColumn = new TableColumn(table, SWT.NONE);
        // sessionColumn.setWidth(75);
        // sessionColumn.setText(FieldConstants.SESSION);

        dbm = SessionManager.getInstance();
        loadTable();

        int xPos = 565;
        // button
        addButton = new Button(dialog, SWT.NONE);
        addButton.setBounds(xPos, 5, 80, 27);
        addButton.setText("Add   ");
        addButton.setToolTipText("Add a new connection");
        addButton.addSelectionListener(this);

        editButton = new Button(dialog, SWT.NONE);
        editButton.setBounds(xPos, 37, 80, 27);
        editButton.setText("Edit ");
        editButton.setToolTipText("Edit selected connection");
        editButton.addSelectionListener(this);

        deleteButton = new Button(dialog, SWT.NONE);
        deleteButton.setBounds(xPos, 69, 80, 27);
        deleteButton.setText("Delete");
        deleteButton.setToolTipText("Delete selected connection/s");
        deleteButton.addSelectionListener(this);

        puttyWindow = new Button(dialog, SWT.NONE);
        puttyWindow.setBounds(xPos, 101, 80, 27);
        puttyWindow.setText("Putty");
        puttyWindow.setToolTipText("Open selected connection in a single window");
        puttyWindow.addSelectionListener(this);

        btnUp = new Button(dialog, SWT.NONE);
        btnUp.setBounds(xPos, 133, 80, 27);
        btnUp.setText("⬆ UP");
        btnUp.setToolTipText("");
        btnUp.addSelectionListener(this);

        btnDown = new Button(dialog, SWT.NONE);
        btnDown.setBounds(xPos, 165, 80, 27);
        btnDown.setText("⬇ DOWN");
        btnDown.setToolTipText("");
        btnDown.addSelectionListener(this);

        connectButton = new Button(dialog, SWT.NONE);
        connectButton.setBounds(xPos, 230, 80, 27);
        connectButton.setText("Connect");
        connectButton.setToolTipText("Open selected connection/s in a tab");
        connectButton.addSelectionListener(this);

        dialog.pack();
        dialog.open();

    }

    public ConfigSession getCurrentSelectSession() {
        if (table.getSelection().length > 0) {
            return (ConfigSession) (table.getSelection()[0].getData(FieldConstants.SESSION));
        } else {
            return null;
        }
    }

    /**
     * 加载表格. 默认选中第一行
     */
    public void loadTable() {
        loadTable(0);
    }

    /**
     * 加载表格.
     * @param selectionIndex 默认选中行
     */
    public void loadTable(int selectionIndex) {
        table.removeAll();
        List<ConfigSession> sessions = dbm.getAllSessions();
        for (ConfigSession session : sessions) {
            TableItem tableItem = new TableItem(table, SWT.NONE);
            tableItem.setData(FieldConstants.SESSION, session);
            tableItem
                .setText(new String[] { session.getName(), session.getHost(), session.getIntranet(),
                                        session.getPort(), session.getUser(),
                                        session.getProtocol(), session.getSession() });
        }

        if (selectionIndex >= 0 && selectionIndex < table.getItemCount()) {
            table.setSelection(selectionIndex);
        }
    }

    /**
     * Open all selected sessions in tabs.
     */
    private void openSelectedSessions() {
        TableItem[] tableItems = table.getSelection();
        ArrayList<ConfigSession> sessions = new ArrayList<>();
        for (TableItem tableItem : tableItems) {
            ConfigSession session = (ConfigSession) tableItem.getData(FieldConstants.SESSION);
            log.info("session:{}", session);
            if (session != null) {
                sessions.add(session);
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
        ConfigSession session = getCurrentSelectSession();
        if (Objects.nonNull(session)) {
            InvokeProgram.invokeSinglePutty(session);
            dialog.dispose();
        }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent arg0) {
        //
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        if (e.getSource() == addButton) {
            new NewSessionDialog(null, this, false);
        } else if (e.getSource() == editButton) {
            if (table.getSelection().length == 1) {
                new NewSessionDialog(null, this, true);
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
                ConfigSession session = (ConfigSession) item.getData(FieldConstants.SESSION);
                dbm.deleteSession(session);
            }
            loadTable();
        } else if (e.getSource() == puttyWindow) {
            if (this.table.getSelection().length == 0) {
                MessageDialog.openInformation(dialog, "Warning", "Please select one record!");
                return;
            }

            openPutty();
        } else if (e.getSource() == btnUp) {
            if (this.table.getSelection().length == 0) {
                MessageDialog.openInformation(dialog, "Warning", "Please select one record!");
                return;
            }

            ConfigSession session = getCurrentSelectSession();
            int index = dbm.up(session);

            loadTable(index);
        } else if (e.getSource() == btnDown) {
            if (this.table.getSelection().length == 0) {
                MessageDialog.openInformation(dialog, "Warning", "Please select one record!");
                return;
            }

            ConfigSession session = getCurrentSelectSession();
            int index = dbm.down(session);

            loadTable(index);
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
        //do nothing
    }

    @Override
    public void mouseUp(MouseEvent e) {
        //do nothing
    }

}
