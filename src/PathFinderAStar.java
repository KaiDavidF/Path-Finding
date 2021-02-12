import javax.swing.*;

import PathFind.Node;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;



public class PathFinderAStar extends JFrame {
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
	int count = 0;
	
	
	public PathFinderAStar(int n) {
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
	public JPanel generateButtons() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1,3));
		JToggleButton start = new JToggleButton("start");
		JToggleButton goal = new JToggleButton("goal");
		JToggleButton barr = new JToggleButton("barrier");
		JButton startButton = new JButton("startDraw");
		ToggleListener tL = new ToggleListener();
		start.addActionListener(tL);
		goal.addActionListener(tL);
		barr.addActionListener(tL);
		
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawAStar();
			}
		});
		
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
				first.updateF();
				second.updateF();
				if (first.fScore > second.fScore) {
					return 1;
				}
				else if (first.fScore < second.fScore) {
					return -1;
				}
				else {
					if (first.hScore() > second.hScore()) {
						return 1;
					} else {
						return -1;
					}
				}
			}
		};
		
		PriorityQueue <Node> priority = new PriorityQueue(50, nodeComparator);
		ArrayList<Node> explored = new ArrayList<Node>();
		
		started = true;
		Node current = startNode;
		priority.add(startNode); //Add the Start Node as the first Node in the Queue
		
		while (!priority.isEmpty()) {
			current = priority.poll(); //Get the best Node
			explored.add(current);
			int tmpGScore = current.gScore;
			
			if (current.equals(;))
		}
		
	}
	
	
	class Node {
		int fScore;
		int gScore;
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
		
		public int hScore() {
			return Math.abs(x - endNode.x) + Math.abs(y - endNode.y);
		}
		public void updateG() {
			this.gScore = mother.gScore + 1;
		}
		public void updateF() {
			updateG();
			this.fScore = gScore + hScore();
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
		
		//Update the neighbours of a Node
		
		public void updateNeighbours() {
			this.neighs = new ArrayList<Node>();
			if (y < n-1 && !field[x][y+1].isBarrier()) { //Check right
				neighs.add(field[x][y+1]);
				field[x][y+1].explore(this);
	
			}
			if (y > 0 && !field[x][y-1].isBarrier()) { //Check left; Check if it is still in
				neighs.add(field[x][y-1]);
				field[x][y-1].explore(this);
				
			}
			if (x > 0 && !field[x-1][y].isBarrier()) { //Check up coord begin at top left 
				neighs.add(field[x-1][y]);
				field[x-1][y].explore(this);
				
			}
			if (x < n-1 && !field[x+1][y].isBarrier()) { //Check down
				neighs.add(field[x+1][y]);
				field[x+1][y].explore(this);
			}
		}
		
		public void explore(Node mother) {
			if (!this.equals(startNode) && !this.equals(endNode)) {
				setOpen();
			}
			this.mother = mother; //Keep track were the Node originated from
			if (this.equals(endNode)) {
				this.findWay();
			}
		}
		//Re - construct the way
		public void findWay() {
			Node current = this;
			while (!current.equals(startNode)) {
				current.drawPath();
				current = current.mother;
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
						System.out.println("Barrier");
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
		PathFinderAStar finder = new PathFinderAStar(20);
		finder.setVisible(true);
	}
}
