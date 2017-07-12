import java.awt.Color;
import java.util.*; 

class Player161214999 extends GomokuPlayer {
  private ArrayList<Move> alreadyExists;
	public Move chooseMove(Color[][] board, Color me) {
    ArrayList<Move> moves = getNextMoves(board); //get all open spaces that neighbor players' tiles
    if (isEmpty(board)) { //if it is the start of the game, place a tile in the middle of the board
      return new Move(4, 4);
    }
    ArrayList<Color[][]> boards = new ArrayList<>();
    for (int i = 0; i < moves.size(); i++) { //for each move, create a copy of the current board and place this move on the board
      Color[][] copy = copyBoard(board);
      copy[moves.get(i).row][moves.get(i).col] = me;
      boards.add(copy);
    }

    //determine opponent's color
    Color opp;
    if (me == Color.black) opp = Color.white;
    else opp = Color.black;

    Color[][] bestBoard = null;
    int bestScore = Integer.MIN_VALUE;
    boolean foundWinningBoard = false;
    for (Color[][] b : boards) {
      if (isWinningBoard(b, me)) { //if it can win in the next move, immediately place that tile
        bestBoard = b;
        foundWinningBoard = true;
        break;
      }
    }
    if (!foundWinningBoard) { //if it can block the opponent's win in the next move, immediately place that tile
      for (Color[][] b : boards) {
        Move changedMove = blockOpponent(board, b); //gets the difference
        Color[][] bOpp = copyBoard(b);
        if (changedMove != null) bOpp[changedMove.row][changedMove.col] = opp;
        if (isWinningBoard(bOpp, opp)) {
          foundWinningBoard = true;
          return blockOpponent(board, b);
        }
      }
    }
    if (!foundWinningBoard) { //if none of those cases are true, do minimax
      for (Color[][] b : boards) {
        int score = minimax(b, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, me, false);
        if (score > bestScore) {
          bestScore = score;
          bestBoard = b;
        }
      }
    }
    //return the best move
    int loc = boards.indexOf(bestBoard);
    return moves.get(loc);
  }

  public int minimax(Color[][] board, int depth, int alpha, int beta, Color me, boolean maximisingPlayer) {
    int bestValue;
    Color oppColor;
    if (me == Color.black) {
      oppColor = Color.white;
    }
    else oppColor = Color.black;

    //base cases
    if (isWinningBoard(board, me)) { //if the player wins
      return Integer.MAX_VALUE;
    }
    else if (isWinningBoard(board, oppColor)) { //if the opponent wins
      return Integer.MIN_VALUE;
    }
    else if (depth == 0) { //if the depth reaches 0, stop exploring
      return determineScore(board, me);
    }
    else {
      ArrayList<Move> moves = getNextMoves(board);
      if (maximisingPlayer) {
        bestValue = alpha;
        for (int i = 0; i < moves.size(); i++) {
          Color[][] c = copyBoard(board);
          c[moves.get(i).row][moves.get(i).col] = me;
          int childVal = minimax(c, depth - 1, bestValue, beta, me, false);
          bestValue = Math.max(bestValue, childVal);
          if (beta <= bestValue) {
            break;
          }
        }
      }
      else {
        bestValue = beta;
        for (int i = 0; i < moves.size(); i++) {
          Color[][] c = copyBoard(board);
          c[moves.get(i).row][moves.get(i).col] = oppColor;
          int childVal = minimax(c, depth - 1, alpha, bestValue, me, true);
          bestValue = Math.min(bestValue, childVal);
          if (bestValue <= alpha) {
            break;
          }
        }
      }
    }
    return bestValue;
  }

  public Move blockOpponent(Color[][] original, Color[][] child) {
     //called if the next board has a winning move for the opponent. gets the move the opponent would make to win
     //compares the original board with the child board, returns a move object of the tile that is placed
    for (int r = 0 ; r < GomokuBoard.ROWS; r++) {
    	for (int c = 0 ; c < GomokuBoard.COLS; c++) {
        if (original[r][c] != child[r][c]) {
          return new Move(r, c);
        }
      }
    }
    return null;
  }

  public boolean isWinningBoard(Color[][] board, Color winningColor) {
    for (int r = 0 ; r < GomokuBoard.ROWS; r++) {
    	for (int c = 0 ; c < GomokuBoard.COLS; c++) {
        if (checkForNumInARow(board, 5, r, c, 0, 1, winningColor)) {
            return true;
        }
        if (checkForNumInARow(board, 5, r, c, 1, 0, winningColor)) {
            return true;
        }
        if (checkForNumInARow(board, 5, r, c, 1, 1, winningColor)) {
            return true;
        }
        if (checkForNumInARow(board, 5, r, c, 1, -1, winningColor)) {
            return true;
        }
      }
    }

    return false;
  }

