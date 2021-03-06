package com.IB.SL;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.IB.SL.entity.Entity;
import com.IB.SL.entity.mob.Player;
import com.IB.SL.entity.mob.PlayerMP;
import com.IB.SL.graphics.Screen;
import com.IB.SL.graphics.font;
import com.IB.SL.graphics.font8x8;
import com.IB.SL.graphics.UI.GUI;
import com.IB.SL.input.Keyboard;
import com.IB.SL.input.Mouse;
import com.IB.SL.level.Level;
import com.IB.SL.level.TileCoord;
import com.IB.SL.level.tile.Tile;
import com.IB.SL.level.worlds.Maps;
import com.IB.SL.level.worlds.SpawnHaven;
import com.IB.SL.util.LoadProperties;
import com.IB.SL.util.SaveGame;
import com.IB.SL.util.Sound;

@SuppressWarnings("static-access")

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;	
	
	public Tile tile;
	public GUI gui;
	private Thread thread;
	public JFrame frame; 
	public Keyboard key;
	public transient font font;
	
	public static int width = 300; // 300 //520
	public static int height = 168; // 168 //335
	public static int scale = 4;
	public static final int TILE_BIT_SHIFT = 4;

	public static String title = "";
	public double xScroll, yScroll;
	
	public static Level level;
	public static Level level2;
	private Player player;
	public int frames = 0;
	public static int mouseMotionTime = 0;
	//private boolean invokedLoad = false;

	public boolean autoSave = true;

	private boolean running = false;
	public transient font8x8 font8x8;
	public static int currentLevelId;
	public static boolean showAVG;
	public static boolean recAVG_FPS = false;
	
	public int ClickTime = 0;
	public int time2 = 0;
	public boolean atMenu = true;
	public static boolean devModeOn = false;
	private boolean devModeReleased = true;
	public LoadProperties loadProp;
	public static boolean devModeInfoOn = false;
	public TileCoord playerSpawn;
	public TileCoord playerRespawn = new TileCoord(52, 72);
	public static String PlayerName = "Player";
	File screenshots = null;
	public static boolean runTut = false;

	int saveTime = 0;
	/**
	 * 0 = stop; 1 = menu; 2 = [m]Protocol: (in-game); 3 = [a]Protocol:
	 * (in-game); 4 = pause; 5 = modded/tampered; 6 = dead; 7 = Splash;
	 */
	
	public HashMap<String, Boolean> properties = new HashMap<String, Boolean>();

	private boolean releasedDevInfo = true;
		
	private Screen screen;
	public WindowHandler windowHandler;
	private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

	@SuppressWarnings("unused")
	private int time = 0;

	private void folderCreation() {
		File SavesFolder = new File(LoadProperties.basePath + "/Saves");

		if (SavesFolder.exists()) {
			System.out.println("File: " + SavesFolder
					+ " exists, not creating a new directory");
		}
		if (!SavesFolder.exists()) {
			System.out.println("Creating Directory: " + SavesFolder);

			boolean result = SavesFolder.mkdir();
			if (result) {
				System.out.println(SavesFolder + " Created Successfully");
			}

		}

		if (screenshots.exists()) {
			System.out.println("File: " + screenshots
					+ " exists, not creating a new directory");
		}
		if (!screenshots.exists()) {
			System.out.println("Creating Directory: " + screenshots.getAbsolutePath());

			boolean result = screenshots.mkdir();
			if (result) {
				System.out.println(screenshots.getAbsolutePath() + " Created Successfully");
			}
		}
	}

	/*
	 * void WriteVersion() throws IOException { PrintWriter writer; try { writer
	 * = new PrintWriter( Game.basePath + "/Saves/version.dat", "UTF-8");
	 * writer.println(version); writer.close(); } catch (FileNotFoundException |
	 * UnsupportedEncodingException e) { e.printStackTrace(); } }
	 */

	/*
	 * private void VersionFile() throws IOException { File xyFile = new
	 * File(basePath + "/Saves/" + "version.dat"); if (!xyFile.exists()) {
	 * xyFile.createNewFile(); try(PrintWriter out = new PrintWriter(new
	 * BufferedWriter(new FileWriter(xyFile, false)))) { out.println(0); }catch
	 * (IOException e) { } } DataInputStream xyStream = new DataInputStream(new
	 * FileInputStream(xyFile)); String xyStored = xyStream.readLine();
	 * System.out.println(xyStored); Double.parseDouble(xyStored); /* if
	 * (xyStored.contains("/")) { String xStored = xyStored.substring(0
	 * ,xyStored.indexOf("/")); //System.out.println(xStored); String yStored =
	 * xyStored.replace(xStored + "/", ""); //System.out.println(yStored);
	 * 
	 * int xStoredInt = Integer.parseInt(xStored); int yStoredInt =
	 * Integer.parseInt(yStored);
	 * 
	 * //System.out.println(xStoredInt / 16 + " / " + yStoredInt / 16);
	 * //System.out.println(players.get(i).getX() + " / " +
	 * players.get(i).getY()); /*players.get(i).setX(xStoredInt);
	 * players.get(i).setY(yStoredInt); } }
	 */

