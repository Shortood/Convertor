package nfa;

import java.util.Stack;

import simple.Cell;

public class NfaManager {
    private final int NFA_MAX = 256; 
    private Cell[] nfaStatesArr = null;
    private Stack<Cell> nfaStack = null;
    private int nextAlloc = 0; 
    private int nfaStates = 0; 
    
    public NfaManager()  { //생성자
    	nfaStatesArr = new Cell[NFA_MAX];
    	for (int i = 0; i < NFA_MAX; i++) {
    		nfaStatesArr[i] = new Cell();
    	}
    	
    	nfaStack = new Stack<Cell>();
    	
    }
    
    public Cell newNfa()  { //새로운 NFA 상태 반환
    	Cell nfa = null;
    	if (nfaStack.size() > 0) { //스택에서 상태 가져옴
    		nfa = nfaStack.pop();
    	}
    	else { //스택이 비었으면 배열에서 가졍ㅁ
    		nfa = nfaStatesArr[nextAlloc];
    		nextAlloc++;
    	}
    	
    	nfa.clearState();
    	nfa.setState(nfaStates++);
    	nfa.setEdge(Cell.EPSILON);
    	
    	return nfa;
    }
}
