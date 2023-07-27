package nfa;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import simple.Cell;
import simple.Pair;

public class NFA {

    private int restate = 0;

    private String re;
    private String reJoined;
    private String rePostfix;

    private String[] letter;
    private Pair pair;

    private Vector<Integer> stateset = new Vector<Integer>();
    private Vector<String> terminalset = new Vector<>();
    private Vector<Integer> finalset = new Vector<>();

    public NFA(String re) {
        this.re = re;
        reJoined = null;
        rePostfix = null;
        Set<Character> temp = new HashSet<>();
        for (int i = 0; i < this.re.length(); i++) {
            if (is_letterOrdigit(this.re.charAt(i))) {
                temp.add(this.re.charAt(i));
            }
        }
        letter = new String[temp.size() + 2];
        Object[] tempObj = temp.toArray();
        int i = 0;
        letter[i] = "";
        for (; i < tempObj.length; i++) {
            letter[i + 1] = (char) tempObj[i] + "";
        }
        letter[i + 1] = "EPSILON";

        for (String string : letter) {
            terminalset.add(string);
        }
        terminalset.remove(0);
    }

    public Pair getPair() {
        return pair;
    }

    public String[] getLetter() {
        return letter;
    }

    public void setPair(Pair pair) {
        this.pair = pair;
    }

    public String adddot() { //표현식에 연결기호 '.' 추가
        int length = re.length(); //길이 확인
        if (length == 1) { //길이가 1이면 그대로 반환
            reJoined = re;
            return re;
        }
        int return_string_length = 0;
        char return_string[] = new char[2 * length + 2]; //새로운 문자열 생성
        char first, second = '0';
        for (int i = 0; i < length - 1; i++) { //문자열 순회
            first = re.charAt(i);
            second = re.charAt(i + 1);
            return_string[return_string_length++] = first;
            if (first != '(' && first != '+' && is_letterOrdigit(second)) { //문자 확인 후 . 추가
                return_string[return_string_length++] = '.';
            } else if (second == '(' && first != '+' && first != '(') {
                return_string[return_string_length++] = '.';
            }
        }
        return_string[return_string_length++] = second;
        String rString = new String(return_string, 0, return_string_length);
        reJoined = rString;
        return rString;
    }

    private boolean is_letterOrdigit(char check) { //문자 확인
        {
            if (check >= 'a' && check <= 'z' || check >= 'A' && check <= 'Z' || check >= '0' && check <= '9')
                return true;
            return false;
        }
    }

    public String postfix() { //문자 postfix 형태 변환
        reJoined = reJoined + "#";
        Stack<Character> s = new Stack<>();
        char ch = '#', ch1, op;
        s.push(ch);
        String out_string = ""; //출력할 문자열 초기화
        int read_location = 0;
        ch = reJoined.charAt(read_location++);
        while (!s.empty()) {
            if (is_letterOrdigit(ch)) { //문자가 유효한지 확인
                out_string = out_string + ch;
                ch = reJoined.charAt(read_location++);
            } else { //연산자일 경우 우선순위 비교 후 스택에서 연산자 꺼냄
                ch1 = s.peek();
                if (isp(ch1) < icp(ch)) { // ch의 우선순위가 높을 경우
                    s.push(ch); //ch 스택에 push
                    ch = reJoined.charAt(read_location++);
                } else if (isp(ch1) > icp(ch)) { //ch1의 우선순위가 높을 경우
                    op = s.pop();
                    out_string = out_string + op; //연산저 문자열에 추가
                } else {
                    op = s.pop();
                    if (op == '(')
                        ch = reJoined.charAt(read_location++);
                }
            }
        }
        rePostfix = out_string;
        return out_string;
    }

    private int isp(char c) { //연산자 우선순위
        switch (c) {
            case '#':
                return 0;
            case '(':
                return 1;
            case '*':
                return 7;
            case '+':
                return 3;
            case '.':
                return 5;
            case ')':
                return 8;
        }
        return -1;
    }

    private int icp(char c) { //연산자 우선순위
        switch (c) {
            case '#':
                return 0;
            case '(':
                return 8;
            case '*':
                return 6;
            case '+':
                return 2;
            case '.':
                return 4;
            case ')':
                return 1;
        }
        return -1;
    }

