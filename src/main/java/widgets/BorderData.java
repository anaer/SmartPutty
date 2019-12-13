package widgets;

import org.eclipse.swt.SWT;

/**
 * @author lvcn
 * Indicates the region that a control belongs to.
 */
public final class BorderData {

    private int region = SWT.CENTER;

    public BorderData() {
    }

    public BorderData(int region) {
        this.region = region;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

}
