package simple;

public class Cell {
    public static final int EPSILON = -1;
    public static final int EMPTY = -2;

    private int edge;

    public int getEdge() {
        return edge;
    }

    public void setEdge(int type) {
        edge = type;
    }

    //다음 상태
    public Cell next;
    public Cell next2;
    private int state; //상태
    private boolean visited = false; //방문 여부

    public void setVisited() {
        visited = true;
    }

    public void setUnVisited() {
        visited = false;
    }

    public boolean isVisited() {
        return visited;
    }

    //상태 번호
    public void setState(int num) {
        state = num;
    }

    public int getState() {
        return state;
    }


    public void clearState() { //상태 초기화
        next = next2 = null;
        state = -1;
    }

    @Override
    public String toString() {
        return (char) edge + " " + state + "" + isVisited();
    }
}
