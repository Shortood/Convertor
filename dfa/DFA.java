package dfa;

import java.util.*;
import java.util.Map.Entry;

import simple.Cell;
import simple.Pair;

public class DFA {
    private Pair pair; //시작 노드 종료 노드 저장
    private String[] letter; //입력 상태
    private Map<Set<Integer>, Integer> map;//상태 집합과 상태 매핑
    private Set<Integer> tempset; //임시 상태 집합
    private Queue<Integer> queue = new LinkedList<>(); //BFS용 큐
    private List<Character[]> dfa = new ArrayList<>(); //DFA delta 저장
    private List<Character> endState = new ArrayList<>(); //종료 상태

    private int state = 'A'; //상태

    private Vector<String> terminalSet = new Vector<String>(); //terminalset
    private Vector<Character> stateSet = new Vector<Character>(); //stateset
    private Vector<Character> finalSet = new Vector<Character>();//finalset
    private Character startsym; //시작 상태

    public DFA(Pair pair, String[] letter) { //DFA 생성자
        this.pair = pair;
        this.letter = letter;
        for (int i = 1; i < letter.length - 1; i++) { //terminal set 구하기
            terminalSet.add(letter[i]);
        }
        map = new HashMap<>();
    }

    public List<Character[]> getDFA() { //상태 전이 함수 반환
        List<Character[]> redfa = new ArrayList<>();
        for (Character[] ch : dfa) {
            if (getSet(ch[0]) == null || getSet(ch[0]).isEmpty()) {
                continue;
            } else {
                Character[] newch = new Character[ch.length];
                for (int i = 0; i < ch.length; i++) {
                    if (ch[i] == null)
                        continue;
                    Set<Integer> set = getSet(ch[i]);
                    if (set == null || set.isEmpty())
                        newch[i] = null;
                    else
                        newch[i] = ch[i];
                }
                redfa.add(newch);
            }
        }
        return redfa;
    }

    public List<Character> getEndState() {
        return endState;
    }

    public String[] getLetter() {
        return letter;
    }

    public void printDFA() { //출력
        for (Entry<Set<Integer>, Integer> entry : map.entrySet()) {
            if (entry.getValue() == -1)
                continue;
            System.out.println((char) entry.getValue().intValue() + " = " + entry.getKey());
            if (isStart(entry.getKey())) {
                startsym = (char) entry.getValue().intValue();
            }
            if (isEnd(entry.getKey())) {
                finalSet.add((char) entry.getValue().intValue());
            }
        }
        System.out.print("StateSet = {");
        for (int i = 0; i < stateSet.size(); i++) {
            System.out.print(stateSet.get(i) + ", ");
        }
        System.out.println("}");
        System.out.println("StartState = " + startsym);
        System.out.print("FinalStateSet = {");
        for (int i = 0; i < finalSet.size(); i++) {
            System.out.print(finalSet.get(i) + ", ");
        }
        System.out.println("}");
        System.out.println("--------DFA--------");
    }

    private boolean isStart(Set<Integer> set) { //시작상태인지 확인
        for (Integer integer : set) {
            if (integer == pair.startNode.getState())
                return true;
        }
        return false;
    }

    private boolean isEnd(Set<Integer> set) { //종료상태인지 확인
        for (Integer integer : set) {
            if (integer == pair.endNode.getState()) {
                endState.add(new Character((char) getCharacter(set).intValue()));
                return true;
            }
        }
        return false;
    }

