import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

interface BoggleSolver {
    List<String> solve(Board board);
}

class GeneratePossibleWordsSolver implements BoggleSolver {
    public List<String> solve(Board board) {
        return null;
    }
}

class FindWordsFromDictionarySolver implements BoggleSolver {
    public List<String> solve(Board board) {
        List<String> words = readDictionary();
        List<String> foundWords = new ArrayList<String>();

        for (String word : words) {
            if (findWord(word, board)) {
                foundWords.add(word);
            }
        }

        return foundWords;
    }

    private boolean findWord(String word, Board board) {
        List<Node> startingNodes = findStartingNodes(board, word);

        for (Node startingNode : startingNodes) {
            Set<Position> visited = new HashSet<Position>();

            visited.add(startingNode.getPosition());

            if (searchForWord(1, word, board, startingNode, visited)) {
                return true;
            }
        }

        return false;
    }

    private List<Node> findStartingNodes(Board board, String word) {
        List<Node> startingNodes = new ArrayList<Node>();

        for (Node node : board.getNodes()) {
            if (node.getValue() == word.charAt(0)) {
                startingNodes.add(node);
            }
        }

        return startingNodes;
    }

    public boolean searchForWord(int index, String word, Board board, Node currentNode, Set<Position> visited) {
        if (index == word.length()) return true;

        List<Node> neighbours = new ArrayList<Node>();

        for (Position neighbour : currentNode.getNeighbours()) {
            neighbours.add(positionToNode(neighbour, board));
        }

        List<Node> candidates = new ArrayList<Node>();

        for (Node candidate : neighbours) {
            if (visited.contains(candidate.getPosition())) continue;
            if (candidate.getValue() == word.charAt(index)) {
                candidates.add(candidate);
            }
        }

        for (Node node : candidates) {
            visited.add(node.getPosition());
            if (searchForWord(index + 1, word, board, node, visited)) return true;
            visited.remove(node.getPosition());
        }

        return false;
    }

    private Node positionToNode(Position position, Board board) {
        for (Node node : board.getNodes()) {
            if (position.equals(node.getPosition())) return node;
        }

        assert (false); // should never happen

        return null;
    }

    private List<String> readDictionary() {
        String word;
        List<String> words = new ArrayList<String>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("dict.txt"));

            while ((word = bufferedReader.readLine()) != null) {
                words.add(word);
            }

            bufferedReader.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return words;
    }
}

class Position {
    private final int row, column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj instanceof Position) {
            Position pos = (Position) obj;
            if (pos.getRow() == row && pos.getColumn() == column) {
                return true;
            }
        }
        
        return false;
    }
}

class Node {
    private final char value;
    private final Position position;
    private final List<Position> neighbours;

    public Node(char value, Position position, List<Position> neighbours) {
        this.value = value;
        this.position = position;
        this.neighbours = neighbours;
    }

    public char getValue() {
        return value;
    }

    public Position getPosition() {
        return position;
    }

    public List<Position> getNeighbours() {
        return neighbours;
    }
}

class Board {
    private final List<Node> nodes;

    public Board(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Node> getNodes() {
        return nodes;
    }
}

class Boggle {
    private BoggleSolver solver;
    private Board board;

    public Boggle(String input) {
        board = buildBoard(input);
        solver = new FindWordsFromDictionarySolver();
    }

    private Board buildBoard(String input) {
        List<Node> nodes = new ArrayList<Node>(input.length());

        int dimension = (int) Math.sqrt(input.length());
        assert (dimension * dimension == input.length()); // TODO: requires correct input handling

        for (int i = 0, sz = input.length(); i < sz; i++) {
            nodes.add(buildNode(input.charAt(i), new Position(i / dimension, i % dimension), dimension));
        }

        return new Board(nodes);
    }

    private Node buildNode(char value, Position position, int dimension) {
        return new Node(value, position, getNeighbours(position, dimension));
    }

    private List<Position> getNeighbours(Position position, int dimension) {
        int[] directions = {-1, 0, 1};
        List<Position> neighbours = new ArrayList<Position>();

        for (int rowDiff : directions) {
            for (int colDiff : directions) {
                Position neighbour = new Position(position.getRow() + rowDiff, position.getColumn() + colDiff);

                if (!position.equals(neighbour) && validPosition(neighbour, dimension)) {
                    neighbours.add(neighbour);
                }
            }
        }

        return neighbours;
    }

    private boolean validPosition(Position position, int dimension) {
        return inRange(position.getRow(), dimension) && inRange(position.getColumn(), dimension);
    }

    private boolean inRange(int x, int dimension) {
        return x >= 0 && x < dimension;
    }

    private Position positionFromPair(int row, int column) {
        return new Position(row, column);
    }

    public void solve() {
        for (String foundWord : solver.solve(board)) {
            if (foundWord.length() < 3) continue;
            System.out.print(foundWord + " ");
        }

        System.out.println();
    }

    public static void main(String[] args) {
        assert (args.length > 0); // TODO: requires correct input handling
        new Boggle(args[0]).solve();
    }
}