# Summer of Bitcoin 

This repository consists of the solution to the summer of bitcoin challange. 

## Problem Statement

[Problem Statement](https://drive.google.com/drive/folders/12iqp3CvEPSIVgUIgAaJWe6fVKCL3-Y15?_hsmi=133809050&_hsenc=p2ANqtz-9vbcjhSKpq58j3nutFGIA4UUS9h-vgjoXA56Y0EpMim0nxM8uqzJKbc6UovDVPIjhJ9cKzYKGQfXg8vPFeOZ3nG5oR8g)

Problem Statement Explanation : We have a few transactions (let us call these as nodes) which have a fee, weight and parents list(nodes) associated with it. We have to find a block which have the maximum fees of all possible blocks with weight less than 4000000. 

### Idea

Now In order to solve this problem we should first break this problem into sub problems, in which our first task would be to process the input file data and store it into an appropriate data structure.

Now we need to observe the input data, we can see each input has a transaction Id ,fee, weight and list of transaction Ids, Now from this we can observe that it looks like a graph kind of structure (adjacency List).

Now for a transaction Id we have a list of transactions Ids which should occur before this transaction if we are including this transaction into consideration, Now if we take a transaction as a node of graph then this dependency shows that a node can be only accessed if we have already visited its parent nodes, which in turn gives us intution of topological sort. 

Now this problem reduces to find the maximum fee in an array with minimum weight sum which looks similar to the traditional knapsack problem the addtional condition is just we have to keep track of parent nodes of current nodes as well whether they are visited or not.

## Algorithm

#### Approach 1 (Not Efficient)

- First observation : The problem statement seems somewhat similar to 0/1 knapsack where each node have two choices, either contribute to result or not. Inclusion is possible only when the current node's parents are also included in block and the current block's weight + current node's weight is less than the given max weight.
- maintain an order of the transactions such that the parent transaction is done before the child transaction using topological sort.
- This solution is correct but has a complexity of (2 ^ n), where n is the total number of transaction in data file. So this is not time efficient.
- Even if we try to use dynamic programming in this still it won't be efficient because total weight is 4000000 and we have to check the parent's dependency so dynamic programming (tabulation method) is not a good option.

#### Approach 2 (Efficient)

- What we can do is that we can set a threshold value (or cutOff point) based on the fee/weight ratio of all the transactions. 
    - We can traverse through the topological sorted nodes and if the fee/weight ratio of the current node is less than the cutoff ratio then we can include this node. (Only when all the parents are also included and the weight contraint is met).
    - This will give us the most optimal result (required block).

## Solution : [Solution file](https://github.com/jnv27/Summer-of-bitcoin/blob/main/Solution.java)

## Output

### Weight and Fee of the current block

![image](https://github.com/jnv27/Summer-of-bitcoin/blob/main/output.jpeg)

### Block file

[Block.txt](https://github.com/jnv27/Summer-of-bitcoin/blob/main/block.txt)
