
packge maze_solve;

/**
 *
 * @author Aditya veer Parmar
 */
import java.util.Arrays;
import java.util.Random;

public class q_learning {

    int grid[][] = new int[10][10];
    int size;
    double lr = 0.7, gamma = 0.95;
    int num_ep = 5000;
    double q_table[][] = new double[100][10];

    q_learning(int[][] grid, int size) {
        this.grid = grid;
        this.size = size;
        for (int i = 0; i < size * size; i++) {
            for (int j = 0; j < 4; j++) {
                q_table[i][j] = 0;
            }
        }
        //o_grid = copy(grid);
    }

    int state_finder(int[] l) {
        int temp = -1;
        int state = -1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                temp++;
                if (i == l[0] && j == l[1]) {
                    state = temp;
                }
            }
        }
        return state;
    }

    int[] find_pos() {
        int pos[] = new int[2];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == 8) {
                    pos[0] = i;
                    pos[1] = j;
                    return pos;
                }
            }
        }
        return null;
    }

    void take_action(int action) {
        int[] pos = find_pos();
        int i = pos[0];
        int j = pos[1];
        grid[i][j] = 0;
        switch (action) {
            case 0:
                if (grid[i][j + 1] != 1) {
                    grid[i][j + 1] = 8;
                } else {
                    grid[i][j] = 8;
                }
                break;
            case 1:
                if (grid[i - 1][j] != 1) {
                    grid[i - 1][j] = 8;
                } else {
                    grid[i][j] = 8;
                }
                break;
            case 2:
                if (grid[i][j - 1] != 1) {
                    grid[i][j - 1] = 8;
                } else {
                    grid[i][j] = 8;
                }
                break;
            case 3:
                if (grid[i + 1][j] != 1) {
                    grid[i + 1][j] = 8;
                } else {
                    grid[i][j] = 8;
                }
                break;
        }
    }

    double max(double[] arr) {
        double max = -9999;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }

    int[][] brain(int num) {
        int goal = 0;
        int[] l = new int[2];
        int action;
        double n = (double) num;
        Random a = new Random();
        int s = state_finder(find_pos()), s1;
        double reward;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == 9) {
                    l[0] = i;
                    l[1] = j;
                    goal = state_finder(l);
                }
            }
        }

        while (true) {
            int max_i = 1;
            double max_q = -99999;
            if (a.nextDouble() > 20 / (n + 1)) {
                for (int j = 0; j < 4; j++) {
                    if (q_table[s][j] > max_q) {
                        max_q = q_table[s][j];
                        max_i = j;
                    }
                }
                action = max_i;
            } else action = a.nextInt(4);
            take_action(action);
            s1 = state_finder(find_pos());
            if (s1 == goal) {
                reward = 2;
            } else {
                reward = -0.1;
            }
            q_table[s][action] = q_table[s][action] + lr * (reward + gamma * max(q_table[s1]) - q_table[s][action]);
            s = s1;
            if (s == goal) {
                break;
            }
            return grid;
        }
        return null;
    }

    StringBuffer path() {
        int[] pos = new int[2];
        int goal = -1;
        StringBuffer path = new StringBuffer();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == 9) {
                    pos[0] = i;
                    pos[1] = j;
                    goal = state_finder(pos);
                }
            }
        }
        int s = state_finder(find_pos());
        while (true) {
            int max_i = 1;
            double max_q = -99999;
            for (int j = 0; j < 4; j++) {
                if (q_table[s][j] > max_q) {
                    max_i = j;
                }
            }
            path.append(max_i);
            take_action(max_i);
            s = state_finder(find_pos());
            if (s == goal) {
                break;
            }
        }
        return path;
    }

    public int[][] copy(int[][] input) {
        int[][] target = new int[input.length][];
        for (int i = 0; i < input.length; i++) {
            target[i] = Arrays.copyOf(input[i], input[i].length);
        }
        return target;
    }
}
