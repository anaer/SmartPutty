package dialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import constants.ButtonImage;
import enums.ProgramEnum;
import lombok.extern.slf4j.Slf4j;
import ui.MainFrame;

/**
 * To define which locations has programs to use. <br>
 * 应用程序路径设置.
 *
 * @author Carlos SS
 */
@Slf4j
public class ProgramsLocationsDialog implements SelectionListener, MouseListener {
    final private Shell         dialog;
    /**
     * 保存按钮.
     */
    private Button              saveButton;
    /**
     * 取消按钮.
     */
    private Button              cancelButton;

    /**
     * 配置变更缓存.
     */
    private Map<String, String> tmpPropConfig;

    /**
     *  Constructor:
     * @param parent
     */
    public ProgramsLocationsDialog(Shell parent) {
        this.dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

        init();
    }

    /**
     * Initialize window in a safer way. Useful to avoid "Leaking This In Constructor" warnings.
     */
    private void init() {
        // 临时修改的配置项
        tmpPropConfig = new HashMap<>(16);

        // Setup a layout:
        GridLayout layout = new GridLayout(3, false);

        dialog.setImage(ButtonImage.OPEN_IMAGE);
        dialog.setText("Configure programs locations");
        dialog.setLayout(layout);

        // Initial help text:
        GridData gd00 = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd00.horizontalSpan = 3;
        Label textLabel = new Label(dialog, SWT.LEFT);
        textLabel.setText(
            "Use this window to define which programs to use: \"SmartPutty\" included programs or yours.");
        textLabel.setLayoutData(gd00);

        // Blank space:
        GridData gd01 = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd01.horizontalSpan = 3;
        Label empty01Label = new Label(dialog, SWT.NONE);
        empty01Label.setLayoutData(gd01);

        addItem(ProgramEnum.PUTTY);
        addItem(ProgramEnum.PLINK);
        addItem(ProgramEnum.KEYGEN);
        addItem(ProgramEnum.MINTTY);
        addItem(ProgramEnum.WINSCP);

        // Blank space:
        GridData gd98 = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd98.horizontalSpan = 3;
        Label empty98Label = new Label(dialog, SWT.NONE);
        empty98Label.setLayoutData(gd98);

        // Main buttons:
        GridData gd99 = new GridData(SWT.RIGHT, SWT.RIGHT, true, true);
        gd99.widthHint = 60;
        gd99.heightHint = 30;

        // Unused phantom item:
        Label empty2 = new Label(dialog, SWT.NONE);
        empty2.setLayoutData(gd99);

        saveButton = new Button(dialog, SWT.CENTER);
        saveButton.setText("Save");
        saveButton.setToolTipText("Save changes");
        saveButton.addSelectionListener(this);
        saveButton.setLayoutData(gd99);

        cancelButton = new Button(dialog, SWT.CENTER);
        cancelButton.setText("Cancel");
        cancelButton.setToolTipText("Cancel changes");
        cancelButton.addSelectionListener(this);
        cancelButton.setLayoutData(gd99);

        dialog.pack();
        dialog.open();
    }

    /**
     * 添加程序路径设置条目.
     *
     * @param program 应用程序
     */
    private void addItem(ProgramEnum program) {
        GridData gd1 = new GridData(SWT.FILL, SWT.CENTER, true, true);
        gd1.widthHint = 100;
        Label label = new Label(dialog, SWT.RIGHT);
        label.setText(program.getName());
        label.setLayoutData(gd1);

        GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd2.widthHint = 300;
        Text editText = new Text(dialog, SWT.BORDER);

        // Get current value.
        String path = MainFrame.configuration.getProgramProperty(program.getProperty(), program.getPath());
        editText.setText(path);
        editText.setLayoutData(gd2);

        // 编辑框内容修改时, 添加到临时变量配置中
        editText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (StringUtils.isNotBlank(program.getProperty())) {
                    tmpPropConfig.put(program.getProperty(), editText.getText());
                }
            }

        });

        GridData gd3 = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd3.widthHint = 50;
        gd3.heightHint = 20;
        Button button = new Button(dialog, SWT.CENTER);
        button.setText("Browse");
        button.setToolTipText("Search for " + program.getName() + " executable");
        button.setLayoutData(gd3);
        button.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String path = searchProgramDialog(program);
                if (StringUtils.isNotBlank(path)) {
                    editText.setText(path);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });
    }

    private String searchProgramDialog(ProgramEnum program) {
        FileDialog fileDialog = new FileDialog(dialog, SWT.OPEN);
        String[] filterExtensions = program.getFilterExtensions();
        String[] filterNames = program.getFilterNames();

        // 如果过滤不为空 设置过滤.
        if (ArrayUtils.isNotEmpty(filterExtensions) && ArrayUtils.isNotEmpty(filterNames)) {
            fileDialog.setFilterExtensions(filterExtensions);
            fileDialog.setFilterNames(filterNames);
        }

        String path = fileDialog.open();
        return path;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        if (e.getSource() == saveButton) {
            log.info("保存设置项.");
            // Save changes to configuration:
            if (!tmpPropConfig.isEmpty()) {
                for (Entry<String, String> entry : tmpPropConfig.entrySet()) {
                    MainFrame.configuration.setProgramProperty(entry.getKey(), entry.getValue());
                }
                MainFrame.configuration.saveSetting();
                // 设置完成后, 清空临时配置项
                tmpPropConfig.clear();
            }

            dialog.dispose();
        } else if (e.getSource() == cancelButton) {
            dialog.dispose();
        }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }

    @Override
    public void mouseDoubleClick(MouseEvent e) {
    }

    @Override
    public void mouseDown(MouseEvent e) {
    }

    @Override
    public void mouseUp(MouseEvent e) {
    }
}
