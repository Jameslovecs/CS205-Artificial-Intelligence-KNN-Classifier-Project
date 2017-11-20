package EightPuzzle;

import java.util.*;


public class EightPuzzle {
    //puzzle size
    private int n;
    private char[][] goalState;
    private char[][] initialState;
    private int gn = 0;
    private int space = 0;
    //Initialize
    public EightPuzzle() {
        this.n = 3;
        this.goalState = new char[][] {{'1', '2', '3'}, {'4', '5', '6'}, {'7', '8', 'b'}};
        this.initialState = new char[][]{{'1', '8', '7'},{'3', 'b', '5'},{'6', '2', '4'}};;
    }
    public boolean heuristicSearch(int type) {

        //Generate corresponding queue, using regular queue for uniform cost and priority queue for others.
        Queue<Node> queue = null;
        if (type == 1) {
            queue = new LinkedList<>();
        } else {

             = new PriorityQueue<>(new Comparator<Node>() {
                public int compare(Node n1, Node n2) {
                    return type == 2 ? n1.misplaced - n2.misplaced : n1.manhattan - n2.manhattan;
                }
            });
        }
        //Enqueue
        queue.offer(new Node(initialState, getMisplacedTilte(initialState), getManhattanDis(initialState)));
        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};
        HashSet<String> visited = new HashSet<>();
        //Check initial state
        if (isInvalid(initialState)) {
            return false;
        }
        if (goalTest(initialState)) {
            disPlay(queue.peek(), type);
            return true;
        }

        while (!queue.isEmpty()) {
            //Record max size of queue
            space = Math.max(space, queue.size());
            //Pop minimal cost node from queue
            Node curNode = queue.poll();
            gn++;
            disPlay(curNode, type);
            //Using hash to remove duplicate state
            visited.add(getHash(curNode));
            //Reach goal state
            if (goalTest(curNode.state)) {
                return true;
            }
            //Check for position
            for (int i = 0; i < 4; i++) {
                int nx = curNode.x + dx[i];
                int ny = curNode.y + dy[i];
                if (nx >= 0 && nx < 3 && ny >= 0 && ny < 3) {
                    //Expand state
                    Node node = expand(curNode, nx, ny);
                    //Enqueue neighbor State
                    if (!visited.contains(getHash(node))) {
                        queue.offer(node);
                    }
                }
            }
        }
        return false;
    }
    //Compute Misplaced Title
    public int getMisplacedTilte(char[][] state) {
        int res = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (state[i][j] != 'b' && state[i][j] != goalState[i][j]) {
                    res++;
                }
            }
        }
        return res;
    }
    //Compute Manhattan Distance
    public int getManhattanDis(char[][] state) {
        int res = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (state[i][j] != 'b') {
                    int x = (state[i][j] - '0' - 1) / n;
                    int y = (state[i][j] - '0' - 1) % n;
                    res += Math.abs(x - i) + Math.abs(y - j);
                }
            }
        }
        return res;
    }
    //Check weather initial state is valid or not
    public boolean isInvalid(char[][] state) {
        char[][] invalidState = new char[][] {{'1', '2', '3'}, {'4', '5', '6'}, {'8', '7', 'b'}};
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (state[i][j] != invalidState[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
    //Get Hash code
    public String getHash(Node node) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                builder.append(node.state[i][j]);
            }
        }
        return builder.toString();
    }
    //Display state
    public void disPlay(Node node, int type) {
        System.out.println("Expanding State:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(node.state[i][j]);
            }
            System.out.println();
        }
        if (type == 1) {
            System.out.print("g(n):");
            System.out.print(this.gn + "\t");
            System.out.print("h(n):");
            System.out.println(0);
        } else if (type == 2) {
            System.out.print("g(n):");
            System.out.print(this.gn + "\t");
            System.out.print("h(n):");
            System.out.println(node.misplaced);

        } else if (type == 3) {
            System.out.print("g(n):");
            System.out.print(this.gn + "\t");
            System.out.print("h(n):");
            System.out.println(node.manhattan);
        }
        System.out.println("Max Queue Size:" + this.space);

    }
    //Expand current state to next state
    public Node expand(Node node, int x, int y) {
        char[][] state = new char[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                state[i][j] = node.state[i][j];
            }
        }
        char temp = state[node.x][node.y];
        state[node.x][node.y] = state[x][y];
        state[x][y] = temp;
        return new Node(state, getMisplacedTilte(state), getManhattanDis(state));
    }
    //Check if reaching the goal state
    public boolean goalTest(char[][] state) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (goalState[i][j] != state[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
    //Get input and do interaction with users
    public String getInput() {
        System.out.println("Welcome to Eight Puzzle.\nType 1 to use a default puzzle, type 2 to type your own puzzle.");
        //get state
        char[][] state = new char[n][n];
        Scanner scanner = new Scanner(System.in);
        if (!scanner.next().equals("1")) {
            System.out.println("Please input every row without space and using \"b\" as blank. ");
            for (int i = 0; i < n; i++) {
                String input = scanner.next();
                state[i] = input.toCharArray();
            }
            this.initialState = state;
        }
        //Select algorithm
        System.out.println("Enter your choice of algorithm:\n" +
                "1:Uniform Cost Search\n" +
                "2:A* with Misplaced Title heuristic\n" +
                "3:A* with Manhattan distance heuristic\n");
        String choice = scanner.next();
        return choice;
    }
    public static void main(String[] args) {
        EightPuzzle main = new EightPuzzle();
        String algorithm = main.getInput();
        switch(algorithm) {
            case "1":
                System.out.print(main.heuristicSearch(1));
                break;
            case "2":
                System.out.print(main.heuristicSearch(2));
                break;
            case "3":
                System.out.print(main.heuristicSearch(3));
        }
    }
}
//Node class, indicates state
class Node {
    char[][] state;
    int x;
    int y;
    int misplaced = 0;
    int manhattan = 0;
    Node(char[][] state, int misplaced, int manhattan) {
        this.state = state;
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[0].length; j++) {
                if (state[i][j] == 'b') {
                    x = i;
                    y = j;
                    break;
                }
            }
        }
        this.misplaced = misplaced;
        this.manhattan = manhattan;
    }
}
