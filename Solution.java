import java.io.*;
import java.util.*;

// Node Class 

class Node {
	String id;
	long wt;
	long fee;
	List<String> parentsId;

	// constructors
	public Node(String id, long fee, long wt, List<String> parentsId) {
		this.id = id;
		this.fee = fee;
		this.wt = wt;
		this.parentsId = parentsId;
	}

	public Node(String id, long fee, long wt) {
		this.id = id;
		this.fee = fee;
		this.wt = wt;
		this.parentsId = new ArrayList<>();
	}	
}

public class Solution {
		
	private Map<String, Node> map;
	private List<Node> fileData;
	private Map<Node, Set<Node>> cpList;
	private Map<Node, Set<Node>> adj;
	private List<Node> topList;

	// creating Nodes based on the file data
	private void fetchTransactions(String src) {
		this.fileData = new ArrayList<>();
		this.map = new HashMap<>();
		Scanner s = null;
		try {
			s = new Scanner(new File(src));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		int line = 0;
		while (s.hasNext()) {
			if (line == 0) {
				s.nextLine();
				line++;
			}

			String[] input = s.nextLine().split(",");
			String id = input[0];
			long fee = Long.parseLong(input[1]);
			long wt = Long.parseLong(input[2]);

			List<String> parentsId = new ArrayList<>();
			if (input.length > 3) {
				String[] pids = input[3].split(";");
				for (String pi : pids)
					parentsId.add(pi);
			}

			Node curr_node = new Node(id, fee, wt, parentsId);
			this.fileData.add(curr_node);
			this.map.put(id, curr_node);
		}
	}

	// creating our graph => node : all it's ancestors
	private void bfs() {
		cpList = new HashMap<>();
		for(Node node : this.fileData){
			Queue<Node> queue = new LinkedList<>();
			Set<Node> visited = new HashSet<>();

			cpList.put(node, new HashSet<>());
			
			for(String ss : node.parentsId){
				queue.add(map.get(ss));
			}

			while(!queue.isEmpty()){
				Node cn = queue.remove();
				visited.add(cn);
				cpList.get(node).add(cn);
				for(String ss : cn.parentsId){
					if(!visited.contains(map.get(ss)))
						queue.add(map.get(ss));
				}
			}
		}
	}

	// creating adjancy list
	private void createAdjList() {
		adj = new HashMap<>();

		for(String ss : map.keySet()){
			adj.put(map.get(ss), new HashSet<>());
		}

		for(Node child : cpList.keySet()){
			for(String pn : child.parentsId){
				Node parent = map.get(pn);
				if(!adj.containsKey(parent))
					adj.put(parent, new HashSet<>());
				adj.get(parent).add(child);
			}
		}
		
		for(Node node : adj.keySet()){
			Queue<Node> queue = new LinkedList<>();
			for(Node cn : adj.get(node))
				queue.add(cn);
			while(!queue.isEmpty()){
				Node cn = queue.remove();
				adj.get(node).add(cn);				
				for(Node child : adj.get(cn))
					queue.add(child);
			}
		}

	}

	/*********************** Topological Sort Fuction *****************************
		I'm using kahn's algorithm for finding the processing order of the nodes 
	    We have to process our parent nodes before the child nodes
	********************************************************************************/

	private void topoSort(){
		this.topList = new ArrayList<>();
		Map<Node, Integer> indegree = new HashMap<>();
		Queue<Node> queue = new LinkedList<>();

		for(Node node : cpList.keySet()){
			indegree.put(node, cpList.get(node).size());
			if(cpList.get(node).size() == 0)
				queue.add(node);
		}

		while(!queue.isEmpty()){
			Node node = queue.remove();
			this.topList.add(node);

			for(Node cn : adj.get(node)){
				indegree.put(cn, indegree.get(cn) - 1);
				if(indegree.get(cn) <= 0)
					queue.add(cn);
			}
		}

	}

	private long ans;
	private long maxwt;
	private long ansBlockWt;
	private List<String> path;

	/********************************************************************************************************************
		I'm using greedy approach, In which finding the optimal threshold point at which we will get the 
		maximum fee within the weight limit. For this optimal threshold point (or avg point)
			-> Find the extremes of the ratio 
			-> I'm assuming 
				-> the avg of fee/weight of whole data as the higher extreme.
				-> 0.0 as lower extreme.
			-> Now find the optimal cutOff point using linear traversal
				-> I'm using linear traversal (with precision 0.1) 
				-> We can improve this by having more precision.
			-> Traverse through topological sorted list (in which the parents come before the child transactions)
				and if the fee/wt ratio of current node is less than threshold and it's all parent transactions 
				are already included then take this in the current block if the wt permits.
			-> Finally print the best optimal result
	
	********************************************************************************************************************/

	private void greedyApproach(double avg, Set<Node> visited){
		List<String> currPath = new ArrayList<>();
		long currwt = 0;
		long currFee = 0;

		for(Node node : topList){
			double cratio = node.fee / (node.wt * 1.0);
			
			// check if parents are visited or not
			Set<Node> parents = cpList.get(node);
			int count = 0;
			for(Node pn : parents){
				if(visited.contains(pn))
					count++;
			}

			if(cratio >= avg && count == parents.size() && currwt + node.wt < maxwt){
				visited.add(node);
				currFee += node.fee;
				currwt += node.wt;
				currPath.add(node.id);
			}
		}

		if(currFee > ans){
			ans = currFee;
			path = new ArrayList<>(currPath);
			ansBlockWt = currwt;
		}
	}

	private void writeToFile(String filename) throws IOException {
		FileWriter fileWriter = new FileWriter(filename);
    	PrintWriter printWriter = new PrintWriter(fileWriter);
		for(String ss : path){
			printWriter.print(ss + "\n");
		}
		printWriter.close();
	}

	public static void main(String[] args) throws IOException {
		Solution sol = new Solution();
		// This path may vary
		String src = "C:\\Users\\ambrish\\Desktop\\Summer-of-Bitcoin\\mempool.csv";
		sol.fetchTransactions(src);
		sol.bfs();
		sol.createAdjList();
		sol.topoSort();

		/* ============= Resultant Block's Properties Initialization ============= */

		sol.ans = 0;					// fees of the block
		sol.ansBlockWt = 0;				// weight of the block
		sol.maxwt = 4000000;		// max-weight allowed
		sol.path = new ArrayList<>();	// complete block
		
		// FINDING EXTREME THRESHOLD (I'M ASSUMING IT TO BE AVG. OF fee/weight across all ndoes)

		double avg = 0.0;
		for(Node node : sol.fileData){
			avg += node.fee / (node.wt * 1.0);
		}

		avg = avg / sol.fileData.size();

		for(double i = 0.0; i <= avg; i += 0.1){
			sol.greedyApproach(i, new HashSet<>());
		}

		System.out.println("Maximum Fees generated through transaction in block.txt : " + sol.ans);
		System.out.println("Maximum weight generated through transaction in block.txt : " + sol.ansBlockWt + '\n');
		sol.writeToFile("block.txt");
	}

}