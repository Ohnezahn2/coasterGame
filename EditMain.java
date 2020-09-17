package editmode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entity.Camera;
import entity.Entity;
import entity.Light;
import gui.GuiRenderer;
import gui.GuiTexture;
import object.SceneObject;
import renderEngine.DisplayManager;
import renderEngine.FileEncoder;
import renderEngine.Loader;
import renderEngine.MasterRenderer;

public class EditMain {
	private static final float WIDTH = 1.980f;
	private static final float HEIGHT = 1.080f;
	private static final float gridDistance = 1.01f;
	private static final float scale = 0.4f;
	
	private static JPanel scoEditor = new JPanel(new BorderLayout());
	private static JPanel chooserPanel = new JPanel(new BorderLayout());
	private static JPanel scoOptions = new JPanel();
	
	private static List<GuiTexture> grid = new ArrayList<GuiTexture>();
	
	private static JFrame scoEditorFrame = new JFrame();
	
	private static JFileChooser fileChooser = new JFileChooser();
	
	private static int result;
	
	private static boolean isEditorPressed;
	
	private static Camera previewCam;
	
	private static SceneObject selectedSco;
	private static boolean wasSelected;
	
	private static float rot;
	private static float speed = 530.0f;
	private static MasterRenderer renderer;
	
	public static void update(Loader loader) {
		GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f );
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		
		
		if(Keyboard.isKeyDown(Keyboard.KEY_R)) {
				if(!isEditorPressed) {
					fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
					FileFilter filter = new FileNameExtensionFilter("Coaster SceneObject","csco");
					fileChooser.setFileFilter(filter);
					result = fileChooser.showOpenDialog(chooserPanel);
					speed = 530.0f;
					wasSelected = false;
					
					isEditorPressed = true;
				}
			}else{
				isEditorPressed = false;
			}
			
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				if(selectedFile != null) {
					List<Light> lights = new ArrayList<Light>();
					if(!wasSelected) {
						Light sun = new Light(new Vector3f(100000,150000,-100000), new Vector3f(0.2f,0.2f,0.2f));
						lights.add(sun);
						selectedSco = FileEncoder.decodeSco(selectedFile ,loader);
						wasSelected = true;
					}
					if(speed > 15.0f) {
						speed -= DisplayManager.getDeltaTime() * 30.6f;
					}else {
						speed = 15.0f;
					}
					rot += speed*DisplayManager.getDeltaTime();
					selectedSco.setRotation(new Vector3f(0,rot,0));
					Entity ent = new Entity(selectedSco, new Vector3f(),new Vector3f(), new Vector3f(1,1,1));
					renderer.processEntity(ent);
					renderer.setParent(scoEditor);
					renderer.render(lights, previewCam, true);
				}
				
			}
	}
	
	public static synchronized void init(Loader loader) {
		
		
		previewCam = new Camera(new Vector3f(0,10,40), 0,0,0);
		for(int i = 0; i<1; i++){
			GuiTexture tex = new GuiTexture(loader.loadTexture("grid"), new Vector2f((-1 + (gridDistance/2))+ i *gridDistance*scale,1), new Vector2f(scale/WIDTH,scale/HEIGHT));
			grid.add(tex);
		}
		for(int y = 0; y<1; y++) {
			GuiTexture tex = new GuiTexture(loader.loadTexture("grid"), new Vector2f(-1,y *gridDistance*scale), new Vector2f(scale/WIDTH,scale/HEIGHT));
			grid.add(tex);
		}
	}
	public static synchronized void lateInit(Loader loader) {
		
		scoEditorFrame= new JFrame("Welecome to JavaTutorial.net");    
		//scoEditorFrame.getContentPane().add(new JFrameGraphics());
		scoEditorFrame.setSize(1020, 1020);
        scoEditorFrame.setVisible(true);
        //scoEditorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        scoEditorFrame.setResizable(false); 
        
        chooserPanel.setSize(new Dimension(480, 480));
        chooserPanel.setBackground(Color.red);
        chooserPanel.setForeground(Color.green);
		
		scoEditor.setSize(new Dimension(640, 480));
		scoEditor.setBackground(Color.red);
		scoEditor.setForeground(Color.green);
		scoOptions.setLayout(new BoxLayout(scoOptions, BoxLayout.Y_AXIS));
		scoOptions.add(new JLabel("Test"));
		scoOptions.add(new JLabel("whooooo"));
		
		
		scoEditorFrame.add(chooserPanel);
		//scoEditorFrame.add(scoEditor);
		scoEditorFrame.add(scoOptions);
		scoEditorFrame.setVisible(true);
		GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f );
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);	
		GuiRenderer guiRen = new GuiRenderer(loader);
		guiRen.render(grid);
	
		renderer = new MasterRenderer(loader, previewCam);
		
	}
	
	
}