    public void createDFA() { //DFA 생성
        System.out.println();
        System.out.println("--------DFA--------");
        System.out.print("TerminalSet = {");
        for (int i = 0; i < terminalSet.size(); i++) {
            System.out.print(terminalSet.get(i) + ", ");
        }
        System.out.println("}");
        tempset = new HashSet<>();
        Set<Integer> start = move(pair.startNode, -1); //시작 노드에서 이동가능한 집합 가져옴
        map.put(start, state); //매핑
        queue.add(state++);
        System.out.println("DeltaFunctions = {");
        while (!queue.isEmpty()) { //bfs 사용
            Character[] dfaline = new Character[letter.length - 1];
            int character = queue.poll();
            stateSet.add((char) character);
            dfaline[0] = (char) character;
            Set<Integer> set = getSet(character);
            for (int i = 1; i < letter.length - 1; i++) {
                tempset = new HashSet<>();
                Set<Integer> midset = new HashSet<>();
                for (Integer integer : set) {
                    Cell cell = getCell(pair.startNode, integer); //해당 노드 가져옴
                    revisit();
                    if (cell == null) {
                        continue;
                    } else if ((char) cell.getEdge() == letter[i].charAt(0)) {
                        midset.add(cell.next.getState()); //이동할 수 있는 상태 추가
                    }
                }
                for (Integer integer : midset) {
                    Cell cell = getCell(pair.startNode, integer);
                    revisit();
                    move(cell, -1);
                }
                Integer c = getCharacter(tempset);
                if (c == null) { //delta 출력
                    if (tempset.isEmpty()) { //다음 상태 X
                        map.put(tempset, -1);
                        dfaline[i] = null;
                    } else {
                        System.out.print("(" + (char) character + ", " + letter[i] + ") = {");
                        queue.add(state);
                        System.out.println((char) state + "}");
                        dfaline[i] = (char) state;
                        map.put(tempset, state++);
                    }
                } else { //다음 상태 O
                    if (c == -1) {
                        dfaline[i] = null;
                    } else {
                        System.out.print("(" + (char) character + ", " + letter[i] + ") = {");
                        dfaline[i] = (char) c.intValue();
                        System.out.println((char) c.intValue() + "}");
                    }
                }
            }
            dfa.add(dfaline);
        }
        System.out.println("}");
    }

    private Set<Integer> move(Cell startNode, int i) { //주어진 노드에서 이동 가능한 상태 집합 반환
        connect(startNode, i); //상태 연결
        revisit(); //다시 재방문 할 수 있도록 함
        return tempset;
    }

    private void connect(Cell cell, int i) { //DFS 방식 주어진 노드와 입력상태를 통해 이동 가능한 상태 연결
        if (cell == null || cell.isVisited())
            return;
        cell.setVisited(); //방문한 상태로 처리
        tempset.add(cell.getState());
        if (cell.getEdge() == -1 || cell.getEdge() == i) {
            connect(cell.next, i); //해당경로와 연결
            connect(cell.next2, i);
        } else
            return;
    }

    private Cell getCell(Cell cell, int startstate) { //주어진 상태와 일치하는 노드 반환
        if (cell == null || cell.isVisited())
            return null;
        cell.setVisited();
        if (cell.getState() == startstate)
            return cell;
        if (cell.getState() > startstate)
            return null;
        Cell temp1 = getCell(cell.next, startstate);
        Cell temp2 = getCell(cell.next2, startstate);
        if (temp1 != null)
            return temp1;
        if (temp2 != null)
            return temp2;
        return null;
    }
    //상태번호 반환
    private Integer getCharacter(Set<Integer> set) {
        return map.get(set);
    }

    private Set<Integer> getSet(int character) { //상태 집합 반환
        for (Entry<Set<Integer>, Integer> m : map.entrySet()) {
            if (m.getValue() == character)
                return m.getKey();
        }
        return null;
    }

    private void revisit(Cell cell) { //재방문 할 수 있도록 처리
        if (cell == null || !cell.isVisited()) {
            return;
        }
        cell.setUnVisited();
        revisit(cell.next);
        revisit(cell.next2);
    }

    private void revisit() {
        pair.startNode.setUnVisited();
        revisit(pair.startNode.next);
        revisit(pair.startNode.next2);
    }

    @Override
    public String toString() {
        return tempset.toString();
    }
}
