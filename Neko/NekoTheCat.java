import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class NekoTheCat implements MouseListener, Runnable {

	Image catRight1 = new ImageIcon(getClass().getResource("Neko1.gif")).getImage();
	Image catRight2 = new ImageIcon(getClass().getResource("Neko2.gif")).getImage();
	Image catLeft1  = new ImageIcon(getClass().getResource("Neko3.gif")).getImage();
	Image catLeft2  = new ImageIcon(getClass().getResource("Neko4.gif")).getImage();
	Image redBall   = new ImageIcon(getClass().getResource("red-ball.gif")).getImage();
	
	Image cat1 = catRight1;
	Image cat2 = catRight2;
	Image currentImage = catRight1;
	
	JFrame gameWindow = new JFrame("Neko The Cat!");
	JPanel gamePanel  = new JPanel();
	
	int     catxPosition  			= 1;    
	int     catyPosition  			= 50;
	int     catWidth      			= catRight1.getWidth(gamePanel);
	int     catHeight     			= catRight1.getHeight(gamePanel);
	int     ballxPosition 			= 0;
	int     ballyPosition 			= 0;
	int     ballSize      			= redBall.getWidth(gamePanel); 
	int     sleepTime     			= 100; // pause time between image repaints (in ms)
	int     xBump         			= 10;  // amount cat image is moved each repaint.
	int 	yBump					= 10;
	boolean catIsRunningDown	 	= false; 
	boolean catIsRunningUp		  	= false;
	boolean catIsRunningToTheRight 	= true; 
	boolean catIsRunningToTheLeft  	= false;
	boolean ballHasBeenPlaced      	= false;
	
	Graphics g;
	
	AudioClip soundFile = Applet.newAudioClip(getClass().getResource("spacemusic.au"));
	
	public NekoTheCat() {
		
		// get ready
		gameWindow.getContentPane().add(gamePanel,"Center");
		gamePanel.setBackground(Color.white);
		gameWindow.setSize(700, 700);
		gameWindow.setVisible(true);
		gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		g = gamePanel.getGraphics();
		gamePanel.addMouseListener(this); 		// call me!
		soundFile.loop();
		
		// show game instructions on the screen
		g.setFont(new Font("Times Roman", Font.BOLD, 20));
		g.drawString("Neko the cat is looking for it's red ball!"         ,100,100);
		g.drawString("Click the mouse to place Neko's ball."              ,100,120);
		g.drawString("Can you move the ball to keep Neko from getting it?",100,140);
		g.drawString("(Pull window larger to make the game easier)"       ,100,160);
	}

	public static void main(String[] args) {
		
		NekoTheCat ntc = new NekoTheCat();
		ntc.run();
	}

	public void run() {
		
		// g.drawImage(catRight1,0,0,gamePanel);			//image,x,y,where
		// g.drawImage(catRight2,1*catWidth,0,gamePanel);
		// g.drawImage(catLeft1, 2*catWidth,0,gamePanel);
		// g.drawImage(catLeft2, 3*catWidth,0,gamePanel);
		// g.drawImage(redBall,  4*catWidth,0,gamePanel);
		
		while(true) {
				while(!ballHasBeenPlaced) {
					
					// 1. Blank out the last image
					eraseNeko();
	        
					// 2. Bump the location for the new image
					catxPosition += xBump;
					checkIfNekoNeedsToChangeDirections(); 
				
					// 3. Select the next image.
					selectNextImage();
	        
					// 4. Draw the next cat image
					g.drawImage(currentImage,catxPosition,catyPosition,gamePanel);
	        
					// 5. Pause briefly to let human eye see the new image!
					pause();
				}
				
					// 6. Have Neko chase the ball
					chase();
				
			 
					// 7. Proximity test to see if Neko is "at" the ball. 
					if ((Math.abs(catyPosition - ballyPosition) < 10)   // y within 10   
							&& (Math.abs(catxPosition - ballxPosition) < 10))  // x within 10 pixels
					{	
						gamePanel.removeMouseListener(this);
						gamePanel.setBackground(Color.red);
						g.setFont(new Font("Times Roman", Font.BOLD, 50));
						g.drawString("At last I have my ball!"         ,50,50);
						soundFile.stop();
						return;
					}
			}
	}
	
	private void chase() 
	{
		checkIfNekoNeedsToChangeDirections();
		
		// If Neko is BELOW the ball
	    if (catyPosition > ballyPosition) {
	    	eraseNeko();
	    	catyPosition -= yBump;
			g.drawImage(currentImage,catxPosition,catyPosition,gamePanel);
			pause();
	    }
	       	   
	    // If Neko is ABOVE the ball 
	    if (catyPosition < ballyPosition) {
	    	eraseNeko();
	    	catyPosition += yBump;
			g.drawImage(currentImage,catxPosition,catyPosition,gamePanel);
			pause();
	    }  	   
	    
	    // If Neko is TO THE LEFT OF the ball 
	    if (catxPosition < ballxPosition) {
	    	eraseNeko();
	    	catxPosition += xBump; 
			selectNextImage();
			g.drawImage(currentImage,catxPosition,catyPosition,gamePanel);
			pause();
	    } 
	    
	    // If Neko is TO THE RIGHT OF the ball 
	    if (catxPosition > ballxPosition) {
	    	eraseNeko();
	    	catxPosition += xBump; 
			selectNextImage();
			g.drawImage(currentImage,catxPosition,catyPosition,gamePanel);
			pause();
	    } 
	    
	    // If Neko is DIRECTLY ABOVE the ball
	    while (catxPosition == ballxPosition) {
	    	eraseNeko();
	    	catyPosition += yBump; 
			selectNextImage();
			g.drawImage(currentImage,catxPosition,catyPosition,gamePanel);
			pause();
	    } 
	    
	}
	
	private void pause()
	{
		try {Thread.sleep(sleepTime);}
		catch(InterruptedException ie){}
	}
	
	private void selectNextImage ()
	{
		if (currentImage == cat1) currentImage = cat2;
		else                     currentImage = cat1;
	}
	
	private void eraseNeko()
	{
		g.setColor(Color.white); 	
		g.fillRect(catxPosition, catyPosition, catWidth, catHeight);
	}
	
	private void checkIfNekoNeedsToChangeDirections()
    {
		if (catxPosition > gamePanel.getSize().width)
		{
			reverseDirectionFromRightToLeft();
			catxPosition = gamePanel.getSize().width -1;
		}

		if (catxPosition < 0)
		{
			reverseDirectionFromLeftToRight();
			catxPosition = 1;
		}
		
		if ((catxPosition > ballxPosition) && catIsRunningToTheRight && ballHasBeenPlaced)
		{
			reverseDirectionFromRightToLeft();
		}

		if ((catxPosition < ballxPosition) && catIsRunningToTheLeft && ballHasBeenPlaced)
		{
			reverseDirectionFromLeftToRight();
		}
		
		if ((catyPosition > ballyPosition) && catIsRunningDown && ballHasBeenPlaced)
		{
			reverseDirectionFromDownToUp();
		}

		if ((catyPosition < ballyPosition) && catIsRunningUp && ballHasBeenPlaced)
		{
			reverseDirectionFromUpToDown();
		}
    }
	
	private void reverseDirectionFromRightToLeft() {
    xBump = -xBump; 		
    cat1 = catLeft1;
    cat2 = catLeft2;
    catIsRunningToTheLeft  = true;
    catIsRunningToTheRight = false;
    }
	
	private void reverseDirectionFromLeftToRight()
    {
    xBump = -xBump;	
    cat1 = catRight1;
    cat2 = catRight2;
    catIsRunningToTheRight = true;
    catIsRunningToTheLeft  = false;
    }
	
	private void reverseDirectionFromDownToUp() { 	
	    catIsRunningUp		= true;
	    catIsRunningDown	= false;
	 }
	
	private void reverseDirectionFromUpToDown() {	
	    catIsRunningUp		= false;
	    catIsRunningDown	= true;
	 }
	
	
	public void mouseClicked(MouseEvent me) {
	    
		ballHasBeenPlaced = true;
		
	    // erase the old ball image
	    g.setColor(Color.white); 	// set to background color
	    g.fillRect(ballxPosition, ballyPosition, ballSize, ballSize);	//x,y,width,height
	    
	    // set the ball to new location
		ballxPosition = me.getX();
	    ballyPosition = me.getY();
	    g.drawImage(redBall, ballxPosition, ballyPosition ,gamePanel);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
