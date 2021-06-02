package listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * tab拖动Listener.
 */
public class DragListener implements Listener {

    private CTabFolder folder;
    private Display display;

    boolean drag = false;
    boolean exitDrag = false;
    CTabItem dragItem;
    Cursor cursorSizeAll = new Cursor(null, SWT.CURSOR_SIZEALL);
    Cursor cursorNo = new Cursor(null, SWT.CURSOR_NO);
    Cursor cursorArrow = new Cursor(null, SWT.CURSOR_ARROW);

    /**
    * @param folder
    * @param display
    */
    public DragListener(Display display, CTabFolder folder) {
        this.display = display;
        this.folder = folder;
    }

    @Override
    public void handleEvent(Event e) {
        Point p = new Point(e.x, e.y);
        if (e.type == SWT.DragDetect) {
            // see eclipse bug 43251
            p = folder.toControl(display.getCursorLocation());
        }
        switch (e.type) {

        case SWT.DragDetect: {
            CTabItem item = folder.getItem(p);
            if (item == null) {
                return;
            }

            drag = true;
            exitDrag = false;
            dragItem = item;

            folder.getShell().setCursor(cursorNo);
            break;
        }

        case SWT.MouseEnter:
            if (exitDrag) {
                exitDrag = false;
                drag = e.button != 0;
            }
            break;

        case SWT.MouseExit:
            if (drag) {
                folder.setInsertMark(null, false);
                exitDrag = true;
                drag = false;

                folder.getShell().setCursor(cursorArrow);
            }
            break;

        case SWT.MouseUp: {
            if (!drag) {
                return;
            }
            folder.setInsertMark(null, false);
            CTabItem item = folder.getItem(new Point(p.x, 1));

            if (item != null) {
                int index = folder.indexOf(item);
                int newIndex = folder.indexOf(item);
                int oldIndex = folder.indexOf(dragItem);
                if (newIndex != oldIndex) {
                    boolean after = newIndex > oldIndex;
                    index = after ? index + 1 : index;
                    index = Math.max(0, index);

                    CTabItem cloneItem = new CTabItem(folder, SWT.CLOSE, index);
                    cloneItem.setText(dragItem.getText());
                    cloneItem.setImage(dragItem.getImage());
                    cloneItem.setData("hwnd", dragItem.getData("hwnd"));
                    cloneItem.setData("session", dragItem.getData("session"));

                    Control c = dragItem.getControl();
                    dragItem.setControl(null);
                    cloneItem.setControl(c);
                    dragItem.dispose();
                    folder.setSelection(cloneItem);
                }
            }
            drag = false;
            exitDrag = false;
            dragItem = null;

            folder.getShell().setCursor(cursorArrow);
            break;
        }

        case SWT.MouseMove: {
            if (!drag) {
                return;
            }
            CTabItem item = folder.getItem(new Point(p.x, 2));
            if (item == null) {
                folder.setInsertMark(null, false);
                return;
            }
            Rectangle rect = item.getBounds();
            boolean after = p.x > rect.x + rect.width / 2;
            folder.setInsertMark(item, after);

            folder.getShell().setCursor(cursorSizeAll);
            break;
        }

        default:
            break;
        }
    }

}
