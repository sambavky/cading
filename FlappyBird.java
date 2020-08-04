package flappyBird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;
import javax.swing.JFrame;

/**
 *
 * @Muhamaad Syahmi Bin Sairin
 */

public class FlappyBird implements ActionListener, MouseListener, KeyListener{
    
    // create an object of the class
    public static FlappyBird flappyBird;
    
    //set the height and width variables for the frame
    public final int WIDTH = 1200, HEIGHT = 900;
    
    //create an instance of the Renderer class
    public Renderer renderer;
    
    //create the bird ( the red rectangle)
    public Rectangle bird;
    
    //store all the columns(pipe) here
    public ArrayList<Rectangle> columns;
    
    //use to generate a random columns(pipe)
    public Random rand;
    
    //set the variables 
    public int ticks, birdmovement, score, highscore;
    
    //set variable of the game state
    public boolean gameover, started;
    
    
    //default constructor
    public FlappyBird(){
        //create a new JFrame
        JFrame jframe = new JFrame();
        Timer timer = new Timer(20,this);
        
        //initiate the renderer
        renderer = new Renderer();
        rand = new Random();
        //Adding both mouse and key listener for interaction
        jframe.addKeyListener(this);           
        jframe.addMouseListener(this);
        jframe.add(renderer);
        //set the programme to terminate on close 
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //set the size of the frame
        jframe.setSize(WIDTH, HEIGHT);
        ///We don't want the user to change the height & width of the frame
        jframe.setResizable(false);
        //Displaying the frame
        jframe.setVisible(true);        
        //set the title of the game
        jframe.setTitle("Flappy Birdy");
        //Making the bird appear a little off the centre of the frame
        bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10,20 ,20);           
        columns = new ArrayList<Rectangle>();
        
        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);
        
        timer.start();
    }
    
    
    //create one new column randomly
    public void addColumn(boolean start){
        int space = 300;
        int width = 100;
        int height = 50 + rand.nextInt(300);
        
        if(start){          //If it is true, that means we are going to start the game
            columns.add(new Rectangle(WIDTH + width+ columns.size() * 300, HEIGHT - height - 180, width, height));      //lower column
            columns.add(new Rectangle(WIDTH + width+ (columns.size() -1) * 300, 0, width, HEIGHT - height - space));    //upper column
        }
        else{
            columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 180, width, height));   //Getting the last column and placing the next lower pipe at 600(to the right) from it
            columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));            //
        } 
    }
    public void paintColumn(Graphics g, Rectangle column){              //Painting the columns on the frame
        g.setColor(Color.green.darker().darker());                      
        g.fillRect(column.x, column.y, column.width, column.height);
    }
    
    public void jump(){
        if(gameover){               //If game is over, reset everything
             bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10,20 ,20);      //Reset bird position
            columns.clear();        //Clear list of columns
            birdmovement = 0;       //Reset yMotion
            score = 0;              //Reset Score
            
            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);
            
            gameover = false;       //So that on next click it starts again
        }
        if(!started){               //Starts the game on first click
            started = true;
        }
        else if(!gameover){
            if(birdmovement > 0){   //Everytime you click, it is set to 0 so that gravity doesn't pull it down much
                birdmovement = 0;
            }
        birdmovement -= 10;         //act as gravity 
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        int speed = 10;
        
        ticks++;
        
        if(started){                //Movement starts only when game has started
            for(int i=0; i< columns.size(); i++){       //Decreasing x-coordinate by speed so that it seems as if it is moving left
                Rectangle column = columns.get(i);
                column.x -= speed;  //
            }
            if(ticks % 2 == 0 && birdmovement < 15){    //Increase the y-axis-motion of the bird so that it seems as if it jumps when an action is performed
                birdmovement += 2;
            }
        
            for(int i=0; i< columns.size(); i++){
                Rectangle column = columns.get(i);
                if(column.x + column.width < 0){        //If column is out of the left of the frame, remove it from the ArrayList
                    columns.remove(column);
                    if(column.y == 0){                  //We add 2 pipes for each upper pipe that is removed; We don't want to repeat calling addColumn 2 times
                        addColumn(false);
                    }
                }
            }
        }
        bird.y += birdmovement;                         //Increase the y coordinate to give it the motion
        
    for (Rectangle column : columns){                   //Collision Detection and Score Keeping
        if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 10 && bird.x + bird.width / 2 < column.x + column.width / 2 + 10){     //If bird reaches center of the column, add 1 to score
		
            score++;
        }
                
	if (column.intersects(bird)){                   //Collision Detection with the columns
            gameover = true;
            if (bird.x <= column.x){                    //If the bird hits the column wall, then it stays before it
		bird.x = column.x - bird.width;
            }
            else{
		if (column.y != 0){                     //If bird hits lower pipe top then it stays on top of it
                    bird.y = column.y - bird.height;
		}
		else if (bird.y < column.height){       //If bird hits upper pipe bottom so that it doesn't look as if it's sliding through it
                    bird.y = column.height;
		}
            }
        }
    }

    if (bird.y > HEIGHT - 180 || bird.y < 0){           //collision Detection for when the bird touches the ground OR it flies away
        gameover = true;
    }
    if (bird.y + birdmovement >= HEIGHT - 180){         //Condition ensures that the bird gradually falls down instead of at once
	bird.y = HEIGHT - 180 - bird.height;
	gameover = true;
    }
    renderer.repaint();
    }
  
    public void repaint(Graphics g) {                   //Repainting the frame
        g.setColor(Color.cyan);                         //Creating the sky
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        g.setColor(Color.DARK_GRAY);                    //Creating the ground
        g.fillRect(0, HEIGHT - 180, WIDTH, 180);
        
        g.setColor(Color.green);                        //creating top of the ground
        g.fillRect(0, HEIGHT - 180, WIDTH, 40);
        
        g.setColor(Color.red);                          //Coloring the bird red
        g.fillRect(bird.x, bird.y, bird.width, bird.height);
        
        for(Rectangle column : columns){                //For each rectangle in columns ArrayList it will be painted
            paintColumn(g, column);
        }
        
        g.setColor(Color.white);
        g.setFont(new Font("Tahoma", 1, 90));
        
        if(!started){
            g.drawString("Click to play ", 300, HEIGHT / 2 - 50);
        }
        if(gameover){
            g.drawString("Better Luck Next Time.", 85, HEIGHT / 2 - 150);
            g.drawString(String.valueOf("Your score is " + score), WIDTH /2 - 350 , 100);
            g.drawString("Click to play again", 180, HEIGHT / 2 - 1);
           
        }
        if(!gameover && started){                       //Shows the score at the top when game is ongoing
            if(score>highscore)
                highscore = score;
                g.drawString(String.valueOf(score), WIDTH /2 - 50 , 100);
        }
    }
    
    public static void main(String[]args){              //Main function
        flappyBird = new FlappyBird();
    }

    @Override
    public void mouseClicked(MouseEvent me) {           //When mouse is clicked the bird will jump
        jump();
    }

    @Override
    public void mousePressed(MouseEvent me) {
        
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        
    }

    @Override
    public void mouseExited(MouseEvent me) {
        
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        
    }

    @Override
    public void keyReleased(KeyEvent ke) {                  //When space is clicked, jump
        if(ke.getKeyCode() == KeyEvent.VK_SPACE){
            jump();
        }
    }

}
