package dialog;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import constants.ButtonImage;
import constants.ConstantValue;
import dao.DbManager;
import enums.ProtocolEnum;
import lombok.extern.slf4j.Slf4j;
import model.ConfigSession;
import ui.MainFrame;
import utils.RegistryUtils;

/**
 * 新会话对话框.
 *
 * @author lvcn
 * @version $Id: NewSessionDialog.java, v 1.0 Jul 22, 2019 3:45:25 PM lvcn Exp $
 */
@Slf4j
public class NewSessionDialog implements SelectionListener, MouseListener {

    private OpenSessionDialog sessionDialog = null;
    private MainFrame         mainFrame;
    private Shell             dialog;
    private Combo             comboHost, comboIntranet, comboUser, comboProtocol, comboSession;
    private Text              textPassword, textKey, textName, textPort;
    private Button            buttonFile, buttonOk, buttonCancel, buttonShow;

    public NewSessionDialog(MainFrame mainFrame, OpenSessionDialog sessionDialog, String type) {
        this.mainFrame = mainFrame;
        this.sessionDialog = sessionDialog;
        dialog = new Shell(MainFrame.SHELL, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialog.setImage(ButtonImage.NEW_IMAGE);
        dialog.setSize(400, 160);
        dialog.setText("New Session Dialog");
        MainFrame.dbm = DbManager.getDbManagerInstance();
        Rectangle rect = dialog.getBounds();
        int x = rect.width;
        int y = rect.height;
        List<ConfigSession> sessions = MainFrame.dbm.getAllSessions();

        int index = 0;
        Label label = new Label(dialog, SWT.NONE);
        label.setText("Name");
        label.setBounds(0, index * y / 6, x / 3, y / 6);
        textName = new Text(dialog, SWT.None);
        textName.setBounds(x / 3, index * y / 6, 2 * x / 3, y / 6);

        index++;
        label = new Label(dialog, SWT.NONE);
        label.setText("Host");
        label.setBounds(0, index * y / 6, x / 3, y / 6);
        comboHost = new Combo(dialog, SWT.None);
        HashSet<String> hs = new HashSet<String>();
        for (ConfigSession item : sessions) {
            hs.add(item.getHost());
        }
        for (String item : hs) {
            comboHost.add(item);
        }
        comboHost.setBounds(x / 3, index * y / 6, 2 * x / 3, y / 6);
        comboHost.addSelectionListener(this);

        index++;
        label = new Label(dialog, SWT.NONE);
        label.setText("Intranet");
        label.setBounds(0, index * y / 6, x / 3, y / 6);
        comboIntranet = new Combo(dialog, SWT.None);
        HashSet<String> intranetSet = new HashSet<String>();
        for (ConfigSession item : sessions) {
            intranetSet.add(item.getHost());
        }
        for (String item : intranetSet) {
            comboIntranet.add(item);
        }
        comboIntranet.setBounds(x / 3, index * y / 6, 2 * x / 3, y / 6);
        comboIntranet.addSelectionListener(this);

        index++;
        label = new Label(dialog, SWT.NONE);
        label.setText("User");
        label.setBounds(0, index * y / 6, x / 3, y / 6);
        comboUser = new Combo(dialog, SWT.None);
        comboUser.setBounds(x / 3, index * y / 6, 2 * x / 3, y / 6);
        comboUser.addSelectionListener(this);

        index++;
        label = new Label(dialog, SWT.NONE);
        label.setText("Protocol");
        label.setBounds(0, index * y / 6, x / 3, y / 6);
        comboProtocol = new Combo(dialog, SWT.READ_ONLY);
        // comboProtocol.setItems(new String[] { "ssh" });
        // Get all protocols and add:
        for (ProtocolEnum protocol : ProtocolEnum.values()) {
            comboProtocol.add(protocol.getName());
        }
        comboProtocol.select(0);
        comboProtocol.setBounds(x / 3, index * y / 6, 2 * x / 3, y / 6);
        comboProtocol.setText(ConstantValue.DEFAULT_PROTOCOL);
        comboProtocol.addSelectionListener(this);

        index++;
        label = new Label(dialog, SWT.NONE);
        label.setText("Port");
        label.setBounds(0, index * y / 6, x / 3, y / 6);
        textPort = new Text(dialog, SWT.BORDER);
        textPort.setBounds(x / 3, index * y / 6, 3 * x / 6, y / 6);
        textPort.setText("22");

        index++;
        label = new Label(dialog, SWT.NONE);
        label.setText("SSH Key");
        label.setBounds(0, index * y / 6, x / 3, y / 6);
        textKey = new Text(dialog, SWT.BORDER);
        textKey.setBounds(x / 3, index * y / 6, 3 * x / 6, y / 6);
        buttonFile = new Button(dialog, SWT.PUSH);
        buttonFile.setBounds(5 * x / 6, index * y / 6, x / 6, y / 6);
        buttonFile.setText("browse");
        buttonFile.addMouseListener(this);

        index++;
        label = new Label(dialog, SWT.NONE);
        label.setText("Password");
        label.setBounds(0, index * y / 6, x / 3, y / 6);
        textPassword = new Text(dialog, SWT.PASSWORD | SWT.BORDER);
        textPassword.setBounds(x / 3, index * y / 6, 3 * x / 6, y / 6);

        buttonShow = new Button(dialog, SWT.PUSH);
        buttonShow.setBounds(5 * x / 6, index * y / 6, x / 6, y / 6);
        buttonShow.setText("show");
        buttonShow.addMouseListener(this);

        index++;
        label = new Label(dialog, SWT.NONE);
        label.setText("Session");
        label.setBounds(0, index * y / 6, x / 3, y / 6);
        comboSession = new Combo(dialog, SWT.READ_ONLY);
        comboSession.setLayoutData(new RowData());
        comboSession.setToolTipText("Session to use");
        comboSession.setBounds(x / 3, index * y / 6, 2 * x / 3, y / 6);
        // Empty entry to use none.
        comboSession.add("");
        // Get all "Putty" sessions:
        List<String> puttySessions = RegistryUtils.getAllPuttySessions();
        for (String session : puttySessions) {
            comboSession.add(session);
        }

        index++;
        buttonOk = new Button(dialog, SWT.PUSH);
        buttonOk.setText("ok");
        buttonOk.setBounds(2 * x / 3, index * y / 6, 50, y / 6);
        buttonOk.addMouseListener(this);

        buttonCancel = new Button(dialog, SWT.PUSH);
        buttonCancel.setText("cancel");
        buttonCancel.setBounds(2 * x / 3 + 60, index * y / 6, 50, y / 6);
        buttonCancel.addMouseListener(this);

        if (Objects.equals(type, "edit")) {
            ConfigSession session = sessionDialog.getCurrentSelectSession();
            if (session != null) {
                textName.setText(session.getName());
                textPort.setText(session.getPort());
                comboHost.setText(session.getHost());
                comboIntranet.setText(session.getIntranet());
                comboUser.setText(session.getUser());
                comboProtocol.setText(session.getProtocol().getName());
                textKey.setText(session.getKey());
                textPassword.setText(session.getPassword());

                if (ProtocolEnum.SSH2 == session.getProtocol()
                        || ProtocolEnum.SSH == session.getProtocol()) {
                    String profile = session.getSession();
                    if (StringUtils.isNotBlank(profile)) {
                        comboSession.setText(profile);
                        comboSession.select(ArrayUtils.indexOf(comboSession.getItems(), profile));
                    }
                } else {
                    comboSession.setEnabled(false);
                }
            }
        }

        dialog.pack();
        dialog.open();

    }

    @Override
    public void widgetDefaultSelected(SelectionEvent arg0) {
    }

    @Override
    public void widgetSelected(SelectionEvent e) {

        if (e.getSource() == comboHost) {
            String host = comboHost.getText();
            if (StringUtils.isNotBlank(host)) {
                comboUser.removeAll();
                List<ConfigSession> sessions = MainFrame.dbm.querySessionByHost(host);
                for (ConfigSession item : sessions) {
                    comboUser.add(item.getUser());
                }
                if (sessions.size() == 1) {
                    comboUser.setText(sessions.get(0).getUser());
                    comboProtocol.setText(sessions.get(0).getProtocol().getName());
                    textPassword.setText(sessions.get(0).getPassword());
                } else {
                    comboUser.setText("");
                    comboProtocol.setText(ConstantValue.DEFAULT_PROTOCOL);
                    textPassword.setText("");
                }

            }

        } else if (e.getSource() == comboUser) {
            String host = comboHost.getText();
            String user = comboUser.getText();
            if (StringUtils.isNotBlank(host) && StringUtils.isNotBlank(user)) {
                List<ConfigSession> sessions = MainFrame.dbm.querySessionByHostUser(host, user);
                if (sessions.size() == 1) {
                    comboProtocol.setText(sessions.get(0).getProtocol().getName());
                    textPassword.setText(sessions.get(0).getPassword());
                } else {
                    comboProtocol.setText(ConstantValue.DEFAULT_PROTOCOL);
                    textPassword.setText("");
                }
            }
        } else if (e.getSource() == comboProtocol) {
            String host = comboHost.getText();
            String user = comboUser.getText();
            String protocol = comboProtocol.getText();
            ConfigSession session = MainFrame.dbm.querySessionByHostUserProtocol(host, user,
                protocol);
            if (session != null) {
                textPassword.setText(session.getPassword());
            } else {
                textPassword.setText("");
            }
        }

    }

    @Override
    public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent arg0) {
    }

