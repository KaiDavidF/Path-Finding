import javax.swing.*;



import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;



public class RealPathFinder extends JFrame {
	Container c;
	
	Node startNode;
	Node endNode;
	
	Node [][] field;
	JPanel background;
	private boolean drawStart = true;
	private boolean drawGoal;
	private boolean drawBarrier;
	private boolean started;
	int n;
	PriorityQueue <Node> priority;
	ArrayList<Node> explored;
	ArrayList<Node> way;
	
	
	public RealPathFinder(int n) {
		this.n = n;
		field = new Node[n][n];
		
		c = getContentPane();
		c.add(generateField(n), BorderLayout.CENTER);
		c.add(generateButtons(), BorderLayout.SOUTH);
		this.setSize(new Dimension(1000, 1000));
		this.setResizable(false);
	}
	
	
	public JPanel generateField(int n) {
		
		background = new JPanel();
		background.setLayout(new GridLayout(n,n,-1,-1));
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				field[i][j] = new Node(i,j);
				field[i][j].reset();
				background.add(field[i][j].getButton());
			}
		}
		return background;
	}
	public void resetField () {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {	
				field[i][j].reset();
				started = false;
			}
		}
	}
	public JPanel generateButtons() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1,4));
		JToggleButton start = new JToggleButton("start");
		JToggleButton goal = new JToggleButton("goal");
		JToggleButton barr = new JToggleButton("barrier");
		JToggleButton res = new JToggleButton("reset");
		JButton startButton = new JButton("startDraw");
		ToggleListener tL = new ToggleListener();
		start.addActionListener(tL);
		goal.addActionListener(tL);
		barr.addActionListener(tL);
		res.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetField();
			}
		});
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (startNode != null && endNode != null)
					drawAStar();
				
			}
		});
		
		panel.add(res);
		panel.add(start);
		panel.add(goal);
		panel.add(barr);
		panel.add(startButton);
		return panel;
	}
	
	class ToggleListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JToggleButton tmp = (JToggleButton) e.getSource();
			if (tmp.isSelected()) {
				drawStart = tmp.getText().equals("start");
				drawGoal = tmp.getText().equals("goal");
				drawBarrier = tmp.getText().equals("barrier");
			} else {
				if (tmp.getText().equals("start")) {
					drawStart = false;
				}
				if (tmp.getText().equals("goal")) {
					drawGoal = false;
				}
				if (tmp.getText().equals("barrier")) {
					drawBarrier = false;
				}
			}
			
			
		}
	}
	public void drawAStar() {
		/*Define the Comparator for the Priority Queue: 
		 * Always put the Node with the lowest F-Score first.
		 * If both Nodes have the same F-Score, take the one that
		 * has the lowest heuristic distance to the End-Node.*/
		
		Comparator<Node> nodeComparator = new Comparator<Node>() {
			@Override
			public int compare(Node first, Node second) {
				first.getFScore();
				second.getFScore();
				if (first.fScore != second.fScore) {
					return first.fScore - second.fScore;
				}
				else {
					return first.hScore - second.hScore;
				}
			}
		};
	
	    priority = new PriorityQueue(20, nodeComparator); //Sorted Queue with all the Nodes that have to be evaluated
		explored = new ArrayList<Node>(); //Stores the Nodes that we have already explored
		
	
		started = true;
		Node current = startNode; //Current Node at the beginning is the Start-Node
		
		priority.add(startNode); //Add the Start Node as the first Node in the Queue
		while (!priority.isEmpty()) { //While the Priority Queue is not empty
			
			current = priority.poll(); //Get the best Node
			explored.add(current);     //Add it to the explored List
			if (current.isGoal()) {
				current.drawEdge(); //return true
				break;
			}
			current.updateNeighbors(); //Otherwise the Node has no neighbors
			
			for (Node neighbor : current.neighs) {
					
				int newCost = current.gScore + 1;
					if (!explored.contains(neighbor) || newCost < neighbor.gScore) {
						current.setOpen();
						neighbor.gScore = newCost;
						neighbor.hScore();
						neighbor.mother = current;
						neighbor.updateG();
						if (!explored.contains(neighbor)) {
							priority.add(neighbor);
						}
					}
			}
		}
	}
		
	
	
	
	class Node {
		int fScore;
		int gScore;
		int hScore;
		int x,y;
		Node mother;
		ArrayList<Node>neighs; //Neighbours
		Color col; 			   //Colour of Button
		JButton button;
		
		
		public Node(int x,int y) {
			this.x = x;
			this.y = y;
			button = new JButton();
			button.addActionListener(new NodeListener());
			this.col = Color.white;
		}
		
		public void hScore() {
		
			hScore =  Math.abs(x - endNode.x) + Math.abs(y - endNode.y);
		}
		public void updateG() {
			hScore();
			this.gScore = mother.gScore + 1;
		}
		public int getFScore() {
			return gScore + hScore;
		}
		
		
		//Getter
		public JButton getButton () {
			return button;
		}
		public boolean isBarrier () {
			return col == Color.BLACK;
		}
		public boolean isGoal() {
			return col == Color.RED;
		}
		public boolean isStart() {
			return col == Color.GREEN;
		}
		public boolean isOpen() {
			return col == Color.BLUE;
		}
		public boolean isClosed() {
			return col == Color.ORANGE;
		}
		
		
		//Setter
		public void setOpen () {
			this.col = Color.blue;
			button.setBackground(col);
		}
		public void setStart() {
			this.col = Color.GREEN;
			startNode = this;
			button.setBackground(col);
		}
		public void setGoal() {
			this.col = Color.RED;
			endNode = this;
			button.setBackground(col);
		}
		public void setBarrier() {
			this.col = Color.black;
			button.setBackground(col);
		}
		public void reset() {
			this.col = Color.white;
			button.setBackground(col);
		}
		public void drawPath() {
			this.col = Color.cyan;
			button.setBackground(col);
		}
		public void setClosed() {
			this.col = Color.orange;
			button.setBackground(col);
		}
		
		//Update the neighbors of a Node
		
		public void updateNeighbors() {
			this.neighs = new ArrayList<Node>();
			if (y < n-1 && !field[x][y+1].isBarrier() ) { //Check right
				neighs.add(field[x][y+1]);
			}
			if (y > 0 && !field[x][y-1].isBarrier()) { //Check left; Check if it is still in
				neighs.add(field[x][y-1]);	
			}
			if (x > 0 && !field[x-1][y].isBarrier()) { //Check up coord begin at top left 
				neighs.add(field[x-1][y]);
			}
			if (x < n-1 && !field[x+1][y].isBarrier()) { //Check down
				neighs.add(field[x+1][y]);
				
			}
		}
		
		//Re - construct the way
		public void drawEdge() {
			Node current = this;
			while(!current.mother.equals(startNode)) {
				current = current.mother;
				current.drawPath();
			}
		}
			

		
		class NodeListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if (!started) {
					if (drawStart) {
						if (startNode != null) 
							startNode.reset();
						setStart();
					}
					if (drawGoal && !isStart()) {
						if (endNode != null) {
							endNode.reset();
						}
						setGoal();
					}
					if (drawBarrier && !isStart() && !isGoal()) {
						setBarrier();
					}
					else if (Node.this.isBarrier()) {
						Node.this.reset();
					}
					
				}
			}
		} 
	}
	
	public static void main(String[] args) {
		RealPathFinder finder = new RealPathFinder(20);
		System.out.println();
		finder.setVisible(true);
	}
}
