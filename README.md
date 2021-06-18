# Summer of Bitcoin 

This repository consists of the solution to the summer of bitcoin challange. 

## Problem Statement

[Problem Statement](https://drive.google.com/drive/folders/12iqp3CvEPSIVgUIgAaJWe6fVKCL3-Y15?_hsmi=133809050&_hsenc=p2ANqtz-9vbcjhSKpq58j3nutFGIA4UUS9h-vgjoXA56Y0EpMim0nxM8uqzJKbc6UovDVPIjhJ9cKzYKGQfXg8vPFeOZ3nG5oR8g)

## Solution Approach [Solution file](https://github.com/jnv27/Summer-of-bitcoin/blob/main/Solution.java)

Problem Statement Explanation -> We have a few transactions (let us call these as nodes) which have a fee, weight and parents list(nodes) associated with it. We have to find a block which have the maximum fees of all possible blocks with weight less than 4000000. 

#### Approach 1 (Not Efficient)

- First observation : The problem statement seems somewhat similar to 0/1 knapsack where each node have two choices, either contribute to result or not. Inclusion is possible only when the current node's parents are also included in block and the current block's weight + current node's weight is less than the given max weight.
- For this we have to maintain an order of the transactions such that the parent transaction is done before the child transaction. For this we can use topological sorting to find the order.
- This solution is correct but has a complexity of (2 ^ n), where n is the total number of transaction in data file. So this is not efficient.

#### Approach 2 (Efficient)

- Since the total weight is 4000000 and we have to check the parent's dependency dynamic programming (tabulation method) is not a good option.
- So I came up with another solution based on the Greedy Method.
- What we can do is that we can set a threshold value (or cutOff point) based on the fee/weight ratio of all the transactions. 
    - We can traverse through the topological sorted nodes and if the fee/weight ratio of the current node is less than the cutoff ratio then we can include this node. (Only when all the parents are also included and the weight contraint is met).
    - This will give us the most optimal result (required block).


## Output

### Weight and Fee of the current block

![image](https://github.com/jnv27/Summer-of-bitcoin/blob/main/output.jpeg)

### Block file

[Block.txt](https://github.com/jnv27/Summer-of-bitcoin/blob/main/block.txt)
