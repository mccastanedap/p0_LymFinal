package uniandes.lym.robot.control;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import uniandes.lym.robot.kernel.*;



/**
 * Receives commands and relays them to the Robot. 
 */

public class Interpreter   {

	/**
	 * HashMap that contains the variables
	 */
	private HashMap<String, Integer> hashVariables;

	/**
	 * Robot's world
	 */
	private RobotWorldDec world;   


	// --------------------------------------------------------------------------
	// Constants
	// --------------------------------------------------------------------------

	/** 
	 * Constants to model the directions	
	 */
	public static final int NORTH = 0;
	public static final int SOUTH = 1;
	public static final int EAST = 2;
	public static final int WEST = 3;

	public static final String RIGHT = "RIGHT";
	public static final String LEFT = "LEFT";
	public static final String AROUND = "AROUND";
	public static final String FRONT = "FRONT";
	public static final String BACK = "BACK";
	public static final String BALLOONS = "BALLOONS";
	public static final String CHIPS = "CHIPS";


	public Interpreter()
	{
		hashVariables =new HashMap<String,Integer>();
	}


	/**
	 * Creates a new interpreter for a given world
	 * @param world 
	 */


	public Interpreter(RobotWorld mundo)
	{
		this.world =  (RobotWorldDec) mundo;
		hashVariables =new HashMap<String,Integer>();

	}


	/**
	 * sets a the world
	 * @param world 
	 */

	public void setWorld(RobotWorld m) 
	{
		world = (RobotWorldDec) m;

	}



	/**
	 *  Processes a sequence of commands. A command is a letter  followed by a ";"
	 *  The command can be:
	 *  M:  moves forward
	 *  R:  turns right
	 *  
	 * @param input Contiene una cadena de texto enviada para ser interpretada
	 */

