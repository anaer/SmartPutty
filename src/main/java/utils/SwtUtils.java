package utils;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

public class SwtUtils {

    /**
     * 设置对话框位置.
     * 设置水平居中, 垂直1/4位置
     * @param parent 父窗口
     * @param dialog 对话框
     */
    public static void setDialogLocation(Shell parent, Shell dialog) {
        Point pLocation = parent.getLocation();
        Point pSize = parent.getSize();

        Point size = dialog.getSize();

        int px = pLocation.x;
        int py = pLocation.y;
        int pw = pSize.x;
        int ph = pSize.y;

        int w = size.x;
        int h = size.y;

        int x = px + (pw - w) / 2; // 水平居中
        int y = py + (ph - h) / 4; // 垂直在1/4位置

        dialog.setLocation(x, y);
    }
}
