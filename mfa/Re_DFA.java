package mfa;

import java.util.*;
import java.util.Map.Entry;

public class Re_DFA {
    private List<Character[]> dfa; //기존 DFA
    private List<Character[]> re_dfa = new ArrayList<>(); //reduce DFA
    private String[] letter; //문자열
    private List<Character> endState; //종료 상태
    private Set<Set<Character>> totalSet = new HashSet<>(); //상태 집합

    private Map<Character, Character> map = new HashMap<>(); //상태 병합
    private Vector<String> terminalset = new Vector<String>(); //terminalSet
    private Vector<Character> stateSet = new Vector<Character>(); //stateSet

    public Re_DFA(List<Character[]> dfa, List<Character> endState, String[] letter) { //생성자
        this.dfa = dfa;
        this.endState = endState;
        this.letter = letter;

        for (int i = 1; i < letter.length - 1; i++) { //terminal Set 저장
            terminalset.add(letter[i]);
        }
    }

    public void minimize() { //최소화
        init(totalSet); //초기화
        int count = 0;
        while (true) {
            if (count == totalSet.size())
                break;
            else
                count = 0;
            Set<Set<Character>> copyOfTotalSet = new HashSet<>(totalSet);
            for (Set<Character> set : copyOfTotalSet) {
                if (isIndivisible(set)) { //최소화가 가능하지 않다면
                    count++;
                    continue;
                } else { //가능하다면
                    minimize(set);
                }
            }
        }
    }

    private void minimize(Set<Character> state) { //상태 최소화
        totalSet.remove(state);
        Map<String, String> map = new HashMap<>();
        for (Character character : state) {
            String aString = "";
            for (int i = 1; i < letter.length - 1; i++) {
                aString += move(character, letter[i].charAt(0)) + "";
            }
            String tempset = map.get(aString); //생성된 문자열
            if (tempset == null) { //map에 없다면
                map.put(aString, character + ""); //새로운 문자열 + 현재 상태 추가
            } else { //있다면 상태 추가
                tempset += character;
                map.put(aString, tempset);
            }
        }
        for (String str : map.values()) { //상태집합 totalSet에 추가
            Set<Character> set = new HashSet<>();
            for (int i = 0; i < str.length(); i++)
                set.add(str.charAt(i));
            totalSet.add(set);
        }
    }

    private boolean inTotalSet(Set<Character> temp) { //TotalSEt에 포함되는지 확인
        if (temp.isEmpty())
            return true;
        Set<Integer> indexs = new HashSet<>();
        for (Character character : temp) {
            indexs.add(getSetNumber(character));
        }
        return indexs.size() == 1;
    }

    private int getSetNumber(Character character) { //상태 집합 번호
        int i = 0;
        for (Set<Character> a : totalSet) {
            for (Character b : a) {
                if (b.equals(character))
                    return i;
            }
            i++;
        }
        return -1;
    }

    private void init(Set<Set<Character>> totalSet) { //totalSet 초기화
        Set<Character> terminal = new HashSet<>();
        Set<Character> nonTerminal = new HashSet<>();
        for (Character[] characters : dfa) {
            if (isEndState(characters[0])) //터미널과 논터미널 구분
                terminal.add(characters[0]);
            else
                nonTerminal.add(characters[0]);
        }
        totalSet.add(nonTerminal);
        totalSet.add(terminal);
    }

    private boolean isEndState(Character character) { //종료 상태인지 확인
        for (Character state : endState) {
            if (state.equals(character))
                return true;
        }
        return false;
    }

    private boolean isIndivisible(Set<Character> set) { //더이상 최소화 되지 않는지 확인
        if (set.size() == 1)
            return true;
        else {
            for (int i = 1; i < letter.length - 1; i++) {
                Set<Character> temp = new HashSet<>();
                for (Character c : set) {
                    temp.add(move(c, letter[i].charAt(0)));
                }
                if (inTotalSet(temp))
                    continue;
                else {
                    return false;
                }
            }
        }
        return true;
    }

    public void printreDFA() { //출력
        System.out.println("--------RE-DFA--------");
        System.out.print("TerminalSet = {");
        for (int i = 0; i < terminalset.size(); i++) {
            System.out.print(terminalset.get(i) + ", ");
        }
        System.out.println("}");
        System.out.println("DeltaFunctions = {");
        for (Character[] characters : re_dfa) {
            int cnt = 0;
            Character tempchar = characters[0];
            for (Character character : characters) {
                if (cnt == 0) {
                    stateSet.add(character);
                    cnt++;
                } else {
                    if (character != null)
                        System.out.println("(" + tempchar + ", " + letter[cnt] + ") = {" + character + "}");
                    cnt++;
                }
            }
        }

        System.out.print("StateSet = {");
        for (int i = 0; i < stateSet.size(); i++) {
            System.out.print(stateSet.get(i) + ", ");
        }
        System.out.println("}");
        System.out.println("StartState: [A]");
        System.out.println("FinalStateSet: " + endState);
        System.out.println("--------RE-DFA--------");
    }

    public void merge() { //상태 병합
        for (Set<Character> characters : totalSet) { // 각 상태 집합 순회
            if (characters.size() == 1) //크기가 1이면 병합 필요없음
                continue;
            else {
                int i = 0;
                char key = 0;
                for (Character ch : characters) {
                    if (i++ == 0)//첫번째 상태를 key
                        key = ch;
                    else //나머지와 매핑
                        map.put(ch, key);
                }
            }
        }
        List<Character[]> tempMFA = new ArrayList<>();
        for (Character[] characters : dfa) {
            if (ignore(characters[0])) { //무시해도 되는 상태라면
                endState.remove(characters[0]); //삭제
                continue;
            } else {//새로운 문자열에 추가
                Character[] newchar = new Character[characters.length];
                int i = 0;
                for (Character ch : characters) {
                    if (needReplace(ch))
                        newchar[i] = map.get(ch);
                    else {
                        newchar[i] = ch;
                    }
                    i++;
                }
                tempMFA.add(newchar);
            }
        }
        List<Character> finalstate = new ArrayList<>();
        for (Character[] ch : tempMFA) { //만들어진 문자열을 이용
            if (finalstate.contains(ch[0])) //이미 있다면
                continue;
            else { //없다면 추가
                finalstate.add(ch[0]);
                re_dfa.add(ch); //변환된 DFA 생성
            }
        }

    }

    private boolean needReplace(Character ch) { //상태가 대체되어야 하는지 확인
        Character value = map.get(ch);
        return value != null;
    }


    private boolean ignore(Character character) { //무시해야하는지 확인
        for (Entry<Character, Character> m : map.entrySet()) {
            if (m.getKey().equals(character))
                return true;
        }
        return false;
    }

    private Character move(Character oriState, char input) { //상태 이동
        for (Character[] characters : dfa) {
            if (characters[0] != oriState)
                continue;
            else {
                int index = getIndex(input);
                return characters[index] == null ? null : characters[index];
            }
        }
        return null;
    }

    private int getIndex(char input) { //인덱스 반환
        for (int i = 1; i < letter.length - 1; i++) {
            if (letter[i].charAt(0) == input)
                return i;
        }
        return -1;
    }

}