	public String process(String input) throws Error
	{   


		StringBuffer output=new StringBuffer("SYSTEM RESPONSE: -->\n");	



		String[] instructions = input.split(" ");

		if( instructions[0].equals("ROBOT_R") )
		{
			if( instructions[1].equals("VARS") )
			{
				String[] variables = instructions[2].split(",");
				for( String actual : variables )
					hashVariables.put(actual, null);
			}
			String instru = "";

			if(instructions[3].equals("BEGIN") && instructions[instructions.length -1].equals("END"))
				instru = instructions[4];
			else if(instructions[1].equals("BEGIN") && instructions[instructions.length -1].equals("END"))
				instru = instructions[2];

			String[] partsInstructions = instru.split(";");

			for(String instruc : partsInstructions)
			{
				if(instruc.startsWith("assign"))
				{
					String dentroParentesis = instruc.substring(7, instruc.length()-1);
					String[] partesParentesis = dentroParentesis.split(",");
					String variable = partesParentesis[0];
					int number = Integer.parseInt(partesParentesis[1]);

					assignTo(variable, number);
				}

				else if( instruc.startsWith("move(") )
				{
					String inParentheses =instruc.substring(5, instruc.length()-1);
					move(inParentheses);
				}

				else if(instruc.startsWith("turn"))
				{
					String inParentheses = instruc.substring(5, instruc.length()-1);
					String orient = inParentheses;
					turn(orient);

				}
				else if(instruc.startsWith("face"))
				{
					String inParentheses = instruc.substring(5, instruc.length()-1);
					int quantity = Integer.parseInt(inParentheses);

					face(quantity);

				}
				else if(instruc.startsWith("put"))
				{
					String inParentheses = instruc.substring(4, instruc.length()-1);
					String[] partsParentheses = inParentheses.split(",");
					String object = partsParentheses[0];
					String quantity = partsParentheses[1];

					putNumberOf(object, quantity);

				}
				else if( instruc.startsWith("pick") )
				{
					String inParentheses =instruc.substring(5, instruc.length()-1);
					String[] partsParentheses = inParentheses.split(",");
					String object = partsParentheses[0];
					String quantity = partsParentheses[1];

					pickNumberOf(object, quantity);
				}
				else if( instruc.startsWith("moveToThe") )
				{
					String inParentheses =instruc.substring(10, instruc.length()-1);
					String [] partsParentheses = inParentheses.split(",");
					String quantity =partsParentheses[0];
					String orientation =partsParentheses[1];
					
					moveToThe(quantity,orientation);
				}
				else if( instruc.startsWith("moveInDir") )
				{
					String inParentheses =instruc.substring(10, instruc.length()-1);
					String [] partsParentheses = inParentheses.split(",");
					String quantity =partsParentheses[0];
					int direction =Integer.parseInt(partsParentheses[1]);
					
					moveInDir(quantity,direction);
				}
			}
		}
		else
		{
			output.append(" Unrecognized command:  "+ instructions[0]);
		}




//		int i;
//		int n;
//		boolean ok = true;
//		n= input.length();
//
//		i  = 0;
//		try	    {
//			while (i < n &&  ok) {
//				switch (input.charAt(i)) {
//				case 'M': world.moveForward(1); output.append("move \n");break;
//				case 'R': world.turnRight(); output.append("turnRignt \n");break;
//				case 'C': world.putChips(1); output.append("putChip \n");break;
//				case 'B': world.putBalloons(1); output.append("putBalloon \n");break;
//				case  'c': world.pickChips(1); output.append("getChip \n");break;
//				case  'b': world.grabBalloons(1); output.append("getBalloon \n");break;
//				default: output.append(" Unrecognized command:  "+ input.charAt(i)); ok=false;
//				}
//
//				if (ok) {
//					if  (i+1 == n)  { output.append("expected ';' ; found end of input; ");  ok = false ;}
//					else if (input.charAt(i+1) == ';') 
//					{
//						i= i+2;
//						try {
//							Thread.sleep(1000);
//						} catch (InterruptedException e) {
//							System.err.format("IOException: %s%n", e);
//						}
//
//					}
//					else {output.append(" Expecting ;  found: "+ input.charAt(i+1)); ok=false;
//					}
//				}
//
//
//			}
//
//		}
//		catch (Error e ){
//			output.append("Error!!!  "+e.getMessage());
//
//		}
		return output.toString();
	}

	// --------------------------------------------------------------------------
	// Project methods
	// --------------------------------------------------------------------------

	/**
	 * 
	 * @param name
	 * @param n
	 */
	public void assignTo(String name, int n)
	{
		hashVariables.replace(name, n);
	}

	/**
	 * 
	 */
	public void move(String n)
	{
		int steps =0;
		try
		{
			steps = Integer.parseInt(n);
			world.moveForward(steps);
		}
		catch(Exception e)
		{
			steps = hashVariables.get(n);
			world.moveForward(steps);
		}
	}


	/**
	 * 
	 */
	public void turn( String D )
	{
		if( D.equals(RIGHT) )
		{
			world.turnRight();
		}
		else if( D.equals(AROUND) )
		{
			world.turnRight();
			world.turnRight();
		}
		else if( D.equals(LEFT) )
		{
			world.turnRight();
			world.turnRight();
			world.turnRight();
		}
	}

	/**
	 * 
	 */
	public void face( int O )
	{
		int orientation = world.getOrientacion();
		if( O == 0 )
		{
			if( orientation == 1 )
			{
				world.turnRight();
				world.turnRight();
			}
			else if( orientation == 2 )
			{
				world.turnRight();
				world.turnRight();
				world.turnRight();
			}
			else if( orientation == 3 )
			{
				world.turnRight();
			}
		}
		else if( O == 1)
		{
			if( orientation == 0 )
			{
				world.turnRight();
				world.turnRight();
			}
			else if( orientation == 2 )
			{
				world.turnRight();
			}
			else if( orientation ==3 )
			{
				world.turnRight();
				world.turnRight();
				world.turnRight();
			}
		}
		else if( O == 2 )
		{
			if ( orientation == 0 )
			{
				world.turnRight();
			}
			else if( orientation == 1 )
			{
				world.turnRight();
				world.turnRight();
				world.turnRight();
			}
			else if( orientation == 3 )
			{
				world.turnRight();
				world.turnRight();
			}
		}
		else if( O == 3 )
		{
			if( orientation == 0 )
			{
				world.turnRight();
				world.turnRight();
				world.turnRight();
			}
			else if( orientation == 1 )
			{
				world.turnRight();
			}
			else if( orientation == 2 )
			{
				world.turnRight();
				world.turnRight();
			}
		}
	}