/*	public void readVersion() {
		try {
			URL url = new URL(
					"jar:file:/" + loadProp.basePath + "!/version.txt");
			InputStream is = url.openStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			System.out.println(reader.readLine());
			version = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	
	
    private static final char PKG_SEPARATOR = '.';

    private static final char DIR_SEPARATOR = '/';

    private static final String CLASS_FILE_SUFFIX = ".class";

    private static final String BAD_PACKAGE_ERROR = "Unable to get resources from path '%s'. Are you sure the package '%s' exists?";

    public static List<Class<?>> find(String scannedPackage) {
        String scannedPath = scannedPackage.replace(PKG_SEPARATOR, DIR_SEPARATOR);
        URL scannedUrl = Thread.currentThread().getContextClassLoader().getResource(scannedPath);
        if (scannedUrl == null) {
            throw new IllegalArgumentException(String.format(BAD_PACKAGE_ERROR, scannedPath, scannedPackage));
        }
        File scannedDir = new File(scannedUrl.getFile());
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (File file : scannedDir.listFiles()) {
            classes.addAll(find(file, scannedPackage));
        }
        return classes;
    }
    

    private static List<Class<?>> find(File file, String scannedPackage) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        String resource = scannedPackage + PKG_SEPARATOR + file.getName();
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                classes.addAll(find(child, resource));
            }
        } else if (resource.endsWith(CLASS_FILE_SUFFIX)) {
            int endIndex = resource.length() - CLASS_FILE_SUFFIX.length();
            String className = resource.substring(0, endIndex);
            try {
                classes.add(Class.forName(className));
            } catch (ClassNotFoundException ignore) {
            }
        }
        return classes;
    }
	
    ArrayList<String> materials = new ArrayList<String>();
	public Game(String title) {
		this.title = title;
		//load();
		/*List<Class<?>> classes = find("com.IB.SL.entity.inventory.item.material");
		for(int i = 0; i < classes.size(); i++) {
			String path = classes.get(i).getName();
			String name = path.substring(path.indexOf("material.") + "material.".length(), path.length());
			materials.add(name);
		}*/
		loadProp = new LoadProperties();
		loadProp.createDataFolder();
		screenshots = new File(LoadProperties.basePath + "/screenshots");
		Sound.loadOggs();
		//readVersion();
		/*
		 * try { WriteVersion(); } catch (IOException e2) {
		 * e2.printStackTrace(); }
		 * 
		 * try { VersionFile(); } catch (IOException e1) { e1.printStackTrace();
		 * }
		 */
		folderCreation();

		/**
		 * C:\ProgramData\SquareLegacy
		 */
		
	
		setGui(new GUI());
		Dimension size = new Dimension(width * scale, height * scale);
		setPreferredSize(size);
		screen = new Screen(width, height);
		frame = new JFrame();
		windowHandler = new WindowHandler(this);
		key = new Keyboard();

			setLevel(new SpawnHaven(Maps.SpawnHaven));
			playerSpawn = new TileCoord(52, 72);
			
			level2 = level;
		
		// TileCoord playerSpawn = new TileCoord(296, 381);
		setPlayer(new PlayerMP(playerSpawn.x(), playerSpawn.y(), key, this.PlayerName, Entity.genUUID(), null, -1));
		level.add(getPlayer());
		addKeyListener(key);
		Mouse mouse = new Mouse();
		font = new font();
		font8x8 = new com.IB.SL.graphics.font8x8();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		addMouseWheelListener(mouse);
	}

	public void setLevel(Level level) {
		this.level = level;
	}
	
	static Level currentLevel() {
		return level2;
	}
	
	public void captureScreen(JFrame currentFrame, String fileName) throws AWTException {
		List<PlayerMP> players = level.players;
		System.out.println("Saved Screenshot as: " + fileName + "_" + System.currentTimeMillis() + ".png");
		Robot robot = new Robot();
		Rectangle capRectange = currentFrame.getBounds();
		BufferedImage exportImage = robot.createScreenCapture(capRectange);
		try {
			ImageIO.write(exportImage, "png", 
					new File(fileName + "_" + System.currentTimeMillis()
							+ ".png"));
		} catch (IOException e) {

			System.out.println(e);

		}

	}

	public static int getWindowWidth() {
		return width * scale;
	}

	public static int getWindowHeight() {
		return height * scale;
	}

	public synchronized void start() {
		running = true;
		thread = new Thread(this, "Game");
		thread.start();
		
	}

	public synchronized void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
		
	/*inet = InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 });
    System.out.println("Sending Ping Request to " + inet);
    System.out.println(inet.isReachable(5000) ? "Host is reachable" : "Host is NOT reachable");*/
	
	
	public void run() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / 60.0;
		double delta = 0;
		frames = 0;
		int updates = 0;
		requestFocus();
		
		
		
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {

				// speedModif++;

					 //if (speedModif % 1 == 0) {
					update();
					 //speedModif = 0;
					//}
				
				
				key.update();
				gui.update();
				updateMode();
				updates++;
				delta--;
			}
			render();
		
			frames++;

			if (System.currentTimeMillis() - timer >= 1000) {
				timer += 1000;
				// System.out.println(updates + " ups, " + frames + " fps");
				
				frame.setTitle(title + " | " + updates + " ups, " + frames + " fps");
				
				if (this.recAVG_FPS) {				
				fpsTotal += frames;
				System.out.println("FPS: " + frames + " fpsIndex: " + ++fpsIndex + " AVG: " + fpsAVG);
				fpsAVG = fpsTotal / fpsIndex;
				}
				
				updates = 0;
				frames = 0;
			}
		}
		stop();
	}
	
	public static int fpsIndex = 0;
	public static int fpsTotal = 0;
	public static int fpsAVG = 0;