  public int determineScore(Color[][] board, Color me) {
    int[] scores = new int[4];
    for (int i = 0; i < scores.length; i++) {
      scores[i] = 0;
    }
    Color opp;
    if (me == Color.black) opp = Color.white;
    else opp = Color.black;

    int[] oppscores = new int[4];
    for (int i = 0; i < oppscores.length; i++) {
      oppscores[i] = 0;
    }
    for (int r = 0 ; r < GomokuBoard.ROWS; r++) {
    	for (int c = 0 ; c < GomokuBoard.COLS; c++) {
        for (int num = 1; num < 5; num++) {
          if (checkForNumInARow(board, num, r, c, 0, 1, me)) {
              scores[num - 1]++;
          }
          if (num != 1) {
            if (checkForNumInARow(board, num, r, c, 1, 0, me)) {
                scores[num - 1]++;
            }
            if (checkForNumInARow(board, num, r, c, 1, 1, me)) {
              scores[num - 1]++;
            }
            if (checkForNumInARow(board, num, r, c, 1, -1, me)) {
              scores[num - 1]++;
            }
          }
          if (checkForNumInARow(board, num, r, c, 0, 1, opp)) {
              oppscores[num - 1]++;
          }
          if (num != 1) {
            if (checkForNumInARow(board, num, r, c, 1, 0, opp)) {
                oppscores[num - 1]++;
            }
            if (checkForNumInARow(board, num, r, c, 1, 1, opp)) {
              oppscores[num - 1]++;
            }
            if (checkForNumInARow(board, num, r, c, 1, -1, opp)) {
              oppscores[num - 1]++;
            }
          }
        }
    	}
		}
    int posScore = scores[0] + scores[1] * 50 + scores[2] * 2500 + scores[3] * 100000;
    int negScore = oppscores[0] + oppscores[1] * 50 + oppscores[2] * 2500 + oppscores[3] * 100000;
    int score = posScore - negScore;
    return score;
  }


  public boolean checkForNumInARow(Color[][] board, int num, int r0, int c0, int dr, int dc, Color winningColor) {
    for (int k = 0 ; k < num ; k++) {
			int r = r0 + k*dr; //starting point
			int c = c0 + k*dc;
			if (r < 0 || c < 0 || r >= GomokuBoard.ROWS || c >= GomokuBoard.COLS || board[r][c] != winningColor) {
					return false;
			}
		}
		return true;
  }

  public ArrayList<Move> getNextMoves(Color[][] board) { //returns open spaces that neighbor either players' tiles
    ArrayList<Move> nextMoves = new ArrayList<>();
    ArrayList<String> tileLocs = getTileLocs(board);
    for (int i = 0; i < tileLocs.size(); i++) {
      String loc = tileLocs.get(i);
      int row = Integer.parseInt(loc.substring(0, 1));
      int col = Integer.parseInt(loc.substring(2));
      if (row > 0) {
        if (board[row - 1][col] == null) {
          Move newMove = new Move(row - 1, col);
          if (!containsMove(nextMoves, newMove)) nextMoves.add(new Move(row - 1, col));
        }
        if (col > 0) {
          if (board[row - 1][col - 1] == null) {
            Move newMove = new Move(row - 1, col - 1);
            if (!containsMove(nextMoves, newMove)) nextMoves.add(new Move(row - 1, col - 1));
          }
        }
        if (col < GomokuBoard.COLS - 1) {
          if (board[row - 1][col + 1] == null) {
            Move newMove = new Move(row - 1, col + 1);
            if (!containsMove(nextMoves, newMove)) nextMoves.add(new Move(row - 1, col + 1));
          }
        }
      }
      if (row < GomokuBoard.ROWS - 1) {
        if (board[row + 1][col] == null) {
          Move newMove = new Move(row + 1, col);
          if (!containsMove(nextMoves, newMove)) nextMoves.add(new Move(row + 1, col));
        }
        if (col < GomokuBoard.COLS - 1) {
          if (board[row + 1][col + 1] == null) {
            Move newMove = new Move(row + 1, col + 1);
            if (!containsMove(nextMoves, newMove)) nextMoves.add(new Move(row + 1, col + 1));
          }
        }
        if (col > 0) {
          if (board[row + 1][col - 1] == null) {
            Move newMove = new Move(row + 1, col - 1);
            if (!containsMove(nextMoves, newMove)) nextMoves.add(new Move(row + 1, col - 1));
          }
        }
      }
      if (col > 0) {
        if (board[row][col - 1] == null) {
          Move newMove = new Move(row, col - 1);
          if (!containsMove(nextMoves, newMove)) nextMoves.add(new Move(row, col - 1));
        }
      }
      if (col < GomokuBoard.COLS - 1) {
        if (board[row][col + 1] == null) {
          Move newMove = new Move(row, col + 1);
          if (!containsMove(nextMoves, newMove)) nextMoves.add(new Move(row, col + 1));
        }
      }
    }
    return nextMoves;
  }

  public boolean containsMove(ArrayList<Move> moves, Move m) {
    for (int i = 0; i < moves.size(); i++) {
      if (moves.get(i).row == m.row && moves.get(i).col == m.col) return true;
    }
    return false;
  }

  public ArrayList<String> getTileLocs(Color[][] board) {
    ArrayList<String> locs = new ArrayList<>();
    for (int i = 0; i < GomokuBoard.ROWS; i++) {
      for (int j = 0; j < GomokuBoard.COLS; j++) {
        if (board[i][j] != null) {
          locs.add("" + i + " " + j);
        }
      }
    }
    return locs;
  }

  public boolean isEmpty(Color[][] board) { //returns true if the board is empty
		for (int i = 0; i < GomokuBoard.ROWS; i++) {
			for (int j = 0; j < GomokuBoard.COLS; j++) {
				if (board[i][j] != null) {
					return false;
				}
			}
		}
		return true;
	}

  public Color[][] copyBoard(Color[][] board) { //creates a copy of the board
    Color[][] copy = new Color[GomokuBoard.ROWS][GomokuBoard.COLS];
    for (int row = 0; row < GomokuBoard.ROWS; row++) {
			for (int col = 0; col < GomokuBoard.COLS; col++) {
				copy[row][col] = board[row][col];
      }
    }
    return copy;
  }
}
