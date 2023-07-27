package main;

import java.util.Scanner;

import dfa.DFA;
import mfa.Re_DFA;
import nfa.NFA;


public class Convertor {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Input a Regular Expression"); //RE 입력
        String re = in.nextLine();
        NFA nfa = new NFA(re); //NFA 객체 생성
        nfa.adddot(); //연결기호 '.' 추가
        nfa.postfix(); //postfix 형태 변환
        nfa.re2nfa(); //nfa 형태 변환
        nfa.print(); //출력

        DFA dfa = new DFA(nfa.getPair(), nfa.getLetter()); //DFA객체 생성
        dfa.createDFA(); //DFA생성
        dfa.printDFA(); //출력

        Re_DFA reDFA = new Re_DFA(dfa.getDFA(), dfa.getEndState(), dfa.getLetter()); //re-DFA 객체 생성
        reDFA.minimize(); //최소화
        reDFA.merge(); //다시 합침
        reDFA.printreDFA(); //출력
        in.close();
    }
}
