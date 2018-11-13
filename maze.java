
package maze_solve;

/**
 *
 * @author Aditya Veer Parmar
 *
 */
import java.awt.Color;
import java.awt.Graphics;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class maze extends JFrame {

    /**
     * Conventions:
     *
     * maze[row][col]
     *
     * Values: 0 = not-visited node 1 = wall (blocked) 8 = agent 9 = target node
     *
     * borders must be filled with "1" to void ArrayIndexOutOfBounds exception.
     */
    private int [][] grid = 
        { {1,1,1,1,1,1,1,1,1,1},
          {1,8,0,0,0,0,0,0,0,1},
          {1,0,0,1,0,1,0,1,1,1},
          {1,0,0,1,0,0,0,0,0,1},
          {1,0,0,1,1,1,1,1,0,1},
          {1,0,0,1,0,0,0,0,0,1},
          {1,0,0,1,0,0,1,0,1,1},
          {1,0,0,1,0,0,0,0,0,1},
          {1,0,0,0,0,0,0,0,9,1},
          {1,1,1,1,1,1,1,1,1,1}
        };
    /*private final int[][] grid
            = {{1, 1, 1, 1, 1},
            {1, 8, 0, 0, 1},
            {1, 0, 0, 0, 1},
            {1, 0, 0, 9, 1},
            {1, 1, 1, 1, 1}
            };*/
    q_learning agent = new q_learning(grid, grid.length);
    private final int[][] ogrid = agent.copy(grid);

    public maze() {

        setTitle("Maze Solving using q_learning");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.translate(50, 50);
        int[][] grid1 = agent.copy(ogrid);
        int x[][] = agent.copy(ogrid);
        for (int i = 0; i < agent.num_ep; i++) {
            agent.grid = agent.copy(x);
            grid1 = agent.copy(ogrid);
            int step=0;
            while (true) {
                //try{
                
                for (int row = 0; row < grid1.length; row++) {
                    for (int col = 0; col < grid1[0].length; col++) {
                        Color color;
                        switch (grid1[row][col]) {
                            case 1:
                                color = Color.BLACK;
                                break;
                            case 9:
                                color = Color.GREEN;
                                break;
                            default:
                                color = Color.WHITE;
                        }
                        g.setColor(color);
                        g.fillRect(30 * col, 30 * row, 30, 30);
                        g.setColor(Color.BLACK);
                        g.drawRect(30 * col, 30 * row, 30, 30);
                    }
                }
                for (int row = 0; row < grid1.length; row++) {
                    for (int col = 0; col < grid1[0].length; col++) {
                        Color color;
                        if (grid1[col][row] == 8) {
                            g.setColor(Color.cyan);
                            g.fillOval(row * 30, col * 30, 30, 30);
                        }
                    }
                }
                
                //}
                //catch(Exception e){//agent.grid=agent.copy(ogrid);}
                if(i>500){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(maze.class.getName()).log(Level.SEVERE, null, ex);
                }
                }
                grid1 = agent.brain(i);
                /*for (int row = 0; row < grid1.length; row++) {
                    System.out.println();
                    for (int col = 0; col < grid1[0].length; col++){
                        System.out.print(grid1[row][col]+"\t");
                    }
                    }*/
                //System.out.println();
                //agent.grid = agent.copy(grid1);
                if (grid1 == null) { 
                    break;
                }
                //removeAll();
                //validate();
                //repaint();
                System.out.println("step:"+step);
                step++;
            }
            System.out.println("EPISODE NO:"+i);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                maze view = new maze();
                view.setVisible(true);
            }
        });
    }

}
