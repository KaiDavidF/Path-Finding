import javax.swing.*;

import RealPathFinder.Node;
import RealPathFinder.ToggleListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;


public class PathFind extends JFrame {
	Container c;
	Node [][] field;
	JPanel background;
	private boolean drawStart = true;
	private boolean drawGoal;
	private boolean drawBarrier;
	private boolean started;
	int n;
	int count = 0;
	
	
	
	public void pause() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public PathFind(int n) {
		this.n = n;
		c = getContentPane();
		field = new Node[n][n];
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
		Comparator<Node> nodeComparator = new Comparator<Node>() {
			
			@Override
			public int compare(Node last, Node newNode) {
				if (last.fScore > newNode.fScore) {
					return 1;
				}
				else if (last.fScore < newNode.fScore) {
					return -1;
				}
				else {
					return last.hCost() - newNode.hCost(); //Both have the same F Score
				}
			}
		};
		
		PriorityQueue <Node> priority = new PriorityQueue(50, nodeComparator);
		ArrayList<Node> explored = new ArrayList<Node>();
		
		
		started = true;
		Node current = startNode;
		priority.add(startNode);
		while(!priority.isEmpty()) {
			
			current = priority.poll();
			explored.add(current);
			int tmpGScore = current.gScore;
			if (current.equals(endNode)) {
				break; //return true
			}
			current.updateNeighbours(field);
			for (Node neighbour : current.neighs) {
				if (tmpGScore < neighbour.gScore) {
					neighbour.mother = current; //Update the mother of the neighbour
					neighbour.gScore = tmpGScore;
					neighbour.fScore = tmpGScore + neighbour.hCost();
					//Calculate the new F Score of the neighbour
					if (!explored.contains(neighbour)) {
						priority.add(neighbour);
						explored.add(neighbour);
						neighbour.setOpen();
	
					}
					
				}
			}
		}
		
		
	}

	
	
	
	Node startNode;
	Node endNode;
	//Node Class
	class Node {
		int fScore;
		int gScore;
		int x, y;
		Node mother;
		ArrayList<Node>neighs;
		
		Color col;
		JButton button;
		
		public Node(int x, int y) {
			this.x = x;
			this.y = y;
			button = new JButton();
			button.addActionListener(new NodeListener());
			this.col = Color.white;
		}
		
		
		
		
		public int hCost() {
			return  Math.abs(x - endNode.x) + Math.abs(y - endNode.y);
		}
		public void calcgScore() {
			this.gScore = mother.gScore + 1; 
		}
		public void calcFScore() {
			calcgScore();
			this.fScore = gScore + hCost();
		}
		
		
		
		
		public JButton getButton() {
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
		
		
		//Updating the neighbours of a Node
		public void updateNeighbours(Node [][] field) {
	
			
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
		
		public void explore(Node mother) { //Updating a certain Node
			if (!this.equals(startNode) && !this.equals(endNode))  //Check if it is not the start or the end Node
				setOpen();
			this.mother = mother;
			calcFScore(); //Update the F Score
			if (this.equals(endNode)) {
				this.findWay();
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
		
		//Draws the way from end to start
		public void findWay () {
			Node current = this;
			int xy = 100;
			while (xy > 80) {
				System.out.println(current.y);
				current.drawPath();
				current = current.mother;
				xy--;
				
			}
			started = false;
			
		}
	}
	
	
	public static void main(String[] args) {
		PathFind finder = new PathFind(40);
		finder.setVisible(true);
	}
}