public int deathTimeTicks = 0;
public int deathTimeSec = 0;


	public ArrayList<String> getCharDirs() {
		ArrayList<String> result = new ArrayList<String>();
		File file = new File(SaveGame.createDataFolder() + "\\Saves\\");
		String[] names = file.list();

		for(String name : names)
		{
			//System.out.println(SaveGame.createDataFolder() + "\\Saves\\" + name);
		    if (new File(SaveGame.createDataFolder() + "\\Saves\\" + name).isDirectory()) {
		    //    System.out.println(name);
		        result.add(name);
		    }
		}
		return result;
	}

	public void switchCharacter(String name) {
		boolean disabledSave = false;
		if (autoSave) {
			System.out.println("Switching Char -- Disabled AutoSave");
			autoSave = false;
			disabledSave = true;
		}
		this.PlayerName = name;
		getPlayer().name = name;
		//getPlayer().reset(getPlayer());
		getPlayer().invokeLoad(getPlayer());
		System.out.println("Switched To: " + getPlayer().name);
		
		if (disabledSave) {
			System.out.println("Finished Switching Char -- Enabled AutoSave");
			autoSave = true;
			disabledSave = false;
		}
		try {
		loadProp.loadPrefs(this);
		} catch (Exception e) {
			autoSave = true;
		}
		
		if (Game.runTut) {
			getPlayer().setPosition(73, 38, Maps.tutWorldId, true);
		}
	}
	

	public void updateDeath() {	
		deathTimeTicks++;
		if (deathTimeTicks % 60 == 0) {
			if (deathTimeSec < 10) {
				deathTimeSec++;
				deathTimeTicks = 0;
			} else {
				deathTimeSec = 0;
				deathTimeTicks = 0;
				//Game.switchState(gameState.INGAME);
			}
		}
		
		//System.out.println("Respawning In.." + (10 - deathTimeSec));
		
			Player p = level.getClientPlayer();
			/*if (p.dead) {
			p.setX(playerRespawn.x());
			p.setY(playerRespawn.y());
			//Game.getGame().getLevel().resetLevelPostDeath(p);
			p.mobhealth = p.maxhealth;
			p.mana = p.maxmana;
			p.stamina = p.maxstamina;
			p.money = p.money * 5/6;
		//	p.ExpC -= p.ExpC * 1/30;
			p.speed = 1;
			p.riding = false;
			if ((playerRespawn.x() == playerSpawn.x()) && (playerRespawn.y() == playerSpawn.y())) {
				p.setPosition(playerSpawn.x(), playerSpawn.y(), Maps.spawnHavenId, false);
				p.setX(playerSpawn.x());
				p.setY(playerSpawn.y());
			} else {
				p.setPosition(playerRespawn.x(), playerRespawn.y(), Maps.mainId, false);
				p.setX(playerRespawn.x());
				p.setY(playerRespawn.y());
			}
//			Game.getGame().getLevel().add(p);
				p.dead = false;
		}*/
	}

	public void updatePause() {
		//System.out.println("[Game: ~773] GAMESTATE: PAUSE");
		
		
		

	}

	public void save(boolean autoOverride) {
		//if (gameState != gameState.MENU) {
		loadProp.savePrefs(this);
		if (autoSave || autoOverride) {
		List<PlayerMP> players = level.players;
		if (players != null) {
			getLevel().getClientPlayer().invokeSave(getLevel().getClientPlayer());
				}
			}
		//}
	}

	public void autoSave() {
			saveTime++;
			if ((saveTime % 400) == 0) {
				save(false);
				// loadProp.saveEquips((PlayerMP) this.getPlayer());
				// save(this.player.inventory.items);
				// System.out.println("SAVING THE GAME");
		}
	}

	public void updateMode() {
		// adminCmds();

			autoSave();
		if (key.DevMode && !devModeOn && devModeReleased && Mouse.getButton() == 2) {
			devModeOn = true;
			devModeReleased = false;
		}

		if (!key.DevMode)
			devModeReleased = true;

		if (key.DevMode && devModeOn && devModeReleased) {
			devModeOn = false;
			devModeReleased = false;
		}

		if (key.toggleDevModeInfo && !devModeInfoOn && releasedDevInfo
				&& devModeOn) {
			devModeInfoOn = true;
			releasedDevInfo = false;
		}

		if (!key.toggleDevModeInfo)
			releasedDevInfo = true;

		if (key.toggleDevModeInfo && devModeInfoOn && releasedDevInfo) {
			devModeInfoOn = false;
			releasedDevInfo = false;
		}

		if (key.capture) {
			if (screenshots.exists()) {
				try {
					captureScreen(frame, screenshots + "/Square_Legacy");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void update() {
		if (mouseMotionTime > 0) {
		this.mouseMotionTime--;
		}
		
		
		
		time2++;
		ClickTime++;

		level.update();

		/*
		 * if (swampLoaded) {
		 * 
		 * defaultLoaded = false; level = Level.Swamp; level.add(player);
		 * player.removed = true; player.isRemoved(); player.update(); }
		 */

		/*
		 * if (defaultLoaded) { swampLoaded = false; level.add(player);
		 * player.removed = true; player.isRemoved(); player.update();
		 * player.render(screen); screen.renderMob(296, 381, player); }
		 */
	}
	String mat = "";

	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		screen.clear();
		
		
	//if (!screen.shakeScreen()) {
			
		
		
		xScroll = getPlayer().getX() - screen.width / 2;
		yScroll = getPlayer().getY() - screen.height / 2;
		
		
		//}

		
			level.render((int) (xScroll), (int) (yScroll), screen);
			gui.render(screen);
			//player.renderGUI(screen);
		
		if (showAVG) { 
		if (fpsAVG < 200) {			
		font8x8.render(-5, this.height - 17, -3, 0xDB0000,
				"Average FPS: " + fpsAVG, screen, false, true);
		} else {
			font8x8.render(-5, this.height - 17, -3, 0x00ff00,
					"Average FPS: " + fpsAVG, screen, false, true);
		}
		}

		
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = screen.pixels[i];
		}

		Graphics g = bs.getDrawGraphics();
		Color Opaque = new Color(5, 0, 0, 120);

		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

		g.setColor(Opaque);

		if (devModeOn == true || Mouse.getButton() == 2) {
			g.setColor(Opaque);
			// g.fillRect(10, 80, 100, 1);
			g.fill3DRect(0, 0, 545, 95, false);
			g.fill3DRect(Mouse.getX() - 110, Mouse.getY() + 50, 250, 30, false);
			g.setColor(Color.lightGray);
			g.fillRect(Mouse.getX() - 4, Mouse.getY() - 4, 38, 38);
			g.setFont(new Font("Verdana", 0, 16));
			g.setColor(Color.WHITE);
			g.drawString("Player[UUID]: " + level.getPlayers(), 10, 40);
			// g.drawString("xScroll: " + xScroll + " yScroll: " + yScroll, 10,
			// 60);
			g.drawString("Tile: " + level.returnTile() + " || Overlay: " + level.returnOverlayTile(), 10, 60);
			g.drawString("X: " + (int) getPlayer().getX() / TileCoord.TILE_SIZE + ", Y: " + (int) getPlayer().getY() / TileCoord.TILE_SIZE, 10, 20);
			g.drawString("Mouse X: " + (int) Mouse.getX() / scale + ", Mouse Y: " + Mouse.getY() / scale, Mouse.getX() - 103, Mouse.getY() + 70);
			//screen.drawLine(getPlayer(), level.entities);
			g.setColor(Color.gray);
			// g.fill3DRect(1020, 618, 300, 300, true);
			g.setColor(Color.WHITE);

			g.setFont(new Font("Verdana", 0, 18));

			/*
			 * if (gameState == 5) { g.fill3DRect(1362, 4, 55, 30, false);
			 * g.setColor(Color.WHITE); g.setFont(new Font("Verdana",0, 18));
			 * g.drawString("Map", 1372, 25); }
			 */

			if (devModeOn == true && devModeInfoOn) {
				g.setFont(new Font("Verdana", 0, 16));
				g.drawString(
						"Developer Mode: Mouse Grid, Coordinate, Player [UUID], Scrolls",
						10, 80);
				g.setFont(new Font("Verdana", 0, 16));
				g.fill3DRect(1362, 4, 55, 30, false);
				g.setColor(Color.WHITE);
				g.setFont(new Font("Verdana", 0, 18));
				g.drawString("Map", 1372, 25);

				// g.drawString("Button: " + Mouse.getButton(), 415, 80);
			}

		}
		
		//fontLayer.render(g);

		g.dispose();
		bs.show();

	}
    
    public void setWindowIcon(String path) {
    	frame.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Game.class.getResource(path)));
    	
    }
    
    public Cursor setMouseIcon(String path) {
    	Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image = null;
		image = Toolkit.getDefaultToolkit().getImage(
				Game.class.getResource(path));

		Point hotspot = new Point(0, 0);
		Cursor cursor = toolkit.createCustomCursor(image, hotspot, "Stone");
		frame.setCursor(cursor);
		return cursor;
    }

    
    public void Launch(Game game) {
		setWindowIcon("/Textures/sheets/wizard.png");
		game.frame.setResizable(false);			
		if (Boot.launch_args.containsKey("-resizeable")) {
		game.frame.setResizable(true);			
		}
		game.frame.setTitle(Game.title);
		game.frame.add(game);
		if (Boot.launch_args.containsKey("-fullscreen")) {
			game.frame.setUndecorated(true);
			game.frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 			
		}
		//game.frame.setOpacity(0.01F);
		game.frame.pack();
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		setMouseIcon("/Textures/cursor.png");

		game.start();
		 centerMouse();
	}
    
	public void centerMouse() {
		int centreFrameX = frame.getX() + (frame.getWidth() / 2);
		int centreFrameY = frame.getY() + (frame.getHeight() / 2);
		moveMouse(new Point(centreFrameX, centreFrameY));
	}
	
	public static void moveMouse(Point p) {
	    GraphicsEnvironment ge = 
	        GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice[] gs = ge.getScreenDevices();
	    for (GraphicsDevice device: gs) { 
	        GraphicsConfiguration[] configurations =
	            device.getConfigurations();
	        for (GraphicsConfiguration config: configurations) {
	            Rectangle bounds = config.getBounds();
	            if(bounds.contains(p)) {
	                Point b = bounds.getLocation(); 
	                Point s = new Point(p.x - b.x, p.y - b.y);
	                try {
	                    Robot r = new Robot(device);
	                    r.mouseMove(s.x, s.y);
	                } catch (AWTException e) {
	                    e.printStackTrace();
	                }

	                return;
	            }
	        }
	    }
	    return;
	}
	
	
	public Screen getScreen() {
		return screen;
	}
	
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Level getLevel() {
		return level;
	}

	public GUI getGui() {
		return gui;
	}

	public void setGui(GUI gui) {
		this.gui = gui;
	}
	
	public static void log(String text, boolean err) {
		if (!err) {			
			System.out.println(" >> " + text);
		} else {
			System.err.println(" >> ALERT: " + text);			
		}
	}
	
	public static void log(String text, String outboundClass, boolean err) {
		if (!err) {			
			System.out.println(" >> " + text);
		} else {
			System.err.print(outboundClass + " >> ALERT: ");
				System.out.println(text);
		}
	}
	
	public void setMousePos(int framex, int framey) {
		moveMouse(new Point(framex, framey));
	}

	public void quit() {
		System.out.println("Saving & Closing Application");
			save(false);			
		System.exit(0);
	}
}
