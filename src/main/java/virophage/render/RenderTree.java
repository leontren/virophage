package virophage.render;

import virophage.core.Cell;
import virophage.core.Channel;
import virophage.core.DeadCell;
import virophage.core.MachinePlayer;
import virophage.core.Player;
import virophage.core.Virus;
import virophage.core.Tissue;
import virophage.gui.GameClient;
import virophage.gui.Selection;
import virophage.util.HexagonConstants;
import virophage.util.Location;
import virophage.util.Vector;

import java.util.Timer;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferStrategy;

/**
 * A <code>RenderTree</code> contains an array of renderNodes, it is also a GUI componet.
 * @author      Max Ovsiankin and Leon Ren
 * @version     1.0 (Alpha)
 * @since       2014-05-6
 */
public class RenderTree extends Canvas implements Runnable {

    public double zoom = 1;
    public Vector displacement = new Vector(0, 0);
    private Tissue tissue;
    private GameClient w;
    
    public static Timer timer = new Timer();
    public static Selection selection = new Selection(null);

    /**
     * Constructs a RenderTree and adds the listeners.
     */
    public RenderTree(GameClient g) {
    	w = g;
        setIgnoreRepaint(true);
        setFocusable(true);
        requestFocus();

        TreeListener listener = new TreeListener(this);


        addKeyListener(listener);

        addMouseListener(listener);
        addMouseMotionListener(listener);
        addMouseWheelListener(listener);
    }

    public GameClient getGameClient() {
    	return w; 	
    }
    
    public void setTissue(Tissue t) {
    	tissue = t;
    }
    
    public Tissue getTissue() {
    	return tissue;
    }
    
    /**
     * Draws a channel between two cells
     * @param channel
     * @param g
     */
    public void drawChannel(Channel channel, Graphics2D g) {
    	 // loop caluclations
        int rad = (int) (HexagonConstants.RADIUS * 2 * 0.80);
        int pos = rad / -2;
        Vector endpoint = new Vector(HexagonConstants.TRI_HEIGHT * 2, 0)
                .rotate(channel.from.getDirectionTowards(channel.to));
        double mult = (rad / 2 + 5) / endpoint.magnitude();
        Vector startpoint = endpoint.scale(mult);

        // draw line with masking circle at end
        g.setColor(channel.player.getColor().darker().darker());
        g.setStroke(new BasicStroke(10));
        g.drawLine((int) startpoint.x, (int) startpoint.y, (int) endpoint.x, (int) endpoint.y);
        g.fillOval((int) endpoint.x - 7, (int) endpoint.y - 7, 14, 14);

        // draw loop
        g.setColor(channel.player.getColor().darker().darker().darker().darker());
        g.setStroke(new BasicStroke(3));
        g.drawOval(pos, pos, rad, rad);

        // draw endpoint
        g.setColor(channel.player.getColor().darker().darker());
        g.setStroke(new BasicStroke());
        if(channel.hasVirus()) {
            Virus v = channel.virus;
            double circleRadius = (v.getEnergy() / (double) GameClient.MAX_ENERGY) *
                    (HexagonConstants.RADIUS * 0.7);
            int circleDiameter = (int) (circleRadius * 2);
            int x = (int) (endpoint.x - circleRadius);
            int y = (int) (endpoint.y - circleRadius);
            g.fillOval(x, y, circleDiameter, circleDiameter);
        }
    }
    
