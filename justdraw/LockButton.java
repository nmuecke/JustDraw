package justdraw;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.URL;

class LockButton extends JToggleButton
{
//    static protected Font font;
    static protected ImageIcon unlockedIcon, lockedIcon;

    LockButton()
    {
	super();
//	setHorizontalTextPosition(AbstractButton.CENTER);
	setFocusPainted(false);
	setBorderPainted(false);
        setContentAreaFilled(false);
//	if (font == null)
//	{
//	    font = new Font("serif", Font.BOLD, 24);
// 	}
//	setFont(font);
	URL unlockedURL = getClass().getClassLoader().getResource("justdraw/resources/unlocked.gif");
	URL lockedURL = getClass().getClassLoader().getResource("justdraw/resources/locked.gif");
	unlockedIcon = new ImageIcon(unlockedURL);
	lockedIcon = new ImageIcon(lockedURL);

	setSelectedIcon(lockedIcon);

	setIcon(unlockedIcon);
    }
}