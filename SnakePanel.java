import java.awt.Canvas;
import java.awt.Graphics;
import javax.swing.JFrame;
import java.awt.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class SnakePanel extends JPanel implements KeyListener
{
	private final int FEED_AMT = 2;

	public boolean init = true;

	//calculated by cord x,y is occupied[y*Xwidth + x]
	//occupied[c] refers to c%Xwidth , c/Xwidth;
	public boolean[] occ;

	public boolean[][] occupied;


	public Snake head;
	public Snake tail;

	public Food food;

	int Xwidth;
	int Ywidth;

	public boolean lost = false;

	private boolean edit = true;

	private int score = 0;

	/****************************************
						1
						^
						|   
				  0 <--------> 2
				        |
				        v
				        3
	*******************************************/
	int heading = 0;

	private int feed = 0;

	public SnakePanel(int sizeX, int sizeY)
	{
		addKeyListener(this);
		//coordinates = new boolean[sizeY][sizeX];
		Xwidth = sizeX;
		Ywidth = sizeY;

		
	}
	public SnakePanel(int size)
	{
		addKeyListener(this);
	
		Ywidth = size;

	}


	public void paint(Graphics g)
	{
		if (init)
		{
			setUpCords();

			tail = new Snake(Xwidth - 1, Ywidth - 1);
			heading = 0;
			head = tail;

			paintGrid(g);
			init = false;

			drawSnake(g);
			SetUpOccupancy();
			food = new Food(Xwidth/3,Ywidth/3);
		}
		verifyCord(head.X(), head.Y());
		if (headIntersect())
		{
			super.paint(g);
			Font font = new Font("Verdana", Font.BOLD, 60);
   			g.setFont(font);

			int[] cords = actCord(Xwidth/3, Ywidth/2);

			g.drawString("snek touch snek u lose! press p to play again", cords[0], cords[1]);
			g.drawString("score was: " + score, cords[0], cords[1] + 100);
		}
		else if (!lost)
		{	
			verifyCord(head.X(), head.Y());
			super.paint(g);
			//paintBamba(g);
			tryToEat();
			drawFood(g);
			paintGrid(g);
			advanceSnake();
			drawSnake(g);
			verifyCord(head.X(), head.Y());
			edit = true;
		}
		else if (lost)
		{
			super.paint(g);

			 Font font = new Font("Verdana", Font.BOLD, 60);
   			 g.setFont(font);

			int[] cords = actCord(Xwidth/3, Ywidth/2);

			g.drawString("You Lost!  press p to play again", cords[0], cords[1]);
			g.drawString("score was: " + score, cords[0], cords[1] + 100);
		}
	}

	public void keyPressed(KeyEvent evt) 
	{
		if (edit & !lost)
		{
		    int keyCode = evt.getKeyCode();
		 
		    if ((keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) && heading != 2){heading = 0;}
		    else if ((keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) && heading != 0){heading = 2;}
		    else if ((keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) && heading != 3){heading = 1;}
		    else if ((keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) && heading != 1){heading = 3;}
		    edit = false;
		}
		else if (lost)
		{
			int keyCode = evt.getKeyCode();

			if(keyCode == KeyEvent.VK_P)
			{
				lost = false;
				head = new Snake(Xwidth - 1, Ywidth - 1	);
				heading = 0;
				tail = head;
				score = 0;
			}
		}
  }

  	public void keyReleased(KeyEvent e){}
 	public void keyTyped(KeyEvent e){}

 	@SuppressWarnings("deprecation")
 	public boolean isFocusTraversable() {
    return true;
 	 }
  

	/*******************************************
					Set n Get
	*******************************************/
	public void setHeading(int h){heading = h;}

	public void addSnakePart()
	{		
		
		if (heading == 0 && isValidCord(head.X() - 1, head.Y()))
		{
			Snake newHead = new Snake(head.X() - 1, head.Y());
			head.setHead(newHead);
			head = newHead;
			head.setHead(null);
		}
		else if (heading == 1 && isValidCord(head.X(), head.Y() - 1))
		{
			Snake newHead = new Snake(head.X(), head.Y() - 1);
			head.setHead(newHead);
			head = newHead;
			head.setHead(null);
		}
		else if (heading == 2 && isValidCord(head.X() + 1, head.Y()))
		{
			Snake newHead = new Snake(head.X() + 1, head.Y());
			head.setHead(newHead);
			head = newHead;
			head.setHead(null);
		}
		else if (heading == 3 && isValidCord(head.X(), head.Y() + 1))
		{
			Snake newHead = new Snake(head.X(), head.Y() + 1);
			head.setHead(newHead);
			head = newHead;
			head.setHead(null);
		}
	}

	/********************************************
				Helpers and Paint
	********************************************/
	public void paintGrid(Graphics g)
	{

		for (int i = 0; i < Xwidth; i++)
		{
			g.drawLine(actCord(i, 0)[0], 0, actCord(i,0)[0], actCord(0, Ywidth)[1]);
		}
		for (int i = 0; i < Ywidth; i++)
		{
			g.drawLine(0, actCord(0,i)[1], actCord(Xwidth,0)[0], actCord(0,i)[1]);
		}
	}
	public void populate(int x, int y, Graphics g)
	{

		int[] cords = actCord(x,y);

		int width = getWidth();
		int height = getHeight();

		int squareWidth = width/Xwidth;
		int squareHeight = height/Ywidth;

		g.fillRect(cords[0], cords[1],squareWidth , squareHeight );


	}
	public void drawSnake(Graphics g)
	{
		Snake current = tail;

		while (current != null)
		{
			populate(current.X(), current.Y(), g);

			current = current.getNext();
		}
	}

	//in format {x,y}
	public int[] actCord(int ex, int why)
	{
		//verifyCord(ex, why);

		int width = getWidth();
		int height = getHeight();

		int squareWidth = width/Xwidth;
		int squareHeight = height/Ywidth;

		int[] ret = {squareWidth * ex, squareHeight * why};
		return ret;
	}
	public void advanceSnake()
	{
		verifyCord(head.X(), head.Y());
		if (feed <= 0 & !lost)
		{
			int currentX = head.X();
			int currentY = head.Y();

			occupied[tail.Y()][tail.X()] = false;


			head.setHead(tail);
			tail = tail.getNext();
			head = head.getNext();
			head.setHead(null);

			if (heading == 0)
			{
				head.setX(currentX - 1);
				head.setY(currentY);
			}
			else if (heading == 1)
			{
				head.setY(currentY - 1);
				head.setX(currentX);
			}
			else if (heading == 2)
			{
				head.setX(currentX + 1);
				head.setY(currentY);
			}
			else if (heading == 3)
			{
				head.setY(currentY + 1);
				head.setX(currentX);
			}
		}
		else if (!lost)
		{
			addSnakePart();
			feed --;
		}
		verifyCord(head.X(), head.Y());
		if (!lost)
		{
			occupied[head.Y()][head.X()] = true;
		}
	}
	public boolean headIntersect()
	{
		Snake currentSnake = tail;

		while(currentSnake.getNext() != null)
		{
			if (currentSnake.X() == head.X() && currentSnake.Y() == head.Y())
			{
				lost = true;
				return true;
			}
			currentSnake = currentSnake.getNext();
		}
		return false;
	}
	public void paintBamba(Graphics g)
	{
		for(int x = 0; x < Xwidth; x++)
		{
			for (int y = 0; y < Ywidth; y++)
			{
				int[] cord = actCord(x,y);
				int[] nextCord = actCord(x + 1, y + 1);

				Random rand = new Random();

				if (rand.nextInt(2) == 1)
				{
					g.drawString("snako mode", (nextCord[0] + cord[0])/2 - 10, (nextCord[1] + cord[1])/2);
				}
				else
				{
					g.drawString("mo boa", (nextCord[0] + cord[0])/2 + 5, (nextCord[1] + cord[1])/2);
				}
			}
		}
	}

	//sets up coords based on how many squares are desired vertically
	public void setUpCords()
	{
		int width = getWidth();
		int height = getHeight();

		int squareHeight = height/Ywidth;

		int squareWidth = squareHeight;

		Xwidth = width/squareWidth;
	}

	/******************************************
				All Things Food
	******************************************/
	public void SetUpOccupancy()
	{
		occupied = new boolean[Ywidth + 1][Xwidth + 1];

		for(int i = 0; i < Ywidth; i++)
		{
			for (int j = 0; j < Xwidth; j++)
			{
				occupied[i][j] = false;
			}
		}

		Snake currentSnake = tail;

		while(currentSnake != null)
		{
			occupied[currentSnake.Y()][currentSnake.X()] = true;
			currentSnake = currentSnake.getNext();
		}
	}
	public void tryToEat()
	{
		if (occupied[food.Y()][food.X()])
		{
			eat();
			feed += FEED_AMT;
			score++;
		}
	}
	public void eat()
	{
		Random rand = new Random();

		int x = rand.nextInt(Xwidth);
		int y = rand.nextInt(Ywidth);

		while (occupied[y][x])
		{
			x = rand.nextInt(Xwidth);
			y = rand.nextInt(Ywidth);
		}

		food = new Food(x,y);
	}

	public void drawFood(Graphics g)
	{
		g.setColor(Color.BLUE);
		populate(food.X(), food.Y(), g);
		g.setColor(Color.BLACK);
	}

	/********************************************
			 	    Verification
	*********************************************/
	public void verifyCord(int x, int y)
	{
		if (x < 0 || x >= Xwidth || y < 0 || y >= Ywidth)
		{
			lost = true;
		}
	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Snek Game");
		JPanel meme = new SnakePanel(Integer.parseInt(args[0]));

		meme.setSize(400,400);

		frame.add(meme);
       // frame.pack();
        frame.setVisible(true);

        while (true)
        {
        	try
        	{
        		Thread.sleep(Integer.parseInt(args[1]));
        		meme.repaint();
        	}
        	catch(InterruptedException e){}
        }

	}
	public boolean isValidCord(int x, int y)
	{
		if (x < 0 || x >= Xwidth  || y < 0 || y >= Ywidth )
		{
			return false;
		}
		return true;
	}
}