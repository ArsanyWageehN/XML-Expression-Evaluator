package xml.expression.evaluator;

public class Stack<T> {

    private int count = -1;

    private class Node {

        private T data;
        private T dataType;
        private Node Next;

        public Node() {
            this.Next = null;
        }

        public Node(T data, T data2, Node Next) {
            this.data = data;
            this.dataType = data2;
            this.Next = Next;
        }

        public T getDataType() {
            return dataType;
        }

        public void setDataType(T dataType) {
            this.dataType = dataType;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public Node getNext() {
            return Next;
        }

        public void setNext(Node Next) {
            this.Next = Next;
        }

    }

    private Node Head;

    public Stack() {
        Head = new Node();
        count = -1;
    }

    public boolean isEmpty() {
        return (count == -1);
    }

    public int size() {
        return (count + 1);
    }

    public void push(T Data, T DataType) {
        Node data = new Node(Data, DataType, null);
        if (isEmpty()) {
            Head.setNext(data);
        } else {
            data.setNext(Head.getNext());
            Head.setNext(data);
        }
        ++count;
    }

    public T pop() {
        if (!isEmpty()) {
            T popString = Head.getNext().getData();
            count--;
            Head.setNext(Head.getNext().getNext());
            return popString;
        } else {
            return (T) "";
        }
    }

    public T top() {
        if (!isEmpty()) {
            return Head.getNext().getData();
        } else {
            return null;
        }
    }

    public void clear() {
        if (!isEmpty()) {
            for (int i = 0; i < count; i++) {
                Head.setNext(Head.getNext().getNext());
            }
            Head = null;
            count = -1;
        }
    }

}


/*
    public Stack reverse() {
        Stack st = new Stack();
        long se = this.size();
        for (int i = 0; i < se; i++) { 
            st.push(this.top());
            this.pop();
        } 
        return st;
    }
 */
