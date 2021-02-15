import javax.swing.*;



import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;



public class PathFinder extends JFrame {
	Container c;
	
	Node startNode;
	Node endNode;
	
	JToggleButton start;
	JToggleButton goal;
	JToggleButton barr;
	JButton res;
	JButton startButton;
	
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
	
	
	public PathFinder(int n) {
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
				if (field[i][j].isStart() ||field[i][j].isGoal() || field[i][j].isBarrier()) 
					continue;
				field[i][j].reset();
				started = false;
			}
		}
	}
	public JPanel generateButtons() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1,4));
		start = new JToggleButton("draw the Start Node");
		goal = new JToggleButton("draw the Goal Node");
		barr = new JToggleButton("draw a Barrier");
		res = new JButton("reset");
		startButton = new JButton("startDraw");
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
				drawStart = tmp.getText().equals("draw the Start Node");
				drawGoal = tmp.getText().equals("draw the Goal Node");
				drawBarrier = tmp.getText().equals("draw a Barrier");
			} else {
				if (tmp.getText().equals("draw the Start Node")) {
					goal.setSelected(false);
					barr.setSelected(false);
					drawStart = false;
				}
				if (tmp.getText().equals("draw the Goal Node")) {
					start.setSelected(false);
					barr.setSelected(false);
					drawGoal = false;
				}
				if (tmp.getText().equals("draw a Barrier")) {
					start.setSelected(false);
					goal.setSelected(false);
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
					first.hScore();
					second.hScore();
					return first.hScore - second.hScore;
				}
			}
		};
	
	    priority = new PriorityQueue(20, nodeComparator); //Sorted Queue with all the Nodes that have to be evaluated
		explored = new ArrayList<Node>(); //Stores the Nodes that we have already explored
		
	
		started = true;
		
		int counter = 0;
		priority.add(startNode); //Add the Start Node as the first Node in the Queue

		while(!priority.isEmpty()) {
			Node current = priority.poll();
			current.updateNeighbors();
			explored.add(current);
			
			if (current.isGoal()) {
				started = false;
				current.drawEdge(); //If we have found the goal, we draw a path to the StartNode
				return;
			}
			
			for (Node neigh : current.neighs) {
				if (neigh.isBarrier() || explored.contains(neigh)) {
					continue;
				}
				
				int newGScore = current.gScore + current.getDistance(neigh); //Calculate the new G Score with the G Score of the Mother an the Distance we have to go
				if (newGScore < neigh.gScore || !priority.contains(neigh)) {
					neigh.gScore = newGScore;
					neigh.hScore();
					neigh.mother = current;
					
					if (!priority.contains(neigh)) {
						priority.add(neigh);
						explored.add(neigh):
						counter++;
					}
					explored.get(counter).setOpen();
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
			hScore = getDistance(endNode);
		}
		
		public int getDistance(Node node) {
			int dstX = Math.abs(node.x - this.x);
			int dstY = Math.abs(node.y - this.y);

			if (dstX > dstY)
				return 14*dstY + 10* (dstX-dstY);
			return 14*dstX + 10 * (dstY-dstX);
		}
		
		public void updateG() {
			hScore();
			if (mother.gScore + getDistance(mother) < gScore) 
				this.gScore = mother.gScore + getDistance(mother);
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
			
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (i == 0 && j == 0) 
						continue;
					int cX = this.x + i;
					int cY = this.y + j;
					
					if (cX >= 0 && cX < n && cY >= 0 && cY < n && !field[x+i][y+j].isBarrier()) {
						neighs.add(field[x+i][y+j]);
						
					}
					
				}
			}
			
		}
		
		//Re - construct the way
		public void drawEdge() {
			Node current = endNode;
			
			System.out.println("Ziel erreicht");
			
			while(!current.mother.isStart() && current.mother != null) {
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
		PathFinder finder = new PathFinder(20);
		System.out.println();
		finder.setVisible(true);
	}
}