    public void drawCell(Location loc, Cell cell, Graphics2D g) {
    	Polygon hexagon = new Polygon(new int[]{
                (int) (HexagonConstants.RADIUS / 2),
                (int) (HexagonConstants.RADIUS * 3 / 2),
                (int) (HexagonConstants.RADIUS * 2),
                (int) (HexagonConstants.RADIUS * 3 / 2),
                (int) (HexagonConstants.RADIUS / 2),
                0
        }, new int[]{
                0,
                0,
                (int) HexagonConstants.TRI_HEIGHT,
                (int) (HexagonConstants.TRI_HEIGHT * 2),
                (int) (HexagonConstants.TRI_HEIGHT * 2),
                (int) HexagonConstants.TRI_HEIGHT
        }, 6);
    	
    	g.setFont(new Font("SansSerif", Font.BOLD, 32));
        //FontMetrics fm = g.getFontMetrics(g.getFont());
        Virus occupant = cell.getOccupant();
        if (occupant != null) {
            Color light = occupant.getPlayer().getColor();
            Color dark = light.darker();
            if(cell.equals(selection.getFrom())) {
            	dark = Color.GRAY;
            }
            g.setColor(light);
            g.fillPolygon(hexagon);
            g.setColor(dark);
            double circleRadius = (occupant.getEnergy() / (double) GameClient.MAX_ENERGY) *
                    (HexagonConstants.RADIUS * 0.7);
            int circleDiameter = (int) (circleRadius * 2);
            int x = (int) (HexagonConstants.RADIUS - circleRadius);
            int y = (int) (HexagonConstants.TRI_HEIGHT - circleRadius);
            Ellipse2D ellipse = new Ellipse2D.Double(x, y, circleDiameter, circleDiameter);
            g.fill(ellipse);
        } else {
            if (cell instanceof DeadCell) {
                g.setColor(Color.BLACK);
            } else {
            	if(cell.equals(selection.getFrom())) {
                	g.setColor(Color.GRAY);
                } else {
                	g.setColor(Color.WHITE);
                }
            }
            g.fillPolygon(hexagon);
        }

        //g.setStroke(new BasicStroke(4));
        g.setColor(new Color(32, 32, 32));
        g.drawPolygon(hexagon);
        g.setColor(Color.BLACK);
        //int x = (int) (HexagonConstants.RADIUS - fm.stringWidth(loc.toString()) / 2);
        //int y = (int) (HexagonConstants.TRI_HEIGHT);
        //g.drawString(loc.toString(), x, y);
        if (cell != null && cell.getOccupant() != null) {
        	Virus tempV = cell.getOccupant();
        	String s;
        	if (tempV.getPlayer() instanceof MachinePlayer) {
        		s = "M";
        	}
        	else {
        		s = "S";
        	}
        	g.drawString(s, 100, 100);
        }
    }
    
    private Graphics2D createGraphics(Graphics g, Vector transform) {
    	Point point = transform.toPoint();
		Graphics2D gNew = (Graphics2D) g.create();
		gNew.translate(point.x, point.y);
		return gNew;
    }

    public void render(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;
        // makes the game look really nice, but also really slow
        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(new Color(230, 230, 230));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        AffineTransform at = new AffineTransform();
        at.scale(zoom, zoom);
        at.translate(displacement.x, displacement.y);
        g.setTransform(at);

        for(int x = -GameClient.N; x <= GameClient.N; x++) {
        	for(int y = -GameClient.N; y <= GameClient.N; y++) {
        		for(int z = -GameClient.N; z <= GameClient.N; z++) {
        			if(x + y + z == 0) {
        				Location loc = new Location(x, y);
        				drawCell(loc, tissue.getCell(loc), createGraphics(g, loc.asCoordinates()));
        			}
        		}
        	}
        }
        
        for(Channel c: tissue.getChannels()) {
        	drawChannel(c, createGraphics(g, c.from.asCoordinates()));
        }
        
        int x = this.getWidth();
        int y = this.getHeight();
        
        Font f = new Font("arial", Font.BOLD, 20);
        g.setFont(f);
        Player[] p = tissue.getPlayers();
        g.setTransform(new AffineTransform());
        g.setColor(Color.GRAY);
        g.fillRect(0, y - 40, x, y);
        g.setColor(Color.GREEN);
        g.drawString("Green: #cells - " + p[0].getViruses().size() + "", 10, y - 10);
        g.setColor(Color.RED);
        int virusSize = p[1].getViruses().size();
        String vSize = virusSize + "";
        g.drawString("Red: #cells - " + virusSize + "", x - 128 - 15 * vSize.length(), y - 10);
        
        
    }

    @Override
    public void run() {
        this.createBufferStrategy(2);
        BufferStrategy strategy = this.getBufferStrategy();
        while(true) {
            do {
                do {
                    Graphics gr = strategy.getDrawGraphics();
                    render(gr);
                    gr.dispose();
                } while (strategy.contentsRestored());

                strategy.show();
            } while (strategy.contentsLost());
        }
    }
}
