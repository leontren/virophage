package virophage.gui;

import java.awt.Font;
import java.awt.Graphics;

/**
 * A screen which holds the credits.
 *
 * @author Leon Ren
 * @since 2014-05-6
 */
public class CreditsScreen extends TextScreen {

    /**
     * Construct this CreditsScreen.
     *
     * @param g a Gameclient
     */
    public CreditsScreen(GameClient g) {
        super(g);
    }

    /**
     * Paints this CreditsScreen.
     *
     * @param g a Graphics object
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int x = this.getWidth();
        int y = this.getHeight();
        buttonBack.setBounds(x / 10, y - y / 10, 100, 30);
        y -= 90;

        // Draw the Title
        Font f = new Font("arial", Font.BOLD, 30);
        g.setFont(f);
        g.drawString("Credits", x / 3 + x / 10, y / 4);

        //Draw the Credits lines

        f = new Font("calibri", Font.BOLD, 18);
        g.setFont(f);
        g.drawString("Created by Max Ovsiankin and Leon Ren", x / 3, y / 4 + 2 * y / 9);
        f = new Font("calibri", Font.PLAIN, 18);
        g.setFont(f);
        g.drawString("AP Computer Science - Period 1", x / 3, y / 4 + y / 15 + 2 * y / 9);
        g.drawString("5/27/2014", x / 3, y / 4 + 2 * y / 15 + 2 * y / 9);

    }

}