	/**
	 * Metodo corresponde a poner un numero de globos o de papas
	 * @param n corresponde a la cantidad de elementos
	 * @param ballonsChips puede poner elemento Ballons o elemento chips
	 */
	public void putNumberOf (String n, String ballonsChips)
	{

		int number;
		try
		{

			if(ballonsChips.equals(BALLOONS))
			{
				number = Integer.parseInt(n);
				world.putBalloons(number);
			}
			else if(ballonsChips.equals(CHIPS))
			{
				number = Integer.parseInt(n);
				world.putChips(number);
			}
		}
		catch(Exception e){


			if(ballonsChips.equals(BALLOONS)){
				number= hashVariables.get(n);
				world.putBalloons(number);
			}
			else if(ballonsChips.equals(CHIPS)){
				number= hashVariables.get(n);
				world.putChips(number);
			}
		}
	}
	/**
	 * Metodo encargado de recoger un cierto numero de globos o papas
	 * @param n corresponde a la cantidad de elementos
	 * @param ballonsChips puede poder elementos Ballons o elementos chips 
	 */
	public void pickNumberOf (String n, String ballonsChips){

		int number;
		try
		{
			if(ballonsChips.equals(BALLOONS))
			{
				number = Integer.parseInt(n);
				world.grabBalloons(number);
			}
			else if(ballonsChips.equals(CHIPS))
			{
				number = Integer.parseInt(n);
				world.pickChips(number);
			}
		}
		catch(Exception e)
		{
			if(ballonsChips.equals(BALLOONS))
			{
				number= hashVariables.get(n);
				world.grabBalloons(number);
			}
			else if(ballonsChips.equals(CHIPS))
			{
				number= hashVariables.get(n);
				world.pickChips(number);
			}
		}
	}
	/**
	 * Metodo encargado de mover el robot mirando a cierto punto sin cambiar su mirada incial	
	 * @param n cuanto se mueve 
	 * @param direction si se mueve hacia el FRONT, BACK. RIGHT, LEFT
	 */
	public void moveToThe(String n, String direction)
	{
		int number;
		if( direction.equals(BACK) )
		{			
			if( !world.estaAbajo() ) 
			{
				try
				{
					number = Integer.parseInt(n);
					world.moveVertically(number);
				}
				catch( Exception e )
				{
					number = hashVariables.get(n);
					world.moveVertically(number);
				}
			}

		}
		else if( direction.equals(FRONT) )
		{
			if( !world.estaArriba() )
			{
				try
				{
					number = Integer.parseInt(n);
					world.moveVertically(-number);
				}
				catch( Exception e )
				{
					number = hashVariables.get(n);
					world.moveVertically(-number);
				}
			}
		}
		else if( direction.equals(LEFT) )
		{
			if( !world.estaIzquierda() )
			{
				try
				{
					number = Integer.parseInt(n);
					world.moveHorizontally(-number);
				}
				catch( Exception e )
				{
					number = hashVariables.get(n);
					world.moveHorizontally(-number);
				}
			}
		}
		else if( direction.equals(RIGHT) )
		{
			if( !world.estaDerecha() )
			{
				try
				{
					number = Integer.parseInt(n);
					world.moveHorizontally(number);
				}
				catch( Exception e )
				{
					number = hashVariables.get(n);
					world.moveHorizontally(number);
				}
			}
		}

	}

	public void moveInDir(String n, int O){
		face(O);
		move(n);
	}

}



