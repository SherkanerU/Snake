

public class Snake
{
	int x;
	int y;
	Snake nextSnake;
	

	/******************************************
					Constructors
	*******************************************/
	public Snake (int ex, int why)
	{
		x = ex;
		y = why;
		nextSnake = null;
	}
	public Snake (int ex, int why, Snake next)
	{
		x = ex;
		y = why;
		nextSnake = next;
	}

	/***************************************
					Mutators
	***************************************/
	public void setHead(Snake snake){nextSnake = snake;}
	public void setX(int ex){x = ex;}
	public void setY(int why){y = why;}

	/*
	public void advance()
	{
		if (heading == 0){x --;}
		else if (heading == 1){y--;}
		else if (heading == 2){x++;}
		else if (heading == 3){y++;}
	}
	*/

	/******************************************
					 Getters
	*******************************************/
	public Snake getNext(){return nextSnake;}
	public int X(){return x;}
	public int Y(){return y;}

}