    @Override
    public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
        if (e.getSource() == buttonOk) {
            String name = textName.getText();
            String host = comboHost.getText();
            String intranet = comboIntranet.getText();
            String user = comboUser.getText();
            String password = textPassword.getText();
            String file = textKey.getText().trim();
            String port = StringUtils.defaultIfBlank(textPort.getText(), "22");
            ProtocolEnum protocol = ProtocolEnum.values()[comboProtocol.getSelectionIndex()];

            String sessionProfile = "";
            if (ProtocolEnum.SSH2 == protocol || ProtocolEnum.SSH == protocol) {
                int index = comboSession.getSelectionIndex();
                if (index >= 0) {
                    sessionProfile = comboSession.getItem(index);
                    log.info("当前选择会话配置:{}", sessionProfile);
                }
            }

            if (StringUtils.isNotBlank(host) && StringUtils.isNotBlank(user) && protocol != null) {
                ConfigSession session = new ConfigSession(name, host, intranet, port, user,
                    protocol, file, password, sessionProfile);
                dialog.dispose();
                MainFrame.dbm.insertSession(session);
                if (sessionDialog != null) {
                    sessionDialog.loadTable();
                }
                if (mainFrame != null) {
                    mainFrame.addSession(null, session);
                }
            } else {
                MessageDialog.openInformation(dialog, "Warning",
                    "Must set Host, User and Protocol");
            }

        } else if (e.getSource() == buttonCancel) {
            dialog.dispose();
        } else if (e.getSource() == buttonFile) {
            FileDialog fileDlg = new FileDialog(dialog, SWT.OPEN);
            fileDlg.setText("Select SSH key");
            String filePath = fileDlg.open();
            if (null != filePath) {
                textKey.setText(filePath);
            }

        } else if (e.getSource() == buttonShow) {
            // 显示密码功能
            String text = buttonShow.getText();
            if (StringUtils.equals(text, "show")) {
                textPassword.setEchoChar((char) 0);
                buttonShow.setText("hide");
            } else {
                textPassword.setEchoChar('●');
                buttonShow.setText("show");
            }
        }

    }

    @Override
    public void mouseUp(org.eclipse.swt.events.MouseEvent arg0) {
    }

}
