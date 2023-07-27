package nfa;

import simple.Pair;

public class NfaConstructor {
    private NfaManager nfaManager = null;

    public NfaConstructor() {
        nfaManager = new NfaManager();
    } //생성자

    public Pair constructStarClosure(Pair pairIn) { //NFA 쌍을 받아서 closure 연산 처리
        Pair pairOut = new Pair();
        pairOut.startNode = nfaManager.newNfa();
        pairOut.endNode = nfaManager.newNfa();

        pairOut.startNode.next = pairIn.startNode;
        pairIn.endNode.next = pairOut.endNode;

        pairOut.startNode.next2 = pairOut.endNode;
        pairIn.endNode.next2 = pairIn.startNode;

        pairIn.startNode = pairOut.startNode;
        pairIn.endNode = pairOut.endNode;

        return pairOut; //새로운 상태 반환
    }

    public Pair constructNfaForSingleCharacter(char c) {//문자 포함하는 NFA 쌍 생성
        //연결
        Pair pairOut = new Pair();
        pairOut.startNode = nfaManager.newNfa();
        pairOut.endNode = nfaManager.newNfa();
        pairOut.startNode.next = pairOut.endNode;
        pairOut.startNode.setEdge(c);

        return pairOut;
    }

    public Pair constructNfaForOR(Pair left, Pair right) { //or연산 수행
        Pair pair = new Pair();
        pair.startNode = nfaManager.newNfa();
        pair.endNode = nfaManager.newNfa();

        pair.startNode.next = left.startNode;
        pair.startNode.next2 = right.startNode;

        left.endNode.next = pair.endNode;
        right.endNode.next = pair.endNode;

        return pair;
    }

    public Pair constructNfaForConnector(Pair left, Pair right) { //Connector 연산 수행
        Pair pairOut = new Pair();
        pairOut.startNode = left.startNode;
        pairOut.endNode = right.endNode;

        left.endNode.next = right.startNode;

        return pairOut;
    }
}
