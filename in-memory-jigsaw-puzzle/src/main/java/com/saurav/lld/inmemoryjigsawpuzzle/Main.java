package com.saurav.lld.inmemoryjigsawpuzzle;

import java.util.List;

/**
 * Design and implement an in-memory jigsaw puzzle system.

 *  *The system stores multiple rectangular jigsaw puzzles. Each puzzle consists
 * of unordered pieces. Each piece has exactly four edges: top, right, bottom,
 * and left. Each edge can be one of three types:

 *  *FLAT HOLE TAB

 *  *A flat edge represents the outer boundary of the puzzle. A tab edge may
 * connect to a hole edge.

 *  *Assume the following API is provided:

 *  *boolean isMatch(Edge edge1, Edge edge2);

 *  *You do not know how isMatch is implemented.

 *  *Design and implement an approach for reconstructing the puzzle layout.
 *
 *
 * Entry point for in-memory-jigsaw-puzzle. Add domain types in this package (or
 * subpackages), not in the default package.
 *
 * Design and implement Puzzle

 *  *enum: edgeType: FLAT HOLE TAB
 *
 * Edge type:
 *
 * Piece List<Edge>(4): top, right, bottom, left

 *  *Puzzle List<Piece>
 *
 *
 *
 */
enum EdgeType {
    FLAT, HOLE, TAB
};

class Edge {

    EdgeType type;

    Edge(EdgeType _type) {
        this.type = _type;
    }
}

class Piece {

    List<Edge> edges;

    Piece(List<Edge> _edges) {
        this.edges = _edges;
    }
}

class Puzzle {

    List<Piece> pieces;

    Puzzle(List<Piece> _pieces) {
        this.pieces = _pieces;
    }

    // 3 => 
    // 4 => 2*2, 4*1, 1*4
    // a piece => 3-edge flat => single column or single row
    // top, left, right = flat -> single column puzzle
    // 
    // 5*4 => top,left -[t]- top,right
    // b, l -[b]- b,r
    // 
    constructPuzzle() {
        // columns, rows
        Integer topCount = 0, leftCount = 0;

        for (int i = 0; i < this.pieces.size(); i++) {
            if (pieces[i][0].type == EdgeType.FLAT) { // top
                topCount++;
            }
            if (pieces[i][1].type == EdgeType.FLAT) { // left
                leftCount++;
            }
        }
        List<List<Piece>> puzzleGrid (topCount, List<Piece>(leftCount
        ));

        // if top-left

        // boolean isMatch(Edge edge1, Edge edge2);
        // List<Edge>(4): top, right, bottom, left
        for (int i = 0; i < this.pieces.size(); i++) {
            if (pieces[i][3].type == EdgeType.FLAT && piece[i][0] == EdgeType.FLAT) { // left-top
                puzzleGrid[0][0] = piece[i];
            }
            if (pieces[i][1].type == EdgeType.FLAT && piece[i][0] == EdgeType.FLAT) { // top-right
                puzzleGrid[0][topCount - 1] = piece[i];
            }

            if (pieces[i][3].type == EdgeType.FLAT && piece[i][2] == EdgeType.FLAT) { // left-bottom
                puzzleGrid[topCount - 1][0] = piece[i];
            }

        }

    }

    // reconstruct puzzle from piece
    List<List<Piece>> puzzleLayout;

    boolean move(Piece p) { //p
        if (p.edges[0].type == EdgeType.FLAT && p.edges[1].type == EdgeType.FLAT) { // top right

        }
    }

}

public class Main {

    public static void main(String[] args) {
        System.out.println("in-memory-jigsaw-puzzle ready.");

        Edge e1 = new Edge(EdgeType.FLAT);

        List<Piece> p1 = {new Piece(e1,)};
        Puzzle puzzle = new Puzzle();

    }
}
