package Server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import java.awt.datatransfer.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.TrayIcon;
import java.awt.color.ColorSpace;

public class Main {
	
	private static ServerSocket serverSocket;
	private static Socket clientSocket;
	private static String message;
	private static Thread thread;
	private static DataOutputStream out;
	private static DataInputStream in;
	private static Clipboard clpbrd;
	private static Boolean isPasted = false;
	private static Boolean isStarted = true;
	
	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	public static void main(String[] args) {
		
		if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final TrayIcon trayIcon =
                new TrayIcon(new ImageIcon(Main.class.getResource("resources/images/clipboard.png"),"tray").getImage());
        final SystemTray tray = SystemTray.getSystemTray();
        
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
        
        trayIcon.addMouseListener(new MouseAdapter() {
						
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2){
					isStarted = false;
					System.exit(0);
				}
			}
		});
        
		clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					while (isStarted) {
						serverSocket = new ServerSocket(4444); // Server socket
					}

				} catch (IOException e) {
					System.out.println("Could not listen on port: 4444");
				}

				System.out.println("Server started. Listening to the port 4444");

				while (true) {
					try {
						clientSocket = serverSocket.accept(); // accept the client connection
						out = new DataOutputStream(clientSocket.getOutputStream());
						in = new DataInputStream(clientSocket.getInputStream());
												
						while (true) {
                            int size = in.readInt();

                            if (size > 0)
                            {
                            	int type = in.readInt();
                            	switch (type) {
                            	case 1 :
                            		byte[] inMessage = new byte[size];
                                    in.read(inMessage);
                                    
                                    String mess = new String(inMessage,"UTF-8");
                                    
                                    StringSelection str = new StringSelection(mess);
                                    clpbrd.setContents(str, null);
                                    isPasted = true;
                            		break;
                            	case 2:
                            		byte[] inImage = new byte[size];
                            		in.read(inImage);
                            		
                            		int fNameSize = in.readInt();
                            		byte[] inFileName = new byte[fNameSize];
                                    in.read(inFileName);
                                    
                                    String fileName = new String(inFileName,"UTF-8");
                            		
                            		String home = System.getProperty("user.home");
                            		String path = home.replace("\\", "/")+"/Downloads/" + fileName;
                            		
                            		File file = new File(path);
                            		if(!file.exists()) {
                            			file.createNewFile();
                            		} 
                            		
                            		OutputStream out = new FileOutputStream(file, false);
                            		out.write(inImage);
                            		out.close();
                            		
                            	    try {
                            	        if (Desktop.isDesktopSupported()) {
                            	          Desktop.getDesktop().open(file);
                            	        }
                            	      } catch (IOException ioe) {
                            	        ioe.printStackTrace();
                            	     }
                            	    
//                            		}
//                            		catch (Exception e){
//                            			System.out.println("exception : " + e.getMessage());
//                            		}
                            	                                	    
                            		break;
                            	}
                                
                            }
                        }
					} 
					catch (IOException ex) {
						System.out.println("Problem in message reading");
						ex.printStackTrace();
					}
					finally{
						if (clientSocket != null && clientSocket.isConnected() && in != null && out != null){
							try {
								in.close();
								out.close();
								clientSocket.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}								
						}						
					}						
				}
			}
		});
		
		thread.start();
		
		clpbrd.addFlavorListener(new FlavorListener() { 
            @Override 
            public void flavorsChanged(FlavorEvent e) { 
            	try {
            		if (isPasted) {
                        isPasted = false;
                        return;
                    }
            		
            		isPasted = true;
					message = clpbrd.getData(DataFlavor.stringFlavor).toString();
					clpbrd.setContents(new StringSelection(message), null);
										
					if (out != null) {
						byte[] b = message.getBytes(UTF8_CHARSET);
						out.writeInt(b.length);
						out.write(b); // write the message to output stream
                        out.flush();
					}
						
				} catch (UnsupportedFlavorException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
             }
        });
	}
}