    public void re2nfa() { //postfix 기반으로 ε-NFA 생성
        pair = new Pair();
        Pair temp = new Pair();
        Pair right, left;
        NfaConstructor constructor = new NfaConstructor();
        char ch[] = rePostfix.toCharArray(); //postfix 문자열 변환
        Stack<Pair> stack = new Stack<>();
        for (char c : ch) {
            switch (c) {//연산자일 경우 스택에서 피연산자 가져옴
                case '+':
                    right = stack.pop();
                    left = stack.pop();
                    pair = constructor.constructNfaForOR(left, right);
                    stack.push(pair);
                    break;
                case '*':
                    temp = stack.pop();
                    pair = constructor.constructStarClosure(temp);
                    stack.push(pair);
                    break;
                case '.':
                    right = stack.pop();
                    left = stack.pop();
                    pair = constructor.constructNfaForConnector(left, right);
                    stack.push(pair);
                    break;
                default: //아닐 경우 스택에 푸시
                    pair = constructor.constructNfaForSingleCharacter(c);
                    stack.push(pair);
                    break;
            }
        }
    }

    public void print() { //출력
        restate(this.pair.startNode); //모든 노드 방문
        revisit(this.pair.startNode); //다시 나중에 방문할 수 있도록 초기화
        System.out.println("--------E-NFA--------");

        System.out.print("TerminalSet = {");
        for (int i = 0; i < terminalset.size() - 1; i++) {
            System.out.print(terminalset.get(i) + ",");
        }
        System.out.println("}");
        System.out.println("DeltaFunctions = {");
        printNfa(this.pair.startNode);
        System.out.println("}");
        System.out.print("StateSet = {");
        for (int i = 0; i < stateset.size(); i++) {
            System.out.print("q" + stateset.get(i) + ",");
        }
        System.out.println("}");

        revisit(this.pair.startNode);

        System.out.println("Start State = " + ("q" + this.pair.startNode.getState()));
        System.out.print("FinalStateSet = {");
        for (int i = 0; i < finalset.size(); i++) {
            System.out.print("q" + finalset.get(i) + ", ");
        }
        System.out.println("}");
        System.out.println("--------E-NFA--------");
    }

    private void restate(Cell startNfa) { //상태 번호 할당
        if (startNfa == null || startNfa.isVisited()) {
            return;
        }
        startNfa.setVisited();
        startNfa.setState(restate++); //상태 번호 할당
        restate(startNfa.next); //다음 상태 재귀 호출
        restate(startNfa.next2);
    }

    private void revisit(Cell startNfa) { //다시 방문할 수 있도록 초기화
        if (startNfa == null || !startNfa.isVisited()) {
            return;
        }
        startNfa.setUnVisited();
        revisit(startNfa.next);
        revisit(startNfa.next2);
    }

    private void printNfa(Cell startNfa) {//각 노트 상태 출력
        if (startNfa == null || startNfa.isVisited()) {
            return;
        }
        startNfa.setVisited();
        printNfaNode(startNfa);

        printNfa(startNfa.next);
        printNfa(startNfa.next2);
    }

    private void printNfaNode(Cell node) { //상세 정보 출력
        stateset.add(node.getState());
        if (node.next != null) {
            System.out.print("(q" + node.getState());
            if (node.getEdge() == -1) {
                if (node.next2 != null) {
                    System.out.print(",ε) = ");
                    System.out.println("{q" + node.next.getState() + ", q" + node.next2.getState() + "}");
                } else {
                    System.out.print(",ε) = ");
                    System.out.println("{q" + node.next.getState() + "}");
                }
            } else {
                int index = getindex("" + (char) node.getEdge());
                for (int i = 0; i < letter.length - 1; i++) {
                    if (i == index) {
                        System.out.print(", " + terminalset.get(i) + ") = ");
                        if (node.next2 != null)
                            System.out.println("{q" + node.next.getState() + ", q" + node.next2.getState() + "}");
                        else
                            System.out.println("{q" + node.next.getState() + "}");
                    }
                }
            }
        } else {
            finalset.add(node.getState());
        }
    }

    private int getindex(String ch) {
        for (int i = 0; i < letter.length; i++) {
            if (letter[i].equals(ch))
                return i - 1;
        }
        return -1;
    }

